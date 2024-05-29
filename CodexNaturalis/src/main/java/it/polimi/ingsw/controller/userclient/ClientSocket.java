package it.polimi.ingsw.controller.userclient;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import it.polimi.ingsw.controller.connection.SocketComm;
import it.polimi.ingsw.controller.serverfunction.MsgCreator;
import it.polimi.ingsw.view.UserView;

public class ClientSocket extends Client{
  private SocketComm comm;
  private final AtomicBoolean stopConnection;

  public ClientSocket(UserView userView){
    super(userView);
    this.stopConnection = new AtomicBoolean(false);
    this.port = 5432;
  }

  @Override
  public void connect(String addr) {
    this.comm = null;
    try {
      this.comm = new SocketComm(new Socket(addr, this.port));
    } catch (IOException e) {
      //TODO log file
      throw new RuntimeException();
    }
    new Thread(this::listener).start();
  }

  private void listener(){
    while (!stopConnection.get()){
      try{
        super.handleMsg(this.comm.getMsg());
      }catch(IllegalStateException | NoSuchElementException e){
        //TODO LOG FILE
      }
    }
  }

  @Override
  void sendMsg(String msg) {
    this.comm.sendMsg(msg);
  }

  @Override
  public void placeCard(int idx, int x, int y,boolean face) {
    sendMsg(MsgCreator.placeCardMsg(idx,x,y,face));
  }

  @Override
  public void drawCard(int idx) {
    sendMsg(MsgCreator.drawCardMsg(idx));
  }

  @Override
  public void sendChatMessage(String msg) {
    this.comm.sendMsg(msg);
  }

  @Override
  public void closeConnection(){
    this.stopConnection.set(true);
    this.comm.close();
    this.comm = null;
  }
}
