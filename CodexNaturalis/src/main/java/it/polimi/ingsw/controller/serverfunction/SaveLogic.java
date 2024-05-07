package it.polimi.ingsw.controller.serverfunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.model.gameplane.CommonGround;
import it.polimi.ingsw.model.gameplane.Player;

public class SaveLogic {
    private final List<Player> turnOrder;
    private final Map<Player, Comm> playerToComm;
    private final Map<String, Player> names;
    private final CommonGround ground;
    private final int currIdx;
    private final ArrayList<Comm> record;

    SaveLogic(Map<Comm,Player> players, List<Comm> turnOrder, Comm currentPlayer, CommonGround ground){
        this.turnOrder = new ArrayList<>();
        this.names = new HashMap<>();
        this.playerToComm = new HashMap<>();
        for(Comm c : turnOrder){
          this.turnOrder.add(players.get(c));
          this.names.put(players.get(c).getNickname(), players.get(c));
        }
        this.currIdx = turnOrder.indexOf(currentPlayer);
        this.ground = ground;
        this.record = new ArrayList<>(turnOrder.size());
    }

    private void loadSavedGame(CentralServer server){
        //CREATE GAMELOGIC
        Map<Comm,Player> players = new HashMap<>();
        List<Comm> order = new ArrayList<>();
        for(Player player : turnOrder){
            players.put(playerToComm.get(player),player);
            order.add(this.playerToComm.get(player));
        }
        new GameLogic(server, this.ground, players, order,this.playerToComm.get(this.turnOrder.get(currIdx)));
    }

    public void join(Comm comm, CentralServer server) {
      
      String name = "";
      boolean available = false;
      int rem;
      
      do{
        comm.sendMsg(MsgCreator.chooseMessage("Insert name (1-8 length, starts with a letter, no special characters): ",
            "^[a-zA-Z][a-zA-Z0-9]{0,7}$",
            "Invalid name: 1-8 length, starts with a letter, no special characters"));
        //TODO MAYBE EXCEPTION
        name = comm.getMsg();
        synchronized (this.playerToComm) {
          if (names.containsKey(name) && !this.playerToComm.containsKey(names.get(name))) {
            available = true;
          }
        }
      }while(!available);
      synchronized(this.playerToComm){
        comm.sendMsg(MsgCreator.displayMessage("Player Linked\n"));
        this.playerToComm.put(names.get(name), comm);
        rem = this.turnOrder.size() - playerToComm.size();
        this.playerToComm.notifyAll();
      }
      
      if(rem > 0){
        Comm.sendBroadcast(MsgCreator.displayMessage("Waiting for players to join\nRemaining: " + rem), record);
      } 
      if(comm == this.playerToComm.get(this.turnOrder.get(currIdx))){
        synchronized(this.playerToComm){
            try{
              while(rem!=0)
                this.playerToComm.wait();
            }catch(InterruptedException e){
              //TODO handle this
              throw new RuntimeException("Thread Interrupted");
            }
            this.loadSavedGame(server);
        }
      }
    }

    public boolean isFull(){
        return this.record.size()==this.turnOrder.size();
    }

    public void addRecord(Comm comm){
        this.record.add(comm);
    }
}
