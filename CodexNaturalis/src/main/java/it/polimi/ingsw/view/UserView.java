package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.userclient.Client;
import it.polimi.ingsw.controller.userclient.ClientRMI;
import it.polimi.ingsw.controller.userclient.ClientSocket;
import it.polimi.ingsw.util.*;
import it.polimi.ingsw.view.coreview.*;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public abstract class UserView {
    Client client;
    ModelView modelView;
    Set<PawnColor> colors;
    Map<String,Integer> leaderboard;
    final Map<String,Integer> positions;
    Set<String> playersInLobby;
    final AtomicBoolean confirmValue;
    String confirmMsg;
    List<String> turnOrder;
    final Map<String, PawnColor> colorsChosen;
    String name;
    Phase currPhase;
    CardView[] starts;
    ObjectiveView[] scrtObjs;

    List<Message> chats;

    String toSend;
    int idx, tmpIdx;
    final AtomicBoolean waitForPlayers;

    final Object toSendlock;
    final Object currPlayerLock;
    final Object modelLock;
    final Object colorLock;
    final Object leaderboardLock;
    final Object chatLock;
    final Object lobbyLock;

    /**
     * Constructor of the class.
     */
    public UserView() {
        this.modelView = new ModelView();
        this.confirmValue = new AtomicBoolean(false);
        this.positions = new HashMap<>();
        this.toSend = null;
        this.waitForPlayers = new AtomicBoolean(false);
        this.colorsChosen = new HashMap<>();
        this.toSendlock = new Object();
        this.currPlayerLock = new Object();
        this.modelLock = new Object();
        this.colorLock = new Object();
        this.leaderboardLock = new Object();
        this.chatLock = new Object();
        this.lobbyLock = new Object();
    }

    /**
     * Connects to the server using the specified communication type.
     *
     * @param type If `true`, uses socket communication; otherwise, uses RMI communication.
     * @throws RemoteException If an error occurs during communication.
     */
    public void connect(boolean type) throws RemoteException {
        this.client = type ? new ClientSocket(this) : new ClientRMI(this);
    }

    /**
     * Waits for input and returns the received string.
     *
     * @return The received message.
     */
    public String getInput(){
        synchronized(this.toSendlock) {
            while(this.toSend == null) {
                try {
                    this.toSendlock.wait();
                } catch (InterruptedException e) {
                    new ProjectLogger(getClass().getSimpleName()).
                            log("logs/clientLog.txt", Level.SEVERE,"Thread interrupted while waiting",e);
                    throw new RuntimeException(e);
                }
            }
            String tmp = this.toSend;
            this.toSend = null;
            return tmp;
        }
    }

    /**
     * Allocate boards given the capacity number.
     *
     * @param capacity The maximum number of players.
     */
    public void setCapacity(int capacity){
        synchronized(this.modelLock){
            this.modelView.allocateBoards(capacity);
        }
    }

    /**
     * Handles players joining the lobby
     *
     * @param players The set of players who joined.
     */
    public abstract void playerJoined(Set<String> players);

    /**
     * Sets the color of a player's pawn in the colorsChosen attrib.
     *
     * @param player The name of the player.
     * @param color The color to assign to the player's pawn.
     */
    public void setPlayerColor(String player, PawnColor color){
        this.colorsChosen.put(player, color);
    }

    /**
     * Updates the available colors of the pawns.
     *
     * @param newColors A set of new pawn colors.
     */
    public void updColors(Set<PawnColor> newColors){
        synchronized(this.colorLock){
            this.colors.retainAll(newColors);
        }
    }

    /**
     * Sets the player's hand.
     *
     * @param hand The list of CardViews representing the player's hand.
     */
    public void setHand(List<CardView> hand){
        synchronized(this.modelLock){
            this.modelView.setHand(hand);
        }
    }

    /**
     * Creates the ground view with specified cards and objectives.
     *
     * @param resCards An array of resource cards.
     * @param goldCards An array of gold cards.
     * @param objs An array of objective cards.
     */
    public void createGroundView(CardView[] resCards, CardView[] goldCards, ObjectiveView[] objs){
        synchronized(this.modelLock){
            this.modelView.setGround(new GroundView(resCards,goldCards,objs));
        }
    }

    /**
     * Loads the secret objective into the model view.
     *
     * @param objectiveView The objective view to load.
     */
    public void loadObj(ObjectiveView objectiveView){
        this.modelView.setObj(objectiveView);
    }

    /**
     * Loads a board view into the model at the specified index.
     *
     * @param idx The index where the board view should be loaded.
     * @param boardView The board view to load.
     */
    public void loadBoard(int idx,BoardView boardView){
        this.modelView.setBoard(idx,boardView);
    }

    /**
     * Sets the player names for the leaderboard.
     *
     * @param turnOrder The list of player names in turn order.
     */
    public void setNames(List<String> turnOrder){
        this.turnOrder.addAll(turnOrder);
        synchronized(this.leaderboardLock){
            for(String name : turnOrder){
                this.leaderboard.put(name,0);
            }
        }
    }

    /**
     * Updates the player's hand view with the specified card.
     *
     * @param card The CardView to update in the hand.
     */
    public void updHandView(CardView card){
        synchronized(this.modelLock){
            this.modelView.updHand(card,this.idx);
        }
    }

    /**
     * Updates the ground view with the specified card at the given index.
     *
     * @param idx The index representing the type of item being updated
     * @param card The Card being placed.
     */
    public void updGroundView(int idx,CardView card){
        synchronized(this.modelLock){
            this.modelView.updGround(idx,card);
        }
    }

    /**
     * Updates the board view with the specified parameters.
     *
     * @param turnIdx   The turn index.
     * @param card      The card to place.
     * @param x         The x-coordinate.
     * @param y         The y-coordinate.
     * @param inventory The inventory to be updated.
     */
    public void updBoardView(int turnIdx, CardView card, int x, int y, Map<Symbol,Integer> inventory){
        synchronized(this.modelLock){
            this.modelView.getBoard(turnIdx).updBoardView(card,x,y,inventory);
        }
    }

    /**
     * Updates the points for a player at the specified turn index.
     *
     * @param turnIdx The index of the turn.
     * @param newPoints The new points to assign to the player.
     */
    public void updPoints(int turnIdx,int newPoints){
        synchronized(this.leaderboardLock){
            this.leaderboard.put(this.turnOrder.get(turnIdx),newPoints);
        }
    }

    /**
     * Set the user's name.
     *
     * @param name String with the name to be set.
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Sets the current player.
     *
     * @param currPlayer String with the name of the current player.
     */
    public abstract void setCurrPlayer(String currPlayer);

    /**
     * Adds a chat message to the collection.
     *
     * @param msg The chat message to add.
     */
    public void addChatMessage(Message msg) {
        synchronized(this.chatLock){
            this.chats.add(msg);
        }
    }

    /**
     * Sends a confirmation message.
     *
     * @param confirmValue The confirmation value (true or false).
     * @param confirmMsg   The confirmation message to display.
     */
    public void sendConfirm(boolean confirmValue,String confirmMsg){
        synchronized(this.confirmValue){
            this.confirmValue.set(confirmValue);
            this.confirmMsg = confirmMsg;
        }
    }

    /**
     * Sets a certain phase and sets cards and objectives.
     *
     * @param phase The new phase to set.
     * @param items Item views associated with the phase.
     */
    public void setCurrPhase(Phase phase, ItemView... items){
        switch(phase){
            case Phase.STARTCARD -> this.starts = Arrays.copyOf(items,items.length,CardView[].class);
            case Phase.OBJECTIVE -> this.scrtObjs = Arrays.copyOf(items,items.length,ObjectiveView[].class);
        }
    }

    /**
     * Sets the players positions using the provided map.
     *
     * @param positions A map containing position information.
     */
    public void setPositions(Map<String, Integer> positions) {
        synchronized (this.positions) {
            this.positions.putAll(positions);
            this.positions.notifyAll();
        }
    }

    /**
     * Attempts to establish a connection to the specified address.
     *
     * @param addr The address to connect to.
     * @throws RuntimeException If the connection attempt fails.
     */
    public void tryConnection(String addr) throws RuntimeException{
        this.client.connect(addr);
    }

    /**
     * Scans the input and sets the value to be sent.
     *
     * @param toScan The input string to scan.
     */
    public void scanInput(String toScan){
        synchronized(this.toSendlock){
            this.toSend = toScan.trim();
            this.toSendlock.notifyAll();
        }
    }

    /**
     * Clears the view state and initializes fields to their initial values.
     */
    public void cleanView(){
        this.modelView = new ModelView();
        colors = new HashSet<>(Arrays.asList(PawnColor.values()));

        //CLEAR COLLECTIONS IN CASE OF A CLIENT CRASHED
        this.leaderboard.clear();
        this.positions.clear();
        this.playersInLobby.clear();
        this.turnOrder.clear();
        this.chats.clear();
        this.confirmValue.set(false);

        //SETTING THE FIELDS TO THEIR INITIAL VALUES
        this.confirmMsg = null;
        this.name = null;
        this.starts = new CardView[2];
        this.scrtObjs = new ObjectiveView[2];
        this.waitForPlayers.set(false);
    }


    /**
     * Changes phase to the received one.
     *
     * @param phase The phase to be set.
     */
    public abstract void changePhase(Phase phase);
}
