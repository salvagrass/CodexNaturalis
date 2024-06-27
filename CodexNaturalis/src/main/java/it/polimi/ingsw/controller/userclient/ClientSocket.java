package it.polimi.ingsw.controller.userclient;

import it.polimi.ingsw.controller.serverfunction.MsgCreator;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.util.ProjectLogger;
import it.polimi.ingsw.view.UserView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class ClientSocket extends Client{
  private Socket socket;
  private Scanner in;
  private PrintWriter out;
  private final AtomicBoolean stopConnection;
  private final AtomicBoolean pingFlag;

  ExecutorService executor;
  ScheduledExecutorService scheduled;

  /**
   * Constructs a ClientSocket.
   *
   * @param userView The user view associated with this client.
   */
  public ClientSocket(UserView userView){
    super(userView);
    this.stopConnection = new AtomicBoolean(false);
    this.pingFlag = new AtomicBoolean(true);
    this.port = 5432;
    this.executor = Executors.newSingleThreadExecutor();
    this.scheduled = Executors.newSingleThreadScheduledExecutor();
  }

  /**
   * Establishes a socket connection to the specified address.
   *
   * @param addr The address to connect to.
   * @throws RuntimeException If an I/O error occurs during connection setup.
   */
  @Override
  public void connect(String addr) {
    this.socket = null;
    try {
      socket = new Socket(addr, this.port);
      this.in = new Scanner(socket.getInputStream());
      this.out = new PrintWriter(socket.getOutputStream());
    } catch (IOException e) {
      new ProjectLogger(getClass().getSimpleName()).
              log("logs/clientLog.txt",Level.SEVERE,"Can't connect to the server",e);
      throw new RuntimeException(e);
    }
    scheduled.scheduleAtFixedRate(this::waitForPing,0,15, TimeUnit.SECONDS);
    new Thread(this::listener).start();
  }

  /**
   * Waits for a ping response.
   * If no ping is received, changes the server phase to SERVERCRASHED.
   */
  private void waitForPing(){
      synchronized(this.pingFlag){
        if(!this.pingFlag.get()){
          handleMsg(MsgCreator.changePhaseMsg(Phase.SERVERCRASHED));
        }
        this.pingFlag.set(false);
    }
  }

  /**
   * Handles incoming messages.
   *
   * @param msg The message to process.
   */
  @Override
  public void handleMsg(String msg){
    if(msg.equals("PING")) {
      sendMsg("ACK");
      this.pingFlag.set(true);
    }
    else {
      executor.submit(() -> super.handleMsg(msg));
    }
  }

  /**
   * Listens for incoming messages and handles them.
   * Stops when the connection is requested to be stopped.
   */
  private void listener(){
    while (!stopConnection.get()){
      try{
        handleMsg(this.in.nextLine());
      }catch(IllegalStateException | NoSuchElementException e){
        new ProjectLogger(getClass().getSimpleName()).
                log("logs/clientLog.txt",Level.INFO,"Closed connection",e);
        stopConnection.set(true);
      }
    }
  }

  /**
   * Sends a message to the output stream.
   *
   * @param msg The message to send.
   */
  @Override
  void sendMsg(String msg) {
    synchronized(this) {
      this.out.println(msg);
      this.out.flush();
    }
  }

  /**
   * Places a card at the specified position.
   *
   * @param idx The index of the card.
   * @param x   The x-coordinate of the position.
   * @param y   The y-coordinate of the position.
   * @param face Whether the card is face-up or face-down.
   */
  @Override
  public void placeCard(int idx, int x, int y,boolean face) {
    sendMsg(MsgCreator.placeCardMsg(idx,x,y,face));
  }

  /**
   * Draws a card with the specified deck index.
   *
   * @param idx The index of the deck.
   */
  @Override
  public void drawCard(int idx) {
    sendMsg(MsgCreator.drawCardMsg(idx));
  }

  /**
   * Sends a chat message to the output stream.
   *
   * @param msg The chat message to send.
   */
  @Override
  public void sendChatMessage(String msg) {
    this.out.println(msg);
  }

  /**
   * Closes the connection and performs necessary cleanup tasks.
   */
  @Override
  public void closeConnection(){
    this.stopConnection.set(true);
    try{
      scheduled.shutdownNow();
      executor.shutdownNow();
      scheduled = Executors.newSingleThreadScheduledExecutor();
      executor = Executors.newSingleThreadExecutor();
      socket.close();
      in.close();
      out.close();
    }catch(IOException e){
      new ProjectLogger(getClass().getSimpleName()).
              log("logs/clientLog.txt",Level.WARNING,"Can't close the socket",e);
    }
  }
}
