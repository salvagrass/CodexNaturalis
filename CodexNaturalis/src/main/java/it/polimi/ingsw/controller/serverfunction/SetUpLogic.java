package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.model.gameplane.CommonGround;
import it.polimi.ingsw.model.gameplane.Player;
import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.PawnColor;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.util.ProjectLogger;
import it.polimi.ingsw.view.coreview.CardView;
import it.polimi.ingsw.view.coreview.ObjectiveView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * A class that handles the setup of a game.
 */
class SetUpLogic {
    private enum State {
        NAME, STARTING, COLOR, OBJECTIVE, READY
    }
    
    private final int numPlayers;
    private final Map<Comm, State> record;
    private final Set<PawnColor> availableColors;
    private final Map<String, Comm> nameToComm;
    private final List<Comm> turnOrder;
    private final CommonGround ground;
    final Object setUpLock = new Object();

    /**
     * Constructor for the setUpLogic.
     *
     * @param num The number of players in the game.
     * @param turnOrder An empty list that will be updated during execution.
     */
    SetUpLogic(int num, List<Comm> turnOrder,CommonGround ground){
        this.record = new ConcurrentHashMap<>();
        this.nameToComm = new HashMap<>();
        this.availableColors = new HashSet<>(List.of(PawnColor.values()));
        this.numPlayers=num;
        this.turnOrder = turnOrder;
        this.ground = ground;
    }

    /**
     * Handles the joining of a player.
     *
     * @param comm The connection to the specific client.
     * @return The player created.
     */
    public Player addPlayer(Comm comm) {
        Map<String,Comm> tmp;
        String msg;
        StartingCard card;
        comm.sendMsg(MsgCreator.capacityMsg(numPlayers));
        String playerName = linkName(comm);
        record.put(comm, State.STARTING);
        synchronized (this.nameToComm) {
            tmp = new HashMap<>(this.nameToComm);
        }
        msg = MsgCreator.joinedMsg(tmp.keySet());
        Comm.sendBroadcast(msg,new ArrayList<>(tmp.values()));
        synchronized (this.setUpLock) {
            this.setUpLock.notifyAll();
            this.waitStart(State.STARTING);
        }
        synchronized (this.turnOrder){
            if(this.turnOrder.isEmpty()){
                this.turnOrder.addAll(this.record.keySet());
                List<String> names = new ArrayList<>(turnOrder.size());
                for(Comm iterComm:turnOrder){
                    String name = nameToComm.keySet().stream().filter(x-> nameToComm.get(x) == iterComm).findAny().orElse(null);
                    names.add(name);
                }
                Comm.sendBroadcast(MsgCreator.turnOrderMsg(names), turnOrder);
            }
        }
        synchronized(this.turnOrder){
            card = this.ground.getRandomStartingCard();
        }
        chooseStartingCard(comm, card);
        record.put(comm, State.COLOR);
        PawnColor color = chooseColor(comm, playerName);
        record.put(comm, State.OBJECTIVE);
        Objective obj = chooseObjective(comm);
        Player player = new Player(playerName, obj, color);
        player.placeStartingCard(card);
        msg = MsgCreator.updBoardMsg(turnOrder.indexOf(comm),0,0, new CardView(card),
                player.getInventory());
        Comm.sendBroadcast(msg, this.turnOrder);
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

        comm.sendMsg(MsgCreator.changePhaseMsg(Phase.NAME));
        do{
            comm.sendMsg(MsgCreator.chooseMsg());
            name = comm.getMsg();
            synchronized (nameToComm) {
                if (nameToComm.get(name) == null) {
                    nameToComm.put(name, comm);
                    available = false;
                }else{
                    comm.sendMsg(MsgCreator.confirmMsg(false, "Name already taken"));
                }
            }
        }while(available);
        String msg = MsgCreator.cat(MsgCreator.confirmMsg(true, "Valid Name"), MsgCreator.nameMsg(name));
        msg = MsgCreator.cat(msg, MsgCreator.changePhaseMsg(Phase.WAITFORPLAYERS));
        comm.sendMsg(msg);
        return name;
    }

