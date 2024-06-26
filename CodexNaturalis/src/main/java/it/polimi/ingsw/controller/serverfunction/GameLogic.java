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
import it.polimi.ingsw.util.ProjectLogger;
import it.polimi.ingsw.util.json.ObjectiveSerializeLogic;
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
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Class that manages the flow of the game.
 */
public class GameLogic implements CrashHandler {
    private final String id;
    private boolean cardPlaced, cardDrawn;
    private final CommonGround ground;
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
        this.setUp = new SetUpLogic(size, this.turnOrder);
        this.server = server;
        this.id = id;
        players = new ConcurrentHashMap<>();
        try{
            ground = new CommonGround();
        } catch (IOException e) {
            new ProjectLogger(getClass().getSimpleName()).
                    log("logs/serverLog.txt",Level.WARNING,"Can't load selected game",e);
            //TODO MAYBE INFORM THE PLAYERS
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes the game logic.
     *
     * @param server     The central server managing the game.
     * @param id         The unique identifier for the game.
     * @param ground     The common ground shared by players.
     * @param players    A map of communication channels to player objects.
     * @param turnOrder  The list of communication channels representing the turn order.
     * @param currPlayer The current player's communication channel.
     * @param endGame    Indicates whether the game has ended.
     */
    public GameLogic(CentralServer server, String id, CommonGround ground, Map<Comm,Player> players,List<Comm> turnOrder, Comm currPlayer,boolean endGame){
        this.id = id;
        this.server = server;
        this.ground = ground;
        this.players = players;
        this.turnOrder = turnOrder;
        this.currPlayer = currPlayer;
        this.cardPlaced = false;
        this.cardDrawn = false;
        this.executor = Executors.newFixedThreadPool(turnOrder.size());
        for(Comm comm: turnOrder){
            comm.setGame(this);
        }
        this.endGame = endGame;
    }

    /**
     * Submits a player to the game.
     *
     * @param comm The communication object representing the player.
     */
    public void submitPlayer(Comm comm){
        executor.submit(()->addPlayer(comm));
    }

    /**
     * Handles the joining of a player and calls the gameStart method
     * when all the players are in the "ready" state.
     *
     * @param comm The connection to the specific client.
     */
    public void addPlayer(Comm comm) {
        Player player = this.setUp.addPlayer(comm, ground);
        this.players.put(comm, player);
        this.setUp.setReady(comm);
        synchronized (this.setUp) {
            Set<String> names = this.players.values().stream().map(Player::getNickname).collect(Collectors.toSet());
            Comm.sendBroadcast(MsgCreator.joinedMsg(names), this.turnOrder);
            if (this.currPlayer == null) {
                this.currPlayer = comm;
                this.setUp.waitReady();
                gameStart();
            } else {
                this.setUp.notifyAll();
            }
        }
    }

    /**
     * Submits a load request for the game.
     */
    public void submitLoad(){
        executor.submit(this::loadGame);
    }

    /**
     * Loads game data and initializes the game flow.
     */
    private void loadGame(){
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
            comm.sendMsg(MsgCreator.createHandViewMsg(loadedHand));
            comm.sendMsg(MsgCreator.loadHiddenObjMsg(player.getSecretObjective()));
            String msg = MsgCreator.updPointsMsg(this.turnOrder.indexOf(comm),points);
            Comm.sendBroadcast(MsgCreator.cat(msg,
                    MsgCreator.colorChosenMsg(player.getNickname(),
                            player.getPawnColor())),this.turnOrder);
            try {
                Comm.sendBroadcast(MsgCreator.loadBoardMsg(this.id,this.turnOrder.indexOf(comm)),this.turnOrder);
            } catch (IOException e) {
                new ProjectLogger(getClass().getSimpleName()).
                        log("logs/serverLog.txt",Level.WARNING,"Can't load selected game",e);
                //TODO MAYBE INFORM THE PLAYERS
                throw new RuntimeException(e);
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
            this.turnOrder.get(i).sendMsg(MsgCreator.createHandViewMsg(handView));
        }

        createGroundView();
        this.currPlayer = turnOrder.getFirst();
        saveGame();
        server.addGameToMemorized(this.id);
        gameFlow();
    }

    /**
     * Handles the turns of the game until the end
     */
    private void gameFlow(){
        Comm.sendBroadcast(MsgCreator.changePhaseMsg(Phase.GAME), turnOrder);
        while (!this.endGame){
            fullTurn();
        }
        Comm.sendBroadcast(MsgCreator.changePhaseMsg(Phase.LASTTURN), this.turnOrder);
        fullTurn();


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

        for (Comm comm : turnOrder) {
            players.get(comm).addObjectivesScore(this.ground);
            Comm.sendBroadcast(MsgCreator.updPointsMsg(turnOrder.indexOf(comm), players.get(comm).getPoints()),
                    this.turnOrder);
        }

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
        Comm.sendBroadcast(MsgCreator.changePhaseMsg(Phase.ENDGAME), this.turnOrder);
    }

    /**
     * Calls the playTurn method for every player in the game.
     *
     */
    private void fullTurn() {
        final int winPoints = 20;
        for (int i = this.turnOrder.indexOf(this.currPlayer); i < players.size(); i++) {
            this.currPlayer = this.turnOrder.get(i);
            this.cardPlaced = false;
            this.cardDrawn = false;
            playTurn(this.turnOrder.get(i));
            try {
                this.endGame = this.endGame || players.get(this.turnOrder.get(i)).getPoints() >= winPoints ||
                    (ground.isEmpty(0) && ground.isEmpty(1));
            } catch (InvalidSelectionException ignored) {
                //this exception can't occur
            }
        }
        this.currPlayer = this.turnOrder.getFirst();
        if(!endGame) {
            saveGame();
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
                    new ProjectLogger(getClass().getSimpleName()).
                            log("logs/serverLog.txt",Level.WARNING,"Thread interrupted while waiting",e);
                    //TODO INFORM THE PLAYERS?
                    throw new RuntimeException(e);
                }
            }
        }
        synchronized (this) {
            while (!this.cardDrawn) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    new ProjectLogger(getClass().getSimpleName()).
                            log("logs/serverLog.txt",Level.WARNING,"Thread interrupted while waiting",e);
                    //TODO INFORM THE PLAYERS?
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Saves the game data to a JSON file.
     */
    private void saveGame(){
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
        int idx = Integer.parseInt(s[1]);
        int x = Integer.parseInt(s[2]);
        int y = Integer.parseInt(s[3]);
        boolean face = s[4].equals("true");
        if(currPlayer != comm || this.cardPlaced) {
            comm.sendMsg(MsgCreator.confirmMsg(false,"Not your turn"));
            return;
        }
        try {
            DrawableCard card = this.players.get(comm).placeCard(idx,x,y,face);
            updBoardView(card, x, y, comm);
            synchronized(this) {
                this.cardPlaced = true;
                comm.sendMsg(MsgCreator.confirmMsg(true,"Successful placement"));
                this.notifyAll();
            }
        } catch (InvalidPlacementException e) {
            comm.sendMsg(MsgCreator.confirmMsg(false,"Invalid placement"));
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
            comm.sendMsg(MsgCreator.confirmMsg(false,"Not your turn"));
            return;
        }
        try {
            DrawableCard drawnCard  = this.players.get(comm).draw(ground, Integer.parseInt(s[1]));
            comm.sendMsg(MsgCreator.updHandViewMsg(new CardView(drawnCard)));

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
                comm.sendMsg(MsgCreator.confirmMsg(true,"Draw successful"));
                this.notifyAll();
            }
        } catch (InvalidDrawException | InvalidSelectionException e) {
            comm.sendMsg(MsgCreator.confirmMsg(false,"Invalid draw"));
        }
    }

