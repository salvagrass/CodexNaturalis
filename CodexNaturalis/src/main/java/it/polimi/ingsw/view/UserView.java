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

    public void connect(boolean type) throws RemoteException {
        this.client = type ? new ClientSocket(this) : new ClientRMI(this);
    }

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

    public void setCapacity(int capacity){
        synchronized(this.modelLock){
            this.modelView.allocateBoards(capacity);
        }
    }
    
    public abstract void playerJoined(Set<String> players);

    public void setPlayerColor(String player, PawnColor color){
        this.colorsChosen.put(player, color);
    }

    public void updColors(Set<PawnColor> newColors){
        synchronized(this.colorLock){
            this.colors.retainAll(newColors);
        }
    }

    public void setHand(List<CardView> hand){
        synchronized(this.modelLock){
            this.modelView.setHand(hand);
        }
    }

    public void createGroundView(CardView[] resCards, CardView[] goldCards, ObjectiveView[] objs){
        synchronized(this.modelLock){
            this.modelView.setGround(new GroundView(resCards,goldCards,objs));
        }
    }

    public void loadObj(ObjectiveView objectiveView){
        this.modelView.setObj(objectiveView);
    }

    public void loadBoard(int idx,BoardView boardView){
        this.modelView.setBoard(idx,boardView);
    }

    public void setNames(List<String> turnOrder){
        this.turnOrder.addAll(turnOrder);
        synchronized(this.leaderboardLock){
            for(String name : turnOrder){
                this.leaderboard.put(name,0);
            }
        }
    }

    public void updHandView(CardView card){
        synchronized(this.modelLock){
            this.modelView.updHand(card,this.idx);
        }
    }

    public void updGroundView(int idx,CardView card){
        synchronized(this.modelLock){
            this.modelView.updGround(idx,card);
        }
    }

    public void updBoardView(int turnIdx, CardView card, int x, int y, Map<Symbol,Integer> inventory){
        synchronized(this.modelLock){
            this.modelView.getBoard(turnIdx).updBoardView(card,x,y,inventory);
        }
    }

    public void updPoints(int turnIdx,int newPoints){
        synchronized(this.leaderboardLock){
            this.leaderboard.put(this.turnOrder.get(turnIdx),newPoints);
        }
    }

    public void setName(String name){
        this.name = name;
    }

    public abstract void setCurrPlayer(String currPlayer);

    public void addChatMessage(Message msg) {
        synchronized(this.chatLock){
            this.chats.add(msg);
        }
    }

    public void sendConfirm(boolean confirmValue,String confirmMsg){
        synchronized(this.confirmValue){
            this.confirmValue.set(confirmValue);
            this.confirmMsg = confirmMsg;
        }
    }
    
    public void setCurrPhase(Phase phase, ItemView... items){
        switch(phase){
            case Phase.STARTCARD -> this.starts = Arrays.copyOf(items,items.length,CardView[].class);
            case Phase.OBJECTIVE -> this.scrtObjs = Arrays.copyOf(items,items.length,ObjectiveView[].class);
        }
    }

    public void setPositions(Map<String, Integer> positions) {
        synchronized (this.positions) {
            this.positions.putAll(positions);
            this.positions.notifyAll();
        }
    }

    public void tryConnection(String addr) throws RuntimeException{
        this.client.connect(addr);
    }

    public void scanInput(String toScan){
        synchronized(this.toSendlock){
            this.toSend = toScan.trim();
            this.toSendlock.notifyAll();
        }
    }

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




    public abstract void changePhase(Phase phase);
}
