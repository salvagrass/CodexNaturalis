package it.polimi.ingsw.controller.serverfunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.model.gameplane.CommonGround;
import it.polimi.ingsw.model.gameplane.Player;
import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.card.GoldCard;
import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.ObjectiveSerializeLogic;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.util.Symbol;
import it.polimi.ingsw.util.exception.InvalidDrawException;
import it.polimi.ingsw.util.exception.InvalidPlacementException;
import it.polimi.ingsw.util.exception.InvalidSelectionException;
import it.polimi.ingsw.util.json.CardSerializeLogic;
import it.polimi.ingsw.util.json.SaveData;
import it.polimi.ingsw.view.coreview.CardView;
import it.polimi.ingsw.view.coreview.ObjectiveView;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Class that manages the flow of the game.
 */
public class GameLogic {
    private final String id;
    private boolean cardPlaced, cardDrawn;
    private CommonGround ground;
    private Comm currPlayer;
    private SetUpLogic setUp;
    private boolean endGame = false;

    private final CentralServer server;
    private final List<Comm> turnOrder;
    private final Map<Comm, Player> players;
    private final ExecutorService executor;

    /**
     * Constructor for the GameLogic.
     *
     * @param server The server that created it.
     * @param size The number of players in the game.
     */
    public GameLogic(CentralServer server, String id, int size) {
        this.turnOrder = new ArrayList<>();
        this.executor = Executors.newFixedThreadPool(size);
        this.setUp = new SetUpLogic(this, size, this.turnOrder);
        this.server = server;
        this.id = id;
        players = new ConcurrentHashMap<>();
        try{
            ground = new CommonGround();
        } catch (IOException e) {
            //TODO log file
            System.out.println("file corrupted");
        }
    }

    public GameLogic(CentralServer server, String id, CommonGround ground, Map<Comm,Player> players,List<Comm> turnOrder, Comm currPlayer){
        this.id = id;
        this.server = server;
        this.ground = ground;
        this.players = players;
        this.turnOrder = turnOrder;
        this.currPlayer = currPlayer;
        this.executor = Executors.newFixedThreadPool(turnOrder.size());
        for(Comm comm: turnOrder){
            comm.setGame(this);
        }
    }

    /**
     * Handles the joining of a player and calls the gameStart method
     * when all the players are in the "ready" state.
     *
     * @param comm The connection to the specific client.
     */
    public void addPlayer(Comm comm) {
        this.executor.submit(()->heartBeat(comm));
        Player player = setUp.addPlayer(comm, ground);
        players.put(comm, player);
        setUp.setReady(comm);
        synchronized (this.setUp) {
            Set<String> names = players.values().stream().map(Player::getNickname).collect(Collectors.toSet());
            Comm.sendBroadcast(MsgCreator.joinedMsg(names), turnOrder);
            if (this.currPlayer == null) {
                this.currPlayer = comm;
                setUp.waitReady();
                gameStart();
            } else {
                this.setUp.notifyAll();
            }
        }
    }


    public void loadGame(){
        int points,i;
        CardView[] loadedHand;
        Player player;
        List<String> names = this.turnOrder.stream()
                .map(x->this.players.get(x).getNickname())
                .toList();
        Comm.sendBroadcast(MsgCreator.turnOrderMsg(names),this.turnOrder);
        createGroundView();
        for(Comm comm: this.turnOrder){
            player = this.players.get(comm);
            points = player.getPoints();
            loadedHand = new CardView[player.getHand().length];
            for(i=0;i<player.getHand().length;i++){
                loadedHand[i] = new CardView(player.getHand()[i]);
            }
            sendMsg(comm,MsgCreator.createHandViewMsg(loadedHand));
            sendMsg(comm,MsgCreator.loadHiddenObjMsg(player.getSecretObjective()));
            Comm.sendBroadcast(MsgCreator.updPointsMsg(this.turnOrder.indexOf(comm),points),this.turnOrder);
            try {
                Comm.sendBroadcast(MsgCreator.loadBoardMsg(this.id,this.turnOrder.indexOf(comm)),this.turnOrder);
            } catch (IOException e) {
                //TODO HANDLE THIS
            }
        }
        gameFlow();
    }

