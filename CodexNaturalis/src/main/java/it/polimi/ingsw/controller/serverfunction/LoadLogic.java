package it.polimi.ingsw.controller.serverfunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.model.gameplane.Player;
import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.ProjectLogger;
import it.polimi.ingsw.util.json.ObjectiveSerializeLogic;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.util.json.CardSerializeLogic;
import it.polimi.ingsw.util.json.SaveData;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class LoadLogic implements CrashHandler {
    private final CentralServer server;
    private final ExecutorService executor;
    private final SaveData data;
    private final Set<String> names;
    private final Map<String,Comm> nameToComm;
    private final Set<Comm> record;


    /**
     * Constructs a new LoadLogic instance.
     *
     * @param server The central server.
     * @param id The identifier for loading data.
     * @throws IOException If an I/O error occurs.
     */
    public LoadLogic(CentralServer server,String id) throws IOException{
        this.server = server;
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Card.class, new CardSerializeLogic())
                .registerTypeHierarchyAdapter(Objective.class, new ObjectiveSerializeLogic())
                .create();
        FileReader reader = new FileReader(id+"_saved.json");
        this.data = gson.fromJson(reader,SaveData.class);
        reader.close();
        this.names = data.getTurnOrder().stream().map(Player::getNickname).collect(Collectors.toSet());
        this.nameToComm = new HashMap<>();
        this.record = new HashSet<>();
        this.executor = Executors.newFixedThreadPool(names.size());
    }

    /**
     * Links a player name to the communication object.
     *
     * @param comm The communication object.
     */
    void submitLinkName(Comm comm){
        this.executor.submit(()->linkName(comm));
    }
    private void linkName(Comm comm){
        String msg;
        String name = null;
        boolean available = false;
        comm.sendMsg(MsgCreator.capacityMsg(this.data.getTurnOrder().size()));
        comm.sendMsg(MsgCreator.changePhaseMsg(Phase.NAME));
        while(!available){
            comm.sendMsg(MsgCreator.chooseMsg());
            name = comm.getMsg();
            synchronized(this.names){
                if(!names.contains(name)){
                    msg = MsgCreator.confirmMsg(false,"Name not found or already taken");
                    comm.sendMsg(msg);
                }else{
                    available = true;
                    names.remove(name);
                    this.nameToComm.put(name,comm);
                    msg = MsgCreator.confirmMsg(true,"Player successfully linked");
                    comm.sendMsg(msg);
                    this.names.notifyAll();
                }
            }
        }

        comm.sendMsg(MsgCreator.changePhaseMsg(Phase.WAITFORPLAYERS));
        synchronized(this.names){
            comm.sendMsg(MsgCreator.joinedMsg(this.nameToComm.keySet()));
            while(!this.names.isEmpty()){
                try {
                    this.names.wait();
                } catch (InterruptedException e) {
                    new ProjectLogger(getClass().getSimpleName()).
                            log("logs/serverLog.txt",Level.WARNING,"Thread interrupted while waiting",e);
                    // TODO I COULD SEND YOU BACK TO THE MENU
                    throw new RuntimeException(e);
                }
            }
        }

        comm.sendMsg(MsgCreator.nameMsg(name));

        if(name.equals(data.getCurrPlayer())){
            createGameLogic();
        }
    }

    /**
     * Creates a new game logic instance and initializes the game.
     */
    private void createGameLogic(){
        Map<Comm,Player> players = new HashMap<>();
        List<Comm> turnOrder = new ArrayList<>(this.nameToComm.size());
        for(Player player: data.getTurnOrder()){
            String name = player.getNickname();
            Comm comm = this.nameToComm.get(name);
            turnOrder.add(comm);
            players.put(comm,player);
        }
        GameLogic gc = new GameLogic(this.server,this.data.getId(),this.data.getGround(),players,turnOrder,this.nameToComm.get(data.getCurrPlayer()),this.data.isEndGame());
        for(Comm comm : this.record){
            comm.setCrashHandler(gc);
        }
        this.server.addGame(data.getId(),gc);
        gc.submitLoad();
    }

    /**
     * Adds a communication object to the record.
     *
     * @param comm The communication object to add.
     */
    public synchronized void addRecord(Comm comm){
        this.record.add(comm);
    }

    /**
     * Checks whether the record is full (contains all players).
     *
     * @return {@code true} if the record is full, {@code false} otherwise.
     */
    public synchronized boolean isFull(){
        return this.record.size() == this.data.getTurnOrder().size();
    }

    /**
     * Handles a crash event for the given communication object.
     *
     * @param comm The communication object that crashed.
     */
    public synchronized void handleCrash(Comm comm){
        executor.shutdownNow();
        this.record.remove(comm);
        this.server.handleCrash(comm);
        for(Comm iter: this.record){
            iter.setCrashHandler(this.server);
        }
        String msg = MsgCreator.changePhaseMsg(Phase.CLIENTCRASHED);
        Comm.sendBroadcast(msg,new ArrayList<>(this.record));
        this.server.removeGame(this.data.getId());
        for(Comm iter: this.record){
            //CLEAN THE PENDING MESSAGES
            iter.clean();
            this.server.submitClient(iter);
        }
    }
}
