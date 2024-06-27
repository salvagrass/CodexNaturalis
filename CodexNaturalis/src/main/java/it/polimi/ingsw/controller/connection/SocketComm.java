package it.polimi.ingsw.controller.connection;

import it.polimi.ingsw.util.ProjectLogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * Extends Comm to implement a socket connection.
 */
public class SocketComm extends Comm {

  private final Socket socket;
  private final Scanner in;
  private final PrintWriter out;
  private final AtomicBoolean ackFlag;

  /**
   * Constructs a new SocketComm instance.
   *
   * @param socket the socket to use for communication.
   * @throws IOException if a I/O-related exception occurs during initialization.
   */
  public SocketComm(Socket socket) throws IOException {
    super();
    this.socket = socket;
    this.in = new Scanner(socket.getInputStream());
    this.out = new PrintWriter(socket.getOutputStream());
    this.ackFlag = new AtomicBoolean(true);
    new Thread(this::waitMsg).start();
  }

  /**
   * Sends a message via socket.
   *
   * @param msg The message to send.
   */
  @Override
  public void sendMsg(String msg) {
    if (!socket.isClosed()) {
      out.println(msg);
      out.flush();
    }
  }

  /**
   * Waits for a Client's command and handles it.
   */
  private void waitMsg() {
    String s;
    boolean keepGoing = true;
    while(!this.clientCrashed.get() && keepGoing){
      try{
        synchronized(this.in) {
          s = in.nextLine();
        }
        switch (s.split(";")[0]) {
          case "PLACECARD" -> this.getGame().placeCard(this, s);
          case "DRAWCARD" -> this.getGame().drawCard(this, s);
          case "CHAT" -> this.getGame().chat(s);
          case "ACK" -> {
            synchronized(this.ackFlag){
              this.ackFlag.set(true);
            }
          }
          default -> super.setChosen(s);
        }
      }catch(NoSuchElementException e){
        new ProjectLogger(getClass().getSimpleName()).
                log("logs/commLog.txt",Level.INFO,"Socket either closed or unable to retrieve data",e);
        keepGoing = false;
      }
    }
  }

  /**
   * Stops the ping operation.
   */
  @Override
  public void stopPing(){
    super.stopPing();
    this.ackFlag.set(true);
  }

  /**
   * Sends a ping message and handles client crashes.
   * Sends a ping message to the client and handles potential client crashes.
   * This method sends a "PING" message to the client and waits for a response, if the
   * client doesn't respond in the allotted time it is assumed that  it has crashed.
   * In such a case the method calls the crash handler handle method
   * @see it.polimi.ingsw.controller.serverfunction.CrashHandler
   */
  @Override
  public void pingMethod(){
    synchronized(this.ackFlag){
      if(!this.ackFlag.get()){
        this.clientCrashed.set(true);
        stopPing();
        close();
        this.handler.handleCrash(this);
        return;
      }
      this.ackFlag.set(false);
      sendMsg("PING");
    }
  }

  /**
   * Closes the connection with the socket
   */
  @Override
  public void close(){
    try {
      this.in.close();
      this.out.close();
      this.socket.close();
    } catch (IOException e) {
      new ProjectLogger(getClass().getSimpleName()).
              log("logs/commLog.txt",Level.WARNING,"Can't open popup",e);
    }
  }
}

