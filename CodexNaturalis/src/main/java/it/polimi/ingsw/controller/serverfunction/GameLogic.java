package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.model.gameplane.CommonGround;
import it.polimi.ingsw.model.gameplane.Player;
import it.polimi.ingsw.model.util.exception.InvalidDrawException;
import it.polimi.ingsw.model.util.exception.InvalidPlacementException;
import it.polimi.ingsw.model.util.exception.InvalidSelectionException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Class that manages the flow of the game.
 */
public class GameLogic {

    private boolean cardPlaced;
    private CommonGround ground;
    private Comm currPlayer,firstPlayer;
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
        try{
            ground = new CommonGround();
        } catch (IOException e) {
            //TODO log file
            System.out.println("file corrupted");
        }
        this.turnOrder = new ArrayList<>();
        this.setUp = new SetUpLogic(this, size, this.turnOrder);
        this.server = server;
        players = new ConcurrentHashMap<>();
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
        synchronized (this.setUp){
            if(this.firstPlayer == null){
                this.firstPlayer = comm;
                setUp.waitReady();
                gameStart();
            }else{
                this.setUp.notifyAll();
            }
        }
    }

    /**
     * Handles the execution of the game until the end.
     */
    private void gameStart() {
        StringBuilder msg;

        for (int i = 0; i < players.size(); i++) {
            String[] s = players.get(this.turnOrder.get(i)).drawFirstThree(ground);
            msg = new StringBuilder("UPDHANDVIEW;" + 0 + ";" + s[0]);
            msg = new StringBuilder(msg + "|UPDHANDVIEW;" + 1 + ";" + s[1]);
            msg = new StringBuilder(msg + "|UPDHANDVIEW;" + 2 + ";" + s[2]);
            sendMsg(this.turnOrder.get(i), msg.toString());
        }
        
        //keep playing turns until the end condition is reached - then play another turn
        while(!fullTurn());
        fullTurn();
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
        sendMsg(comm, "DISPLAY;Your_turn_to_place_a_card");
        while(!this.cardPlaced);

        sendMsg(comm, "DISPLAY;Your_turn_to_draw_a_card");
        while(this.cardPlaced);
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
        if(currPlayer != comm || cardPlaced)
            return false;
        try {
            String card = this.players.get(comm).placeCard(Integer.parseInt(s[1]), Integer.parseInt(s[2]),
                    Integer.parseInt(s[3]), s[4].equals("1"));
            this.cardPlaced = true;
            updBoardView(card, "(" + s[2] + "," + s[3] + ")", comm);
            return true;
        } catch (InvalidPlacementException e) {
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
        if(currPlayer != comm || !cardPlaced)
            return false;
        try {
            this.players.get(comm).draw(ground,Integer.parseInt(s[1]));
            this.cardPlaced = false;
            return true;
        } catch (InvalidDrawException | InvalidSelectionException e) {
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
     * @param positionOfCard The position where it has been placed.
     * @param currPlayer The player who placed the card.
     */
    private void updBoardView(String newCard, String positionOfCard, Comm currPlayer) {
        Player player = players.get(currPlayer);
        String msg = "UPDBOARDVIEW;" + turnOrder.indexOf(currPlayer) + ";" + positionOfCard + ";" + newCard + ";"
                + player.getPoints();
        Comm.sendBroadcast(msg, turnOrder.stream().filter(x -> x != currPlayer).collect(Collectors.toList()));
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
    private void shutdown(){
        synchronized(server.lock){
            this.setUp.closeConnections();
            this.setUp = null;
            this.server.close(this);
        }
    }
    /**
     * Type of messages:
     * sendMsg("choose;-what to choose-")
     * sendMsg("setUpMsg;-FirstPlayerName-; -SecondPlayerName-;...")
     * sendMsg("updBoardView;-PlayerTurnOrderNumber-;-board(xRel,yRel)-;-newCard.toString()-;-points-")
     * sendMsg("updGroundView;-index-;card.toString()")
     * sendMsg("placeCard;board(xRel,yRel);card.toString()")
     * sendMsg("updHandView;cardPos,drawnCard.toString()")
     * sendMsg("token;-TokenType-")
     * sendMsg("display;String s")
     * sendMsg("GAMECLOSED;-ERROR OR GAME FINISHED-")
     */
    //TODO see if we manage to view players' names while the game is starting
    //TODO check the numPlayer attribute in the client side
}
