package it.polimi.ingsw.controller.serverfunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.model.gameplane.Player;
import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.ObjectiveSerializeLogic;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.util.json.CardSerializeLogic;
import it.polimi.ingsw.util.json.SaveData;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class LoadLogic implements CrashHandler {
    private final CentralServer server;
    private final ExecutorService executor;
    private final SaveData data;
    private final Set<String> names;
    private final Map<String,Comm> nameToComm;
    private final Set<Comm> record;



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

    private void createGameLogic(){
        Map<Comm,Player> players = new HashMap<>();
        List<Comm> turnOrder = new ArrayList<>(this.nameToComm.size());
        for(Player player: data.getTurnOrder()){
            String name = player.getNickname();
            Comm comm = this.nameToComm.get(name);
            turnOrder.add(comm);
            players.put(comm,player);
        }
        GameLogic gc = new GameLogic(this.server,this.data.getId(),this.data.getGround(),players,turnOrder,this.nameToComm.get(data.getCurrPlayer()));
        for(Comm comm : this.record){
            comm.setCrashHandler(gc);
        }
        this.server.addGame(data.getId(),gc);
        gc.submitLoad();
    }


    public synchronized void addRecord(Comm comm){
        this.record.add(comm);
    }

    public synchronized boolean isFull(){
        return this.record.size() == this.data.getTurnOrder().size();
    }

    public synchronized void handleCrash(Comm comm){
        executor.shutdownNow();
        for(Comm iter: this.record){
            iter.setCrashHandler(this.server);
        }
        this.record.remove(comm);
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
