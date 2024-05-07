package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.model.gameplane.CommonGround;
import it.polimi.ingsw.model.gameplane.Player;
import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.ObjectiveSerializeLogic;
import it.polimi.ingsw.util.Symbol;
import it.polimi.ingsw.util.exception.InvalidDrawException;
import it.polimi.ingsw.util.exception.InvalidPlacementException;
import it.polimi.ingsw.util.exception.InvalidSelectionException;
import it.polimi.ingsw.util.json.CardSerializeLogic;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class that manages the flow of the game.
 */
public class GameLogic {

    private boolean cardPlaced, cardDrawn;
    private CommonGround ground;
    private Comm currPlayer;
    private SetUpLogic setUp;

    private final CentralServer server;
    private final List<Comm> turnOrder;
    private final Map<Comm, Player> players;

    /**
     * Constructor for the GameLogic.
     *
     * @param server The server that created it.
     * @param size The number of players in the game.
     */
    public GameLogic(CentralServer server, int size) {
        this.turnOrder = new ArrayList<>();
        this.setUp = new SetUpLogic(this, size, this.turnOrder);
        this.server = server;
        players = new ConcurrentHashMap<>();
        try{
            ground = new CommonGround();
        } catch (IOException e) {
            //TODO log file
            System.out.println("file corrupted");
        }
    }

    public GameLogic(CentralServer server, CommonGround ground, Map<Comm,Player> players,List<Comm> turnOrder, Comm currPlayer){
        this.server = server;
        this.ground = ground;
        this.players = players;
        this.turnOrder = turnOrder;
        this.currPlayer = currPlayer;
    }

    /**
     * Handles the joining of a player and calls the gameStart method
     * when all the players are in the "ready" state.
     *
     * @param comm The connection to the specific client.
     */
    public void addPlayer(Comm comm){
        Player player = setUp.addPlayer(comm, ground);
        players.put(comm, player);
        setUp.setReady(comm);
        synchronized (this.setUp) {
            int numRemaining = this.turnOrder.size()-this.players.size();
            if(numRemaining>0){
                String msg = MsgCreator.displayMessage("Waiting for players\nRemaining: " + numRemaining + '\n');
                Comm.sendBroadcast(msg, new ArrayList<>(players.keySet()));
            }
            if (this.currPlayer == null) {
                this.currPlayer = comm;
                setUp.waitReady();
                gameStart();
            } else {
                this.setUp.notifyAll();
            }
        }
    }

    /**
     * Performs all the starting operations nedeed for the game
     */
    private void gameStart() {
        for (int i = 0; i < players.size(); i++) {
            DrawableCard[] hand = players.get(this.turnOrder.get(i)).drawFirstThree(ground);
            sendMsg(this.turnOrder.get(i), MsgCreator.createHandViewMsg(hand));
        }

        this.sendGroundView();
        gameFlow();
    }

    public void loadGame() {
        List<String> names = new ArrayList<>();
        String publicPlayerView, privatePlayerView;
        for(int i=0;i<turnOrder.size();i++){
            Player player = this.players.get(this.turnOrder.get(i));
            publicPlayerView = MsgCreator.updPointsMsg(i, player.getPoints());
            publicPlayerView = MsgCreator.cat(publicPlayerView, MsgCreator.createBoardViewMsg(player, i));
            names.add(players.get(turnOrder.get(i)).getNickname());
            Comm.sendBroadcast(publicPlayerView, this.turnOrder);

            privatePlayerView = MsgCreator.secretObjectiveMessage(player.getSecretObjective());
            privatePlayerView = MsgCreator.cat(privatePlayerView, MsgCreator.createHandViewMsg(player));
            this.turnOrder.get(i).sendMsg(privatePlayerView);
        }
        Comm.sendBroadcast(MsgCreator.turnOrderMessage(names), this.turnOrder);
        this.sendGroundView();
        gameFlow();
    }

