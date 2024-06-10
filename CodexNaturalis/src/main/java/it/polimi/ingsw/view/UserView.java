package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.userclient.Client;
import it.polimi.ingsw.controller.userclient.ClientRMI;
import it.polimi.ingsw.controller.userclient.ClientSocket;
import it.polimi.ingsw.util.Message;
import it.polimi.ingsw.util.PawnColor;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.util.Symbol;
import it.polimi.ingsw.view.coreview.*;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class UserView {
    final Client client;
    ModelView modelView;
    Set<PawnColor> colors;
    Map<String,Integer> leaderboard;
    final Map<String,Integer> positions;
    Set<String> playersInLobby;
    final AtomicBoolean confirmValue;
    String confirmMsg;
    List<String> turnOrder;
    Map<String, PawnColor> colorsChosen;
    String name;
    Phase currPhase;
    CardView[] starts;
    ObjectiveView[] scrtObjs;

    List<Message> chats;

    String toSend;
    int idx, tmpIdx;
    final AtomicBoolean waitForPlayers;
    final Object toSendlock;
    final Object waitPhaseLock;
    final Object currPlayerLock;
    

    public UserView(boolean type) throws RemoteException{
        this.client = type ? new ClientSocket(this) : new ClientRMI(this);
        this.modelView = new ModelView();
        this.confirmValue = new AtomicBoolean(false);
        this.positions = new HashMap<>();
        this.toSend = null;
        this.waitForPlayers = new AtomicBoolean(false);
        this.toSendlock = new Object();
        this.waitPhaseLock = new Object();
        this.currPlayerLock = new Object();
        this.colorsChosen = new HashMap<>();
    }

    public String getInput(){
        synchronized(this.toSendlock) {
            while(this.toSend == null) {
                try {
                    this.toSendlock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            String tmp = this.toSend;
            this.toSend = null;
            return tmp;
        }
    }

    public void setCapacity(int capacity){
        synchronized(this.modelView){
            this.modelView.allocateBoards(capacity);
        }
    }
    
    public abstract void playerJoined(Set<String> players);

    public void setPlayerColor(String player, PawnColor color){
        this.colorsChosen.put(player, color);
    }

    public void updColors(Set<PawnColor> newColors){
        synchronized(this.colors){
            this.colors.retainAll(newColors);
        }
    }

    public void setHand(List<CardView> hand){
        synchronized(this.modelView){
            this.modelView.setHand(hand);
        }
    }

    public void createGroundView(CardView[] resCards, CardView[] goldCards, ObjectiveView[] objs){
        synchronized(this.modelView){
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
        synchronized(this.leaderboard){
            for(String name : turnOrder){
                this.leaderboard.put(name,0);
            }
        }
    }

    public void updHandView(CardView card){
        synchronized(this.modelView){
            this.modelView.updHand(card,this.idx);
        }
    }

    public void updGroundView(int idx,CardView card){
        synchronized(this.modelView){
            this.modelView.updGround(idx,card);
        }
    }

    public void updBoardView(int turnIdx, CardView card, int x, int y, Map<Symbol,Integer> inventory){
        synchronized(this.modelView){
            this.modelView.getBoard(turnIdx).updBoardView(card,x,y,inventory);
        }
    }

    public void updPoints(int turnIdx,int newPoints){
        synchronized(this.leaderboard){
            this.leaderboard.put(this.turnOrder.get(turnIdx),newPoints);
        }
    }

    public void setName(String name){
        this.name = name;
    }

    public abstract void setCurrPlayer(String currPlayer);

    public void addChatMessage(Message msg) {
        synchronized(this.chats){
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

    public void tryConnection(String addr){
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
