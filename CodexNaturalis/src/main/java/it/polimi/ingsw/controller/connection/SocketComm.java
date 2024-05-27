package it.polimi.ingsw.controller.connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Extends Comm to implement a socket connection.
 */
public class SocketComm extends Comm {

  private final Socket socket;
  private final Scanner in;
  private final PrintWriter out;

  /**
   * Constructs a new SocketComm instance.
   *
   * @param socket the socket to use for communication.
   * @throws IOException if an I/O-related exception occurs during initialization.
   */
  public SocketComm(Socket socket) throws IOException {
    this.socket = socket;
    this.in = new Scanner(socket.getInputStream());
    this.out = new PrintWriter(socket.getOutputStream());
  }

  /**
   * Sends a message over the socket.
   *
   * @param msg The message to send.
   */
  @Override
  public void sendMsg(String msg){
    if(!socket.isClosed()) {
      out.println(msg);
      out.flush();
    }else
      System.out.println("socket closed");
  }

  /**
   * Retrieves a message from the socket.
   *
   * @return The message read from the socket.
   */
  @Override
  public String getMsg() {
    if(!socket.isClosed()) {
      return in.nextLine();
    }else
      return "Socket closed";
  }

  /**
   * Waits for a Client's command and handles it.
   */
  @Override
  public void waitMsg() {
    //TODO CHANGE THIS WHILE LOOP
    while(true){
      String s = in.nextLine();
      switch (s.split(";")[0]) {
        case "PLACECARD" -> this.getGame().placeCard(this, s);
        case "DRAWCARD" -> this.getGame().drawCard(this, s);
        case "CHAT" -> this.getGame().chat(s);
      }
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