    /**
     * Handles the turns of the game until the end
     */
    private void gameFlow(){
        Comm.sendBroadcast(MsgCreator.gameStartMsg(), turnOrder);
        while (!fullTurn());
        fullTurn();

        for (Comm comm : turnOrder) {
            players.get(comm).addObjectivesScore(this.ground);
            Comm.sendBroadcast(MsgCreator.updPointsMsg(turnOrder.indexOf(comm), players.get(comm).getPoints()),
                    this.turnOrder);
        }
        declareWinner();
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
        Map<Player, Integer> leaderboard = new HashMap<>();
        int pos = 1;
        List<Player> ls = this.players.values().stream()
                .sorted(Comparator.comparingInt(Player::getPoints)
                        .thenComparingInt(Player::getCompletedObjs))
                .toList();
        for(int i = 0; i < ls.size() - 1; i++){
            leaderboard.put(ls.get(i), pos);
            if(!(ls.get(i).getPoints() == ls.get(i+1).getPoints() &&
                    ls.get(i).getCompletedObjs() == ls.get(i+1).getCompletedObjs())){
                pos++;
            }
        }
        leaderboard.put(ls.getLast(), pos);
        Comm.sendBroadcast(MsgCreator.leadeboarMessage(leaderboard), turnOrder);
    }

    /**
     * Calls the playTurn method for every player in the game.
     *
     * @return True if someone reached 20 points or both decks are empty, False otherwise.
     */
    private boolean fullTurn() {
        boolean endGame = false;
        for (int i = 0; i < players.size(); i++) {
            this.currPlayer = this.turnOrder.get(i);
            this.cardPlaced = false;
            this.cardDrawn = false;
            playTurn(this.turnOrder.get(i));
            try {
                endGame = endGame || players.get(this.turnOrder.get(i)).getPoints() >= 20 ||
                        (ground.isEmpty(0) && ground.isEmpty(1));
            } catch (InvalidSelectionException e) {
                System.out.println("can't occur");
            }
        }
        return endGame;
    }