    /**
     * Performs all the starting operations nedeed for the game
     */
    private void gameStart() {
        for (int i = 0; i < players.size(); i++) {
            DrawableCard[] hand = players.get(this.turnOrder.get(i)).drawFirstThree(ground);
            CardView[] handView = new CardView[hand.length];
            for (int j = 0; j < hand.length; j++) {
                handView[j] = new CardView(hand[j]);
            }
            sendMsg(this.turnOrder.get(i), MsgCreator.createHandViewMsg(handView));
        }

        createGroundView();
        gameFlow();
    }

    /**
     * Handles the turns of the game until the end
     */
    private void gameFlow(){
        Comm.sendBroadcast(MsgCreator.changePhaseMsg(Phase.GAME), turnOrder);
        for(Comm comm:turnOrder){
            new Thread(comm::waitMsg).start();
        }
        while (!this.endGame){
            fullTurn();
        }
        Comm.sendBroadcast(MsgCreator.changePhaseMsg(Phase.LASTTURN), this.turnOrder);
        fullTurn();

        for (Comm comm : turnOrder) {
            players.get(comm).addObjectivesScore(this.ground);
            Comm.sendBroadcast(MsgCreator.updPointsMsg(turnOrder.indexOf(comm), players.get(comm).getPoints()),
                    this.turnOrder);
        }
        Comm.sendBroadcast(MsgCreator.changePhaseMsg(Phase.ENDGAME), this.turnOrder);
        declareWinner();
        shutdown();
    }

    /**
     * Declares the winner of the game and broadcasts the leaderboard to all players.
     * The leaderboard is determined based on player points and completed objectives.
     * The players are sorted in ascending order of points, with tiebreakers based on the number of completed objectives.
     * Each player's position in the leaderboard is determined by their relative ranking in the sorted list.
     * The constructed leaderboard is then broadcasted to all players in the game.
     * Example of leaderboard format in the broadcast message:
     * LEADERBOARD;{"player1":1,"player2":2,"player3":2}
     */
    private void declareWinner() {
        Map<String, Integer> leaderboard = new HashMap<>();
        int pos = 1;
        List<Player> ls = this.players.values().stream()
                .sorted(Comparator.comparingInt(Player::getPoints).
                        thenComparingInt(Player::getCompletedObjs).reversed())
                .toList();
        for(int i = 0; i < ls.size() - 1; i++){
            leaderboard.put(ls.get(i).getNickname(), pos);
            if(!(ls.get(i).getPoints() == ls.get(i+1).getPoints() &&
                    ls.get(i).getCompletedObjs() == ls.get(i+1).getCompletedObjs())){
                pos++;
            }
        }
        leaderboard.put(ls.getLast().getNickname(), pos);
        Comm.sendBroadcast(MsgCreator.leadeboardMsg(leaderboard), turnOrder);
    }

    /**
     * Calls the playTurn method for every player in the game.
     *
     */
    private void fullTurn() {
        final int winPoints = 20;
        for (int i = 0; i < players.size(); i++) {
            this.currPlayer = this.turnOrder.get(i);
            this.cardPlaced = false;
            this.cardDrawn = false;
            playTurn(this.turnOrder.get(i));
            try {
                this.endGame = this.endGame || players.get(this.turnOrder.get(i)).getPoints() >= winPoints ||
                    (ground.isEmpty(0) && ground.isEmpty(1));
            } catch (InvalidSelectionException e) {
                System.out.println("can't occur");
            }
        }
    }

