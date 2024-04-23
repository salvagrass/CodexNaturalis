package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.model.gameplane.CommonGround;
import it.polimi.ingsw.model.gameplane.Player;
import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.model.util.PawnColor;

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
        this.record.notifyAll();
        waitStart(State.STARTING);
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
        Comm.sendBroadcast("UPDBOARDVIEW;"+turnOrder.indexOf(comm)+"(0,0)"+card.toString(), this.turnOrder);
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
            game.sendMsg(comm, "DISPLAY;Insert_name:_|CHOOSE;^");
            name = game.getMsg(comm);
            synchronized (nameToComm) {
                if (nameToComm.get(name) == null) {
                    nameToComm.put(name, comm);
                    available = false;
                }
            }
        }while(available);
        game.sendMsg(comm, "DISPLAY;valid name"); //TODO correct message

        return name;
    }

    /**
     * Handles the process of choosing a starting card orientation.
     *
     * @param comm The connection to the specific client.
     * @param card The chosen orientation.
     */
    private void chooseStartingCard(Comm comm, StartingCard card){
        game.sendMsg(comm, "DISPLAY;"+card.toString()+"CHOOSE;[0,1]");
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
            game.sendMsg(comm, "DISPLAY;" + this.availableColors.toString() + "|CHOOSE;" + this.availableColors.toString().replace('[', '{').replace(']','}'));
            color = PawnColor.valueOf(game.getMsg(comm));
            synchronized (this.availableColors) {
                if (availableColors.contains(color)) {
                    availableColors.remove(color);
                    chosen = false;
                }
            }
            if (chosen) {
                game.sendMsg(comm, "DISPLAY;" + this.availableColors.toString() + "|CHOOSE;"
                        + this.availableColors.toString());
            }
        }

        game.sendMsg(comm,"DISPLAY;Valid_color");
        Map<Comm,State> copy = new HashMap<>(record);

        for (Comm c : copy.keySet()) {
            if (c != comm && copy.get(c) == State.COLOR) {
                game.sendMsg(comm, "DISPLAY;"+availableColors.toString()); //TODO handle this case
            }
        }
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

        String s = objs[0].toString() + "; " + objs[1].toString();
        game.sendMsg(comm, "DISPLAY;" + s + "|CHOOSE;[0,1]");
        s = game.getMsg(comm);
        return objs[Integer.parseInt(s)];
    }

    /**
     * Waits until every player is in the specified state.
     *
     * @param s The required state.
     */
    private void waitStart(State s) {
        synchronized (this.record) {
            Set<State> set = new HashSet<>(this.record.values());
            while (this.record.size() != this.numPlayers && set.contains(s) && set.size() == 1) {
                try {
                    this.record.wait();
                } catch (InterruptedException e) {
                    //TODO log fileSystem.out.println()
                }
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
