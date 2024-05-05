package it.polimi.ingsw.controller.userclient;

import it.polimi.ingsw.controller.connection.SocketComm;
import it.polimi.ingsw.view.UserView;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The ClientSocket class represents a client using socket-based communication.
 */
public class ClientSocket extends Client{

    private enum ResponseState {
        WAIT, DENIED, ACCEPTED
    }

    private SocketComm socketComm;
    private ResponseState response = ResponseState.WAIT;

    /**
     * The `handleMsg` method processes incoming messages from the server.
     * Otherwise, it delegates the handling to the superclass using `super.handleMsg(command)`.
     *
     * @param msg The input message containing one or more commands separated by '|'.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public void handleMsg(String msg){
        String[] commands = msg.split("\\|");
        for (String command : commands) {
            String[] s = command.split(";");
            switch (s[0]) {
                case "CONFIRM" -> {
                    synchronized(this){
                        this.response = ResponseState.ACCEPTED;
                        this.notifyAll();
                    }
                }
                case "DENIED" -> {
                    synchronized(this){
                        this.response = ResponseState.DENIED;
                        this.notifyAll();
                    }
                }
                default -> super.handleMsg(command);
            }
        }
    }


    /**
     * Constructs a new ClientSocket instance.
     *
     * @throws RemoteException if a communication-related exception occurs.
     */
    public ClientSocket(UserView userView) throws RemoteException{
        super(userView);
    }

    /**
     * Listens for incoming messages and handles them.
     *
     */
    private void listener(){
        while(true){
            handleMsg(this.socketComm.getMsg());
        }
    }

    /**
     * Connects the client to the specified server address and port.
     *
     * @param addr the server address to connect to.
     * @param port the port number to use for the connection.
     */
    public void connect(String addr, int port){
        this.socketComm = null; //check if we can use Comm, otherwise use Sockets
        try {
           this.socketComm = new SocketComm(new Socket(addr,port));
        } catch(IOException e) {
            System.out.println("Can't connect to the server");
        }
        listener();
    }

    @Override
    boolean choose(int min, int max) {
        try{
            int choice = Integer.parseInt(userView.getInput());
            if(choice >= min && choice <= max){
                socketComm.sendMsg(choice + "");
                return true;
            }
        }catch(NumberFormatException e){
            return false;
        }
        return false;
    }

    @Override
    boolean choose(List<String> choices) {
        String s = userView.getInput();
        if(choices.contains(s)){
            socketComm.sendMsg(s);
            return true;
        }
        return false;
    }

    @Override
    boolean choose(String pattern) {
        String s = userView.getInput();
        if(s.matches(pattern)){
            socketComm.sendMsg(s);
            return true;
        }
        return false;
    }

    /**
     * The `placeCard()` method handles placing a card in the game.
     *Otherwise, it displays "Invalid Placement!" to the user.
     */
    void placeCard() {
        String msg = super.placeCardSetUp();
        if (msg == null) {
            return;
        }
        socketComm.sendMsg(msg);
        if(waitResponse()){
            super.acceptedPlacement();
        }else{
            userView.show("Invalid Placement!\n");
        }
    }

    /**
     * The `drawCard()` method handles drawing a card in the game.
     * Otherwise, it displays "Invalid Draw!" to the user.
     */
    void drawCard(){
        String msg = super.drawCardSetUp();
        if(msg==null){
            return;
        }
        socketComm.sendMsg(msg);
        if(waitResponse()){
            super.acceptedDraw();
        }else{
            userView.show("Invalid Draw!\n");
        }
    }

    /**
     * The `waitResponse()` method waits for a response from the server.
     * @return `true` if the response was accepted, `false` otherwise.
     */
    private boolean waitResponse(){
        boolean flag;
        synchronized(this){
            try{
                while(this.response==ResponseState.WAIT)
                    this.wait();
            } catch (InterruptedException ie) {
                //TODO handle
            }
            flag = this.response == ResponseState.ACCEPTED;
            this.response = ResponseState.WAIT;
        }
        return flag;
    }
}
