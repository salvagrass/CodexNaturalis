package it.polimi.ingsw.controller.connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Extends Comm to implement a socket connection.
 */
public class SocketComm extends Comm {

  Socket socket;
  Scanner in;
  PrintWriter out;

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
    out.println(msg);
    out.flush();
  }

  /**
   * Retrieves a message from the socket.
   *
   * @return The message read from the socket.
   */
  @Override
  public String getMsg() {
    return in.nextLine();
  }

  /**
   * Waits for a Client's command and handles it.
   */
  @Override
  public void waitMsg() {
    boolean isValid = false;
    while(!isValid){
      String s = in.nextLine();
      isValid = switch(s.split(";")[0]){
        case "PLACECARD" -> this.getGame().placeCard(this, s);
        case "DRAW" -> this.getGame().drawCard(this, s);
        default -> false;
      };
    }
  }

  @Override
  public void close(){
    //TODO implement
  }
}

