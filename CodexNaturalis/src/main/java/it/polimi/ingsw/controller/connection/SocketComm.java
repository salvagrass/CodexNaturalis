package it.polimi.ingsw.controller.connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

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
   * Sends a message over the socket.
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
    //TODO CHANGE THIS WHILE LOOP
    String s;
    while(!this.clientCrashed.get()){
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
        //TODO LOG FILE
      }
    }
  }

  @Override
  public void stopPing(){
    super.stopPing();
    this.ackFlag.set(true);
  }

  @Override
  public void pingMethod(){
    synchronized(this.ackFlag){
      if(!this.ackFlag.get()){
        this.clientCrashed.set(true);
        close();
        this.handler.handleCrash(this);
        return;
      }
      this.ackFlag.set(false);
      sendMsg("PING");
    }
  }

  @Override
  public void close(){
    try {
      this.in.close();
      this.out.close();
      this.socket.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      System.out.println("Can't close the connection");
    }
  }
}

