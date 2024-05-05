package it.polimi.ingsw.controller.userclient;

import it.polimi.ingsw.controller.connection.RemoteComm;
import it.polimi.ingsw.controller.serverfunction.RemoteServer;
import it.polimi.ingsw.view.UserView;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;


/** 
 * ClientRMI class represents a client RMI entity.
 */
public class ClientRMI extends Client implements RemoteClient {
    private RemoteComm comm;

    /**
     * Constructs a new ClientRMI instance.
     *
     * @throws RemoteException if a communication-related exception occurs.
     */
    public ClientRMI(UserView userView) throws RemoteException {
        super(userView);
    }

    /**
     * Connects the client to the specified server address and port using RMI.
     *
     * @param addr the server address to connect to.
     * @param port the port number to use for the connection.
     */
    public void connect(String addr, int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(addr, port);
            RemoteServer server = (RemoteServer) registry.lookup("RMIServer");
            this.comm = server.join(this);
        } catch (Exception e) {
            //TODO handle
        }
    }

    /**
     * Sets the communication channel for RMI.
     *
     * @param comm The RemoteComm object representing the communication channel.
     * @throws RemoteException If there is an issue with the remote communication.
     */
    @Override
    public void setComm(RemoteComm comm) throws RemoteException{
        if(this.comm==null) {
            this.comm = comm;
        }
    }

    /**
     * It splits the input message using the pipe character ('|') as a delimiter
     * and then processes each command individually.
     *
     * @param msg The input message containing one or more commands separated by '|'.
     */
    @Override
    public void handleMsg(String msg){
        String[] commands = msg.split("\\|");
        for (String command : commands) {
            super.handleMsg(command);
        }
    }

    /**
     * It sets the message received from the user view using the `comm.setMsg()` method.
     */
    @Override
    boolean choose() {
        try{
            comm.setMsg(userView.getInput());
        }catch(RemoteException e){
            System.out.println("choose error"); //TODO handle
            return false;
        }
        return true;
    }

    @Override
    boolean choose(double min, double max) {
        try{
            double choice = Double.parseDouble(userView.getInput());
            if(choice >= min && choice <= max){
                comm.setMsg(choice + "");
                return true;
            }
        }catch(RemoteException e){
            System.out.println("choose error"); //TODO handle
        }catch(NumberFormatException e){
            return false;
        }
        return false;
    }

    @Override
    boolean choose(List<String> choices) {
        try{
            String s = userView.getInput();
            if(choices.contains(s)){
                comm.setMsg(s);
                return true;
            }
        }catch(RemoteException e){
            System.out.println("choose error"); //TODO handle
        }
        return false;
    }

    @Override
    boolean choose(String pattern) {
        try{
            String s = userView.getInput();
            if(s.matches(pattern)){
                comm.setMsg(s);
                return true;
            }
        }catch(RemoteException e){
            System.out.println("choose error"); //TODO handle
        }
        return false;
    }

    /**
     * The `placeCard` method processes card placement based on the input message.
     * Otherwise, it displays an error message to the user view: "Invalid Placement!".
     */
    public void placeCard() {
        String msg = super.placeCardSetUp();
        if (msg == null) {
            return;
        }
        try {
            if(this.comm.placeCard(msg)){
                super.acceptedPlacement();
            }else{
                userView.show("Invalid Placement!");
            }
        } catch (RemoteException e) {
            //TODO handle
        }
    }
    
    /**
     * The `drawCard` method processes card drawing based on the input message.
     * Otherwise, it displays an error message to the user view: "Invalid Draw!".
     */
    public void drawCard() {
        String msg = super.drawCardSetUp();
        if (msg == null) {
            return;
        }
        try {
            if (this.comm.drawCard(msg)) {
                super.acceptedDraw();
            } else {
                userView.show("Invalid Draw!");
            }
        } catch (RemoteException e) {
            // TODO handle
        }
    }
}
