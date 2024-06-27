package it.polimi.ingsw.controller.userclient;

import it.polimi.ingsw.controller.serverfunction.MsgCreator;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.util.ProjectLogger;
import it.polimi.ingsw.view.UserView;
import it.polimi.ingsw.controller.connection.RemoteComm;
import it.polimi.ingsw.controller.serverfunction.RemoteServer;

import java.net.InetAddress;
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
import java.util.logging.Level;

/**
 * Class that represents a client connected to the server via RMI
 */
public class ClientRMI extends Client implements ClientRemote {
  private RemoteComm comm;
  private final ExecutorService clientExecutor;
  private final ExecutorService serverExecutor;
  private final ScheduledExecutorService scheduledExecutor;
  private final AtomicBoolean pingFlag;

  /**
   * Represents a client for the RMI (Remote Method Invocation) system.
   * It exports the object as an RMI remote object and initializes various executors.
   *
   * @param userView The `UserView` object associated with this client.
   * @throws RemoteException If there's an issue with remote communication.
   */
  public ClientRMI(UserView userView) throws RemoteException{
    super(userView);
    UnicastRemoteObject.exportObject(this,0);
    this.clientExecutor = Executors.newSingleThreadExecutor();
    this.serverExecutor = Executors.newSingleThreadExecutor();
    this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    this.pingFlag = new AtomicBoolean(true);
    this.port = 1099;
  }

  /**
   * Connects to the RMI server at the specified address.
   * This method sets the system property for the RMI server hostname,
   * looks up the remote server, and initializes communication with it.
   *
   * @param addr The address of the RMI server.
   * @throws RuntimeException If there's an issue during connection.
   */
  @Override
  public void connect(String addr) {
    try {
      System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
      Registry registry = LocateRegistry.getRegistry(addr, this.port);
      RemoteServer server = (RemoteServer) registry.lookup("RMIServer");
      RemoteComm tmp = server.join(this);
      synchronized(this){
        this.comm = tmp;
        this.notifyAll();
      }
      scheduledExecutor.scheduleAtFixedRate(this::waitForPing,0,15, TimeUnit.SECONDS);
    } catch (Exception e) {
      new ProjectLogger(getClass().getSimpleName()).
              log("logs/clientLog.txt",Level.SEVERE,"Problem with connection",e);
      throw new RuntimeException();
    }
  }

  /**
   * Sends a message to the RMI server.
   * This method waits for communication to be established with the server
   * and then sets the message using the associated communication object.
   *
   * @param msg The message to be sent.
   */
  @Override
  void sendMsg(String msg) {
    try {
      synchronized (this) {
        while (this.comm == null) {
          try {
            this.wait();
          } catch (InterruptedException e) {
            new ProjectLogger(getClass().getSimpleName()).
                    log("logs/clientLog.txt",Level.SEVERE,"Problem with connection",e);
            throw new RuntimeException(e);
          }
        }  
      }
      this.comm.setMsg(msg);
    } catch (RemoteException e) {
        this.pingFlag.set(false);
        waitForPing();
    }
  }

  /**
   * Places a card on the game board.
   * This method submits the task to the client executor, which communicates with the server
   * by invoking the `placeCardMsg` method on the communication object.
   *
   * @param idx   The index of the card.
   * @param x     The x-coordinate on the game board.
   * @param y     The y-coordinate on the game board.
   * @param face  Whether the card is face-up or face-down.
   */
  @Override
  public void placeCard(int idx, int x, int y,boolean face) {
    this.clientExecutor.submit(()->{
      try{
        comm.placeCard(MsgCreator.placeCardMsg(idx, x, y, face));
      }catch(RemoteException e){
        this.pingFlag.set(false);
        waitForPing();
      }
    });
  }

  /**
   * Draws a card from the game deck.
   * This method submits the task to the client executor, which communicates with the server
   * by invoking the `drawCardMsg` method on the communication object.
   *
   * @param idx The index of the card to be drawn.
   */
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

  /**
   * Sends a chat message.
   *
   * @param msg The message to send.
   */
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

  /**
   * Handles a remote chat message.
   *
   * @param message The incoming message.
   * @throws RemoteException If a remote communication error occurs.
   */
  @Override
  public void handleMsgRemote(String message) throws RemoteException {
    serverExecutor.submit(()->{
      if(message.equals("PING")){
        pingFlag.set(true);
      }
      super.handleMsg(message);
    });
  }

  /**
   * Waits for a ping response.
   * If no ping is received, changes the server phase to SERVERCRASHED.
   */
  private void waitForPing(){
    synchronized(this.pingFlag){
      if(!this.pingFlag.get()){
        super.handleMsg(MsgCreator.changePhaseMsg(Phase.SERVERCRASHED));
      }
      this.pingFlag.set(false);
    }
  }

  /**
   * Closes the connection.
   * Shuts down executors and unexports the object.
   */
  @Override
  public void closeConnection() {
    this.comm = null;
    serverExecutor.shutdownNow();
    clientExecutor.shutdownNow();
    scheduledExecutor.shutdownNow();
    try {
      UnicastRemoteObject.unexportObject(this, true);
    } catch (NoSuchObjectException e) {
      new ProjectLogger(getClass().getSimpleName()).
              log("logs/clientLog.txt",Level.WARNING,"Can't find RMI object",e);
    }
  }
}
