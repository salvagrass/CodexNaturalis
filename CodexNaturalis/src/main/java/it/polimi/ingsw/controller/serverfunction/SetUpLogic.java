package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.model.gameplane.CommonGround;
import it.polimi.ingsw.model.gameplane.Player;
import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.PawnColor;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.view.coreview.CardView;
import it.polimi.ingsw.view.coreview.ObjectiveView;

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
    private final Set<PawnColor> availableColors;
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
        this.availableColors = new HashSet<>(List.of(PawnColor.values()));
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
        Map<String,Comm> tmp;
        String msg;
        StartingCard card;
        game.sendMsg(comm, MsgCreator.capacityMsg(numPlayers));
        String playerName = linkName(comm);
        record.put(comm, State.STARTING);
        synchronized (this.nameToComm) {
            tmp = new HashMap<>(this.nameToComm);
        }
        msg = MsgCreator.joinedMsg(tmp.keySet());
        Comm.sendBroadcast(msg,new ArrayList<>(tmp.values()));
        synchronized (this) {
            this.notifyAll();
            this.waitStart(State.STARTING);
        }
        synchronized (this.turnOrder){
            if(this.turnOrder.isEmpty()){
                this.turnOrder.addAll(this.record.keySet());
                List<String> names = new ArrayList<>(turnOrder.size());
                //TODO REVISE
                for(Comm iterComm:turnOrder){
                    String name = nameToComm.keySet().stream().filter(x-> nameToComm.get(x) == iterComm).findAny().get();
                    names.add(name);
                }
                Comm.sendBroadcast(MsgCreator.turnOrderMsg(names), turnOrder);
            }
        }
        synchronized(this.turnOrder){
            card = ground.getRandomStartingCard();
        }
        chooseStartingCard(comm, card);
        record.put(comm, State.COLOR);
        PawnColor color = chooseColor(comm);
        record.put(comm, State.OBJECTIVE);
        Objective obj = chooseObjective(comm,ground);
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

        game.sendMsg(comm,MsgCreator.changePhaseMsg(Phase.NAME));
        do{
            game.sendMsg(comm,MsgCreator.chooseMsg());
            name = game.getMsg(comm);
            synchronized (nameToComm) {
                if (nameToComm.get(name) == null) {
                    nameToComm.put(name, comm);
                    available = false;
                }else{
                    game.sendMsg(comm,MsgCreator.confirmMsg(false, "Name already taken"));
                }
            }
        }while(available);
        String msg = MsgCreator.cat(MsgCreator.confirmMsg(true, "Valid Name"), MsgCreator.nameMsg(name));
        game.sendMsg(comm, msg);
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
        CardView faceUp = new CardView(card);;
        String cardToJson = MsgCreator.changePhaseMsg(Phase.STARTCARD,new CardView[] {faceDown, faceUp});
        String chooseOrientation = MsgCreator.chooseMsg();
        game.sendMsg(comm,MsgCreator.cat(cardToJson,chooseOrientation));
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
        game.sendMsg(comm,MsgCreator.changePhaseMsg(Phase.COLOR));
        while (chosen) {
            game.sendMsg(comm, MsgCreator.chooseMsg());
            color = PawnColor.valueOf(game.getMsg(comm).toUpperCase());
            synchronized (this.availableColors) {
                if (availableColors.contains(color)) {
                    availableColors.remove(color);
                    chosen = false;
                } else {
                    game.sendMsg(comm, MsgCreator.confirmMsg(false, this.availableColors.toString()));
                }
            }
        }

        game.sendMsg(comm, MsgCreator.confirmMsg(true, "Valid color"));

        synchronized (this.availableColors) {
            Comm.sendBroadcast(MsgCreator.updColorMsg(this.availableColors), this.turnOrder);
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
        synchronized (ground) {
            objs = new Objective[] { ground.getRandomObjective(), ground.getRandomObjective()};
        }
        ObjectiveView[] objViews = new ObjectiveView[]{
            new ObjectiveView(objs[0]),
            new ObjectiveView(objs[1])
        };
        
        String s = MsgCreator.changePhaseMsg(Phase.OBJECTIVE, objViews);
        s = MsgCreator.cat(s, MsgCreator.chooseMsg());
        game.sendMsg(comm, s);
        s = game.getMsg(comm);
        Objective obj = objs[Integer.parseInt(s)];
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


    void setReady(Comm comm){
        record.put(comm,State.READY);
    }


    /**
     * Intended to be called from GameLogic to wait for the "Ready" state.
     */
    void waitReady() {
        waitStart(State.READY);
    }
}
