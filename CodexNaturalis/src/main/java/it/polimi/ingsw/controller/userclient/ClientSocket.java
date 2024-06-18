package it.polimi.ingsw.controller.userclient;

import it.polimi.ingsw.controller.serverfunction.MsgCreator;
import it.polimi.ingsw.util.Phase;
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

public class ClientSocket extends Client{
  private Socket socket;
  private Scanner in;
  private PrintWriter out;
  private final AtomicBoolean stopConnection;
  private final AtomicBoolean pingFlag;

  ExecutorService executor;
  ScheduledExecutorService scheduled;

  public ClientSocket(UserView userView){
    super(userView);
    this.stopConnection = new AtomicBoolean(false);
    this.pingFlag = new AtomicBoolean(true);
    this.port = 5432;
    this.executor = Executors.newSingleThreadExecutor();
    this.scheduled = Executors.newSingleThreadScheduledExecutor();
  }

  @Override
  public void connect(String addr) {
    this.socket = null;
    try {
      socket = new Socket(addr, this.port);
      this.in = new Scanner(socket.getInputStream());
      this.out = new PrintWriter(socket.getOutputStream());
    } catch (IOException e) {
      //TODO log file
      throw new RuntimeException();
    }
    scheduled.scheduleAtFixedRate(this::waitForPing,0,15, TimeUnit.SECONDS);
    new Thread(this::listener).start();
  }

  private void waitForPing(){
      synchronized(this.pingFlag){
        if(!this.pingFlag.get()){
          handleMsg(MsgCreator.changePhaseMsg(Phase.SERVERCRASHED));
        }
        this.pingFlag.set(false);
    }
  }

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

  private void listener(){
    while (!stopConnection.get()){
      try{
        handleMsg(this.in.nextLine());
      }catch(IllegalStateException | NoSuchElementException e){
        //TODO LOG FILE
      }
    }
  }

  @Override
  void sendMsg(String msg) {
    synchronized(this) {
      this.out.println(msg);
      this.out.flush();
    }
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
    this.out.println(msg);
  }

  @Override
  public void closeConnection(){
    this.stopConnection.set(true);
    try{
      scheduled.shutdownNow();
      executor.shutdownNow();
      scheduled = Executors.newSingleThreadScheduledExecutor();
      executor = Executors.newSingleThreadExecutor();
      in.close();
      out.close();
      socket.close();
    }catch(IOException e){
      //TODO LOG FILE CANT CLOSE THE SOCKET
    }
  }
}
