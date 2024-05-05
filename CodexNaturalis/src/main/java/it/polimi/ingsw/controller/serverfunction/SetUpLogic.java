package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.model.gameplane.CommonGround;
import it.polimi.ingsw.model.gameplane.Player;
import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.PawnColor;

import java.util.*;

/**
 * A class that handles the setup of a game.
 */
class SetUpLogic {
    private enum State {
        NAME, STARTING, COLOR, OBJECTIVE, READY
    }
    
    private final int numPlayers;
    private final GameLogic game;
    private final Map<Comm, State> record;
    private final List<PawnColor> availableColors;
    private final Map<String, Comm> nameToComm;
    private final List<Comm> turnOrder;

    /**
     * Constructor for the setUpLogic.
     *
     * @param game The GameLogic that creates this setUpLogic.
     * @param num The number of players in the game.
     * @param turnOrder An empty list that will be updated during execution.
     */
    SetUpLogic(GameLogic game, int num, List<Comm> turnOrder){
        this.game = game;
        this.record = new HashMap<>();
        this.nameToComm = new HashMap<>();
        this.availableColors = new LinkedList<>(List.of(PawnColor.values()));
        this.numPlayers=num;
        this.turnOrder = turnOrder;
    }

    /**
     * Handles the joining of a player.
     *
     * @param comm The connection to the specific client.
     * @param ground CommonGround of the game.
     * @return The player created.
     */
    public Player addPlayer(Comm comm, CommonGround ground) {
        StartingCard card;
        String playerName = linkName(comm);
        record.put(comm, State.STARTING);
        synchronized (this){
            this.notifyAll();
            //TODO stream inside synchronized not ideal
            int numRemaining = (int) (numPlayers - record.values().stream().filter(x->x.equals(State.STARTING)).count());
            if(numRemaining>0) {
                String msg = MsgCreator.displayMessage("Waiting for players to finish\nRemaining: " + numRemaining + '\n');
                List<Comm> stateStarting = record.keySet().stream().filter(x->record.get(x)==State.STARTING).toList();
                Comm.sendBroadcast(msg,stateStarting);
            }
            this.waitStart(State.STARTING);
        }
        synchronized (this.turnOrder){
            if(this.turnOrder.isEmpty()){
                this.turnOrder.addAll(this.record.keySet());
            }
        }
        synchronized (ground){
            card = ground.getRandomStartingCard();
        }
        chooseStartingCard(comm, card);
        record.put(comm, State.COLOR);
        PawnColor color = chooseColor(comm);
        record.put(comm, State.OBJECTIVE);
        Objective obj = chooseObjective(comm,ground);
        record.put(comm, State.READY);
        Player player = new Player(playerName, obj, color);
        player.placeStartingCard(card);
        String msg = MsgCreator.updBoardMessage(turnOrder.indexOf(comm),0,0,card,
                player.getInventory(),player.getPoints());
        Comm.sendBroadcast(msg,this.turnOrder);
        return player;
    }

    /**
     * Adds a communication object to the record.
     *
     * @param comm The connection to the specific client.
     */
    public void addRecord(Comm comm) {
        this.record.put(comm, State.NAME);
    }

    /**
     * Handles the process of choosing a player name.
     *
     * @param comm The connection to the specific client.
     * @return The chosen name.
     */
    private String linkName(Comm comm) {
        String name;
        boolean available = true;

        do{
            game.sendMsg(comm, MsgCreator.chooseMessage("Insert name (1-8 length, starts with a letter, no special characters): ",
                    "^[a-zA-Z][a-zA-Z0-9]{0,7}$",
                    "Invalid name: 1-8 length, starts with a letter, no special characters"));
            name = game.getMsg(comm);
            synchronized (nameToComm) {
                if (nameToComm.get(name) == null) {
                    nameToComm.put(name, comm);
                    available = false;
                }
            }
        }while(available);
        game.sendMsg(comm, MsgCreator.displayMessage("valid name\n"));

        return name;
    }

    /**
     * Handles the process of choosing a starting card orientation.
     *
     * @param comm The connection to the specific client.
     * @param card The chosen orientation.
     */
    private void chooseStartingCard(Comm comm, StartingCard card){
        game.sendMsg(comm, MsgCreator.chooseMessage(card.toString()+":\n", 0, 1,"Invalid orientation, try again!"));
        card.setShowingFace(game.getMsg(comm).equals("1"));
    }

    /**
     * Handles the process of choosing a player's pawn color.
     *
     * @param comm The connection to the specific client.
     * @return The chosen color.
     */
    private PawnColor chooseColor(Comm comm) {
        PawnColor color = null;
        boolean chosen;

        chosen = true;
        while (chosen) {
            game.sendMsg(comm, 
                    MsgCreator.chooseMessage(this.availableColors.toString() +
                            ": ", this.availableColors, "Color not available, try again!"));
            color = PawnColor.valueOf(game.getMsg(comm));
            synchronized (this.availableColors) {
                if (availableColors.contains(color)) {
                    availableColors.remove(color);
                    chosen = false;
                }
            }
        }

        game.sendMsg(comm, MsgCreator.displayMessage("Valid color\n"));
        Map<Comm,State> copy = new HashMap<>(record);

        //TODO handle this case
        /*for (Comm c : copy.keySet()) {
            if (c != comm && copy.get(c) == State.COLOR) {
                game.sendMsg(comm, "DISPLAY;"+availableColors.toString());
            }
        }*/
        return color;
    }

    /**
     * Handles the process of choosing an objective.
     *
     * @param comm The connection to the specific client.
     * @return The chosen objective.
     */
    private Objective chooseObjective(Comm comm, CommonGround ground) {
        Objective[] objs;
        synchronized(ground) {
            objs = new Objective[] { ground.getRandomObjective(),
                    ground.getRandomObjective() };
        }

        String s = objs[0].toString()+'\n';
        s += objs[1].toString();
        game.sendMsg(comm, MsgCreator.chooseMessage(s+"\n", 0, 1, "There's not an objective with that index, try again!"));
        s = game.getMsg(comm);
        Objective obj = objs[Integer.parseInt(s)];
        game.sendMsg(comm, MsgCreator.secretObjectiveMessage(obj));
        return obj;
    }

    /**
     * Waits until every player is in the specified state.
     *
     * @param s The required state.
     */
    private void waitStart(State s) {
        synchronized (this) {
            Set<State> set = new HashSet<>(this.record.values());
            while (this.record.size() != this.numPlayers || !(set.contains(s) && set.size() == 1)) {
                try {

                    this.wait();
                } catch (InterruptedException e) {
                    //TODO log fileSystem.out.println()
                }
                set = new HashSet<>(this.record.values());
            }
        }
    }

    /**
     * Checks if the game is already full.
     *
     * @return True if the game is full, False otherwise.
     */
    public boolean isFull(){
        return this.record.size() >= this.numPlayers;
    }

    /**
     * Closes all the open connections.
     */
    void closeConnections(){
        for(Comm comm : this.record.keySet()){
            comm.close();
        }
    }

    /**
     * Intended to be called from GameLogic to wait for the "Ready" state.
     */
    void waitReady() {
        waitStart(State.READY);
    }
}