    /**
     * Sends a chat message to the specified receiver.
     *
     * @param msg The chat message to be sent (format: "receiver;sender;message").
     */
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
     * Closes connections and shuts down the client.
     */
    private void shutdown() {
        for (Comm comm : this.turnOrder) {
            comm.setCrashHandler(this.server);
            this.server.submitClient(comm);
        }
        this.server.closeGame(this.id);
    }

    /**
     * Handles a crash event for a communication channel.
     *
     * @param comm The communication channel that crashed.
     */
    public synchronized void handleCrash(Comm comm){
        executor.shutdownNow();
        Set<Comm> comms = (this.setUp==null) ? this.players.keySet() : this.setUp.getComms();
        comms.remove(comm);
        this.server.handleCrash(comm);
        for(Comm iter: comms){
            iter.setCrashHandler(this.server);
        }

        String msg = MsgCreator.changePhaseMsg(Phase.CLIENTCRASHED);
        Comm.sendBroadcast(msg,new ArrayList<>(comms));
        this.server.removeGame(this.id);
        for(Comm iter: comms){
            //CLEAN THE PENDING MESSAGES
            iter.clean();
            this.server.submitClient(iter);
        }
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

    /**
     * Creates a view for the back side of a card.
     *
     * @param flag Determines whether the card is a resource card or a gold card.
     * @return A CardView representing the back side of the card.
     */
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
}
