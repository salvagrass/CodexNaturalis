package it.polimi.ingsw.controller.userclient;

import it.polimi.ingsw.controller.serverfunction.MsgCreator;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.view.UserView;
import it.polimi.ingsw.controller.connection.RemoteComm;
import it.polimi.ingsw.controller.serverfunction.RemoteServer;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientRMI extends Client implements ClientRemote {
  private RemoteComm comm;
  private final ExecutorService clientExecutor;
  private final ExecutorService serverExecutor;
  private final ScheduledExecutorService scheduledExecutor;
  private final AtomicBoolean pingFlag;

  public ClientRMI(UserView userView) throws RemoteException{
    super(userView);
    UnicastRemoteObject.exportObject(this,0);
    this.clientExecutor = Executors.newSingleThreadExecutor();
    this.serverExecutor = Executors.newSingleThreadExecutor();
    this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    this.pingFlag = new AtomicBoolean(true);
    this.port = 1099;
  }

  @Override
  public void connect(String addr) {
    try {
      Registry registry = LocateRegistry.getRegistry(addr, this.port);
      RemoteServer server = (RemoteServer) registry.lookup("RMIServer");
      RemoteComm tmp = server.join(this);
      synchronized(this){
        this.comm = tmp;
        this.notifyAll();
      }
      scheduledExecutor.scheduleAtFixedRate(this::waitForPing,0,15, TimeUnit.SECONDS);
    } catch (Exception e) {
      // TODO log file
      throw new RuntimeException();
    }
  }

  @Override
  void sendMsg(String msg) {
    try {
      synchronized (this) {
        while (this.comm == null) {
          try {
            this.wait();
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }  
      }
      this.comm.setMsg(msg);
    } catch (RemoteException e) {
        this.pingFlag.set(false);
        waitForPing();
    }
  }

  @Override
  public void placeCard(int idx, int x, int y,boolean face) {
    //TODO handle exception
    this.clientExecutor.submit(()->{
      try{
        comm.placeCard(MsgCreator.placeCardMsg(idx, x, y, face));
      }catch(RemoteException e){
        this.pingFlag.set(false);
        waitForPing();
      }
    });
  }

  @Override
  public void drawCard(int idx) {
    this.clientExecutor.submit(()->{
      try{
        comm.drawCard(MsgCreator.drawCardMsg(idx));
      }catch(RemoteException e){
        this.pingFlag.set(false);
        waitForPing();
      }
    });
  }

  @Override
  public void sendChatMessage(String msg) {
      this.clientExecutor.submit(() -> {
        try {
          this.comm.chat(msg);
        } catch (RemoteException e) {
          this.pingFlag.set(false);
          waitForPing();
        }
      });
  }

  @Override
  public void handleMsgRemote(String message) throws RemoteException {
    serverExecutor.submit(()->{
      if(message.equals("PING")){
        pingFlag.set(true);
      }
      super.handleMsg(message);
    });
  }

  private void waitForPing(){
    synchronized(this.pingFlag){
      if(!this.pingFlag.get()){
        super.handleMsg(MsgCreator.changePhaseMsg(Phase.SERVERCRASHED));
      }
      this.pingFlag.set(false);
    }
  }

  @Override
  public void closeConnection() {
    this.comm = null;
    serverExecutor.shutdownNow();
    clientExecutor.shutdownNow();
    scheduledExecutor.shutdownNow();
    try {
      UnicastRemoteObject.unexportObject(this, true);
    } catch (NoSuchObjectException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