    /**
     * Informs a client of the current state of a turn and waits for them to complete it.
     *
     * @param comm The connection to the specific client.
     */
    private void playTurn(Comm comm) {
        String name = this.players.get(comm).getNickname();
        Comm.sendBroadcast(MsgCreator.currPlayerMsg(name),this.turnOrder);
        synchronized(this) {
            while (!this.cardPlaced){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e); //TODO: HANDLE EXCEPTION
                }
            }
        }
        synchronized (this) {
            while (!this.cardDrawn) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e); //TODO: HANDLE EXCEPTION
                }
            }
        }

        Gson gson = new GsonBuilder().
                registerTypeHierarchyAdapter(Card.class,new CardSerializeLogic()).
                registerTypeHierarchyAdapter(Objective.class,new ObjectiveSerializeLogic()).
                setPrettyPrinting().
                create();
        String jsonGame =
                gson.toJson(new SaveData(this.id,this.ground,this.endGame,this.players,this.turnOrder,this.currPlayer));
        this.server.saveGame(this.id,jsonGame);
    }

    /**
     * Handles the placement process.
     *
     * @param comm The connection to the specific client.
     * @param msg A string containing instructions for the placement of the card.
     */
    public void placeCard(Comm comm, String msg) {
        String[] s = msg.split(";");
        //TODO REVISE THIS
        int idx = Integer.parseInt(s[1]);
        int x = Integer.parseInt(s[2]);
        int y = Integer.parseInt(s[3]);
        boolean face = s[4].equals("true");
        if(currPlayer != comm || this.cardPlaced) {
            sendMsg(comm, MsgCreator.confirmMsg(false,"Not your turn"));
            return;
        }
        try {
            DrawableCard card = this.players.get(comm).placeCard(idx,x,y,face);
            updBoardView(card, x, y, comm);
            synchronized(this) {
                this.cardPlaced = true;
                sendMsg(comm, MsgCreator.confirmMsg(true,"Successful placement"));
                this.notifyAll();
            }
        } catch (InvalidPlacementException e) {
            sendMsg(comm, MsgCreator.confirmMsg(false,"Invalid placement"));
        }
    }

    /**
     * Handles the draw process.
     *
     * @param comm The connection to the specific client.
     * @param msg A string containing the card to be drawn.
     */
    public void drawCard(Comm comm, String msg) {
        String[] s = msg.split(";");
        String cmd = "";
        int idx;
        if(currPlayer != comm || !this.cardPlaced) {
            sendMsg(comm, MsgCreator.confirmMsg(false,"Not your turn"));
            return;
        }
        try {
            DrawableCard drawnCard  = this.players.get(comm).draw(ground, Integer.parseInt(s[1]));
            sendMsg(comm, MsgCreator.updHandViewMsg(new CardView(drawnCard)));

            idx = Integer.parseInt(s[1]);
            if(idx == 1 || idx == 2){
                cmd = MsgCreator.updGroundViewMsg(idx, new CardView(ground.getGroundResourceCards()[idx-1]));
                idx = 0;
            } else if (idx == 4 || idx == 5) {
                cmd = MsgCreator.updGroundViewMsg(idx, new CardView(this.ground.getGroundGoldCards()[idx-4]));
                idx = 3;
            }
            CardView back;
            if (idx == 0) {
                back = createBackView(true);
            } else {
                back = createBackView(false);
            }
            cmd = MsgCreator.cat(cmd, MsgCreator.updGroundViewMsg(idx, back));
            Comm.sendBroadcast(cmd, turnOrder);
            synchronized(this){
                this.cardDrawn = true;
                sendMsg(comm, MsgCreator.confirmMsg(true,"Draw successful"));
                this.notifyAll();
            }
        } catch (InvalidDrawException | InvalidSelectionException e) {
            sendMsg(comm, MsgCreator.confirmMsg(false,"Invalid draw"));
        }
    }

    public void chat(String msg){
        String receiver = msg.split(";")[1];
        String sender = msg.split(";")[2];
        Comm.sendBroadcast(msg,
                receiver.equals("All") ?
                        this.turnOrder :
                        this.players.keySet().stream().
                                filter(x -> this.players.get(x).getNickname().equals(receiver) ||
                                        this.players.get(x).getNickname().equals(sender)).toList());
    }

    /**
     * Adds a communication object to the SetUpLogic record.
     *
     * @param comm The connection to the specific client.
     */
    public void addRecord(Comm comm){
        setUp.addRecord(comm);
    }

    /**
     * Checks if the game is already full.
     *
     * @return True if the game is full, False otherwise.
     */
    public boolean isFull(){
        return setUp.isFull();
    }

    /**
     * Sends an update on the state of a player's board to every other player.
     *
     * @param newCard The card that has been placed.
     * @param x the x-coordinate of the placed card
     * @param y the y-coordinate of the place card
     * @param currPlayer The player who placed the card.
     */
    private void updBoardView(DrawableCard newCard, int x, int y, Comm currPlayer) {
        Player player = players.get(currPlayer);
        String updBoardMsg = MsgCreator.updBoardMsg(turnOrder.indexOf(currPlayer),x,y,new CardView(newCard),player.getInventory());
        String updPointsMsg = MsgCreator.updPointsMsg(turnOrder.indexOf(currPlayer),player.getPoints());
        Comm.sendBroadcast(MsgCreator.cat(updBoardMsg,updPointsMsg), turnOrder);
    }

    /**
     * Sends the specified message to the specified client.
     *
     * @param receiver The client to which the message is sent.
     * @param msg The message to be sent.
     */
    void sendMsg(Comm receiver, String msg) {
        try {
            receiver.sendMsg(msg);
        } catch (Exception e) { //TODO RemoteException
            //TODO log file
            shutdown();
        }
    }

    /**
     * Retrieves a message from the specified communication channel.
     *
     * @param sender the communication channel from which to retrieve the message.
     * @return the received message, or `null` if an error occurs.
     */
    String getMsg(Comm sender) {
        try{
            return sender.getMsg();
        } catch (IllegalStateException e) {
            //TODO log file
            shutdown();
            return null;
        }
    }

    /**
     * Closes connections and shuts down the client.
     */
    private void shutdown() {
        Comm.sendBroadcast(MsgCreator.changePhaseMsg(Phase.ENDGAME), this.turnOrder);
        for (Comm comm : this.turnOrder) {
            this.server.submitClient(comm);
        }
        this.server.closeGame(this.id);
    }

    /**
     * Sets up the game logic by sending broadcast messages to update the ground view.
     * This method sends multiple broadcast messages containing information about the game state,
     * such as the resources and gold cards on the ground, as well as common objectives.
     * Each broadcast message is formatted as follows:
     * UPDGROUNDVIEW;<index>;<data>
     * Where:
     * - <index>: The index indicating the type of update.
     * - <data>: The data associated with the update.
     */
    private void createGroundView() {
        CardView[] resCards = {
            createBackView(true),
            new CardView(this.ground.getGroundResourceCards()[0]),
            new CardView(this.ground.getGroundResourceCards()[1]),
        };
        CardView[] goldCards = {
                createBackView(false),
                new CardView(this.ground.getGroundGoldCards()[0]),
                new CardView(this.ground.getGroundGoldCards()[1])
        };
        ObjectiveView[] objs = {
            new ObjectiveView(this.ground.getCommonObjectives()[0]),
            new ObjectiveView(this.ground.getCommonObjectives()[1])            
        };
        String msg = MsgCreator.createGroundViewMsg(resCards,goldCards,objs);
        Comm.sendBroadcast(msg, turnOrder);
    }

    public CardView createBackView(boolean flag) {
        return flag ? new CardView(new ResourceCard(
                new Symbol[] { Symbol.NOTAVAILABLE, Symbol.NOTAVAILABLE, Symbol.NOTAVAILABLE, Symbol.NOTAVAILABLE },
                new Symbol[] {
                        this.ground.peekResDeck() },
                0, "backView"))
                : new CardView(new GoldCard(
                        new Symbol[] { Symbol.NOTAVAILABLE, Symbol.NOTAVAILABLE, Symbol.NOTAVAILABLE,
                                Symbol.NOTAVAILABLE },
                        new Symbol[] {
                                this.ground.peekGoldDeck() },
                        new HashMap<>(), 0, "backView"));
    }

    private void heartBeat(Comm comm) {
        while (!endGame) {
            sendMsg(comm, "PING");
            try{
                Thread.sleep(20000);
            }catch(InterruptedException e){
                throw new RuntimeException();
            }
        }
    }
}