    /**
     * Handles the process of choosing a starting card orientation.
     *
     * @param comm The connection to the specific client.
     * @param card The chosen orientation.
     */
    private void chooseStartingCard(Comm comm, StartingCard card) {
        CardView faceDown = new CardView(card);
        card.setShowingFace(true);
        CardView faceUp = new CardView(card);
        String cardToJson = MsgCreator.changePhaseMsg(Phase.STARTCARD,new CardView[] {faceDown, faceUp});
        String chooseOrientation = MsgCreator.chooseMsg();
        comm.sendMsg(MsgCreator.cat(cardToJson,chooseOrientation));
        card.setShowingFace(comm.getMsg().equals("1"));
    }

    /**
     * Handles the process of choosing a player's pawn color.
     *
     * @param comm The connection to the specific client.
     * @return The chosen color.
     */
    private PawnColor chooseColor(Comm comm, String playerName) {
        PawnColor color = null;
        boolean chosen;

        chosen = true;
        comm.sendMsg(MsgCreator.changePhaseMsg(Phase.COLOR));
        while (chosen) {
            comm.sendMsg(MsgCreator.chooseMsg());
            color = PawnColor.valueOf(comm.getMsg().toUpperCase());
            synchronized (this.availableColors) {
                if (availableColors.contains(color)) {
                    availableColors.remove(color);
                    chosen = false;
                } else {
                    comm.sendMsg(MsgCreator.confirmMsg(false, this.availableColors.toString()));
                }
            }
        }

        String msg = MsgCreator.confirmMsg(true, "Valid color");
        comm.sendMsg(msg);

        synchronized (this.availableColors) {
            msg = MsgCreator.updColorMsg(this.availableColors);
            Comm.sendBroadcast(MsgCreator.cat(msg, MsgCreator.colorChosenMsg(playerName, color)), this.turnOrder);
        }
        return color;
    }

    /**
     * Handles the process of choosing an objective.
     *
     * @param comm The connection to the specific client.
     * @return The chosen objective.
     */
    private Objective chooseObjective(Comm comm) {
        Objective[] objs;
        synchronized (this.ground) {
            objs = new Objective[] { this.ground.getRandomObjective(), this.ground.getRandomObjective()};
        }
        ObjectiveView[] objViews = new ObjectiveView[]{
            new ObjectiveView(objs[0]),
            new ObjectiveView(objs[1])
        };
        
        String s = MsgCreator.changePhaseMsg(Phase.OBJECTIVE, objViews);
        s = MsgCreator.cat(s, MsgCreator.chooseMsg());
        comm.sendMsg(s);
        s = comm.getMsg();
        Objective obj = objs[Integer.parseInt(s)];
        s = MsgCreator.changePhaseMsg(Phase.WAITFORPLAYERS);
        comm.sendMsg(s);
        return obj;
    }

    /**
     * Waits until every player is in the specified state.
     *
     * @param s The required state.
     */
    private void waitStart(State s) {
        synchronized (this.setUpLock) {
            Set<State> set = new HashSet<>(this.record.values());
            while (this.record.size() != this.numPlayers || !(set.contains(s) && set.size() == 1)) {
                try {
                    this.setUpLock.wait();
                } catch (InterruptedException e) {
                    new ProjectLogger(getClass().getSimpleName()).
                            log("logs/serverLog.txt",Level.WARNING,"Thread interrupted while waiting",e);
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
     * Sets the communication state to "READY" for the specified communication channel.
     *
     * @param comm The communication channel.
     */
    void setReady(Comm comm){
        record.put(comm,State.READY);
    }


    /**
     * Intended to be called from GameLogic to wait for the "Ready" state.
     */
    void waitReady() {
        waitStart(State.READY);
    }

    Set<Comm> getComms(){
        return this.record.keySet();
    }
}