    /**
     * Informs a client of the current state of a turn and waits for them to complete it.
     *
     * @param comm The connection to the specific client.
     */
    private void playTurn(Comm comm) {
        sendMsg(comm,MsgCreator.tokenMsg("Your turn to place a card\n", 0));
        comm.waitMsg();
        synchronized(this) {
            while (!this.cardPlaced){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e); //TODO: HANDLE EXCEPTION
                }
            }
        }

        sendMsg(comm, MsgCreator.tokenMsg("Your turn to draw a card\n", 1));
        comm.waitMsg();
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
        /*
            SaveLogic save = new SaveLogic(players,turnOrder,comm,ground);
            String json = gson.toJson(save,SaveLogic.class);
            this.server.saveGame(this,json);
         */
    }

    /**
     * Handles the placement process.
     *
     * @param comm The connection to the specific client.
     * @param msg A string containing instructions for the placement of the card.
     * @return True if the placement succeeds, False if it's not that client's turn or the placement isn't correct.
     */
    public boolean placeCard(Comm comm, String msg) {
        String[] s = msg.split(";");
        //TODO REVISE THIS
        int idx = Integer.parseInt(s[1]);
        int x = Integer.parseInt(s[2].split(",")[0]);
        int y = Integer.parseInt(s[2].split(",")[1]);
        boolean face = s[3].equals("true");
        if(currPlayer != comm || this.cardPlaced) {
            comm.sendMsg(MsgCreator.confirmMsg(false));
            return false;
        }
        try {
            DrawableCard card = this.players.get(comm).placeCard(idx,x,y,face);
            updBoardView(card, x, y, comm);
            synchronized(this) {
                this.cardPlaced = true;
                comm.sendMsg(MsgCreator.confirmMsg(true));
                this.notifyAll();
            }
            return true;
        } catch (InvalidPlacementException e) {
            comm.sendMsg(MsgCreator.confirmMsg(false));
            return false;
        }
    }

    /**
     * Handles the draw process.
     *
     * @param comm The connection to the specific client.
     * @param msg A string containing the card to be drawn.
     * @return True if the draw succeeds, False if it's not that client's turn or the draw isn't correct.
     */
    public boolean drawCard(Comm comm, String msg) {
        String[] s = msg.split(";");
        String cmd = "";
        int idx;
        if(currPlayer != comm || this.cardDrawn) {
            comm.sendMsg(MsgCreator.confirmMsg(false));
            return false;
        }
        try {
            DrawableCard drawnCard  = this.players.get(comm).draw(ground, Integer.parseInt(s[1]));

            comm.sendMsg(MsgCreator.updHandViewMsg(drawnCard));

            idx = Integer.parseInt(s[1]);
            if(idx == 1 || idx == 2){
                cmd = MsgCreator.updGroundViewMsg(idx, ground.getGroundResourceCards()[idx-2]);
                idx = 0;
            } else if (idx == 4 || idx == 5) {
                cmd = MsgCreator.updGroundViewMsg(idx, this.ground.getGroundGoldCard()[idx-4]);
                idx = 3;
            }
            Symbol sym;
            if (idx == 0) {
                sym = this.ground.peekResDeck();
            } else {
                sym = this.ground.peekGoldDeck();
            }
            MsgCreator.updGroundViewMsg(idx, sym);
            Comm.sendBroadcast(cmd, turnOrder);
            synchronized(this){
                this.cardDrawn = true;
                comm.sendMsg(MsgCreator.confirmMsg(true));
                this.notifyAll();
            }
            return true;
        } catch (InvalidDrawException | InvalidSelectionException e) {
            comm.sendMsg(MsgCreator.confirmMsg(false));
            return false;
        }
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
        String updBoardMsg = MsgCreator.updBoardMessage(turnOrder.indexOf(currPlayer),x,y,newCard,player.getInventory());
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
     * Sets up the game logic by sending broadcast messages to update the ground view.
     * This method sends multiple broadcast messages containing information about the game state,
     * such as the resources and gold cards on the ground, as well as common objectives.
     * Each broadcast message is formatted as follows:
     * UPDGROUNDVIEW;<index>;<data>
     * Where:
     * - <index>: The index indicating the type of update.
     * - <data>: The data associated with the update.
     */
    private void sendGroundView(){
        String s = MsgCreator.updGroundViewMsg(0,this.ground.peekResDeck());
        s = MsgCreator.cat(s, MsgCreator.updGroundViewMsg(3, this.ground.peekGoldDeck()));
        s = MsgCreator.cat(s, MsgCreator.updGroundViewMsg(1, this.ground.getGroundResourceCards()[0]));
        s = MsgCreator.cat(s, MsgCreator.updGroundViewMsg(2, this.ground.getGroundResourceCards()[1]));
        s = MsgCreator.cat(s, MsgCreator.updGroundViewMsg(4, this.ground.getGroundGoldCard()[0]));
        s = MsgCreator.cat(s, MsgCreator.updGroundViewMsg(5, this.ground.getGroundGoldCard()[1]));
        s = MsgCreator.cat(s, MsgCreator.updGroundViewMsg(6, this.ground.getCommonObjectives()[0]));
        s = MsgCreator.cat(s, MsgCreator.updGroundViewMsg(7, this.ground.getCommonObjectives()[1]));
        Comm.sendBroadcast(s, turnOrder);
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
    private void shutdown(){
        synchronized(server.lock){
            this.setUp.closeConnections();
            this.setUp = null;
            this.server.close(this);
        }
    }
    /*
      Type of messages:
      sendMsg("CHOOSE;-what to choose-")
      sendMsg("TURNORDER;[-FirstPlayerName-,-SecondPlayerName-,...]")
      sendMsg("UPDBOARDVIEW;-PlayerTurnOrderNumber-;-board(xRel,yRel)-;-newCard-;-inventory-;points")
      sendMsg("UPDGROUNDVIEW;-index-;-card-")
      sendMsg("SETUPGROUNDVIEW;GROUND.TOJSON")
      sendMsg("PLACECARD;board(xRel,yRel);-card-")
      sendMsg("UPDHANDVIEW;cardPos,-drawnCard-")
      sendMsg("TOKEN;-TokenType-")
      sendMsg("DRAWCARD;IDX")
      sendMsg("DISPLAY;String s")
      sendMsg("GAMECLOSED;-ERROR OR GAME FINISHED-")
     */
    //TODO see if we manage to view players' names while the game is starting
}
