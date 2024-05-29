package it.polimi.ingsw.controller.userclient;

import it.polimi.ingsw.controller.serverfunction.MsgCreator;
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

public class ClientRMI extends Client implements ClientRemote {
  private RemoteComm comm;
  private final ExecutorService executor;

  public ClientRMI(UserView userView) throws RemoteException{
    super(userView);
    UnicastRemoteObject.exportObject(this,0);
    this.executor = Executors.newFixedThreadPool(1);
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
    } catch (Exception e) {
      // TODO log file
      e.printStackTrace();
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
      System.out.println("Server crashed");
      throw new RuntimeException();
    }
  }

  @Override
  public void placeCard(int idx, int x, int y,boolean face) {
    //TODO handle exception
    this.executor.submit(()->{
      try{
        comm.placeCard(MsgCreator.placeCardMsg(idx, x, y, face));
      }catch(RemoteException e){
        System.out.println("Server crashed");
        throw new RuntimeException();
      }
    });
  }

  @Override
  public void drawCard(int idx) {
    this.executor.submit(()->{
      try{
        comm.drawCard(MsgCreator.drawCardMsg(idx));
      }catch(RemoteException e){
        System.out.println("Server crashed");
        throw new RuntimeException();
      }
    });
  }

  @Override
  public void sendChatMessage(String msg) {
      this.executor.submit(() -> {
        try {
          this.comm.chat(msg);
        } catch (RemoteException e) {
          System.out.println("Server crashed");
          throw new RuntimeException(e);
        }
      });
  }

  @Override
  public void handleMsgRemote(String message) throws RemoteException {
    super.handleMsg(message);
  }

  @Override
  public void closeConnection() {
    this.comm = null;
    try {
      UnicastRemoteObject.unexportObject(this, true);
    } catch (NoSuchObjectException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
