package it.polimi.ingsw.controller.connection;

import it.polimi.ingsw.controller.userclient.ClientRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Extends Comm to implement an RMI (Remote Method Invocation) connection.
 */
public class RMIComm extends Comm implements RemoteComm {

    private ClientRemote client;

    /**
     * Constructs a new RMIComm instance.
     *
     * @param client the remote client to communicate with using RMI.
     * @throws RemoteException if a communication-related exception occurs.
     */
    public RMIComm(ClientRemote client) throws RemoteException{
        super();
        this.client = client;
        UnicastRemoteObject.exportObject(this,0);
    }

    /**
     * Sends a message to the client.
     *
     * @param msg The message to be sent.
     */
    @Override
    public void sendMsg(String msg){
        if(client!=null) {
            try {
                this.client.handleMsgRemote(msg);
            } catch (RemoteException e) {
                System.out.println("Client crashed");
            }
        }
    }

    /**
     * Sends a ping message to the server.
     * If a communication error occurs, the method closes the connection,
     * handles the crash, and sets the clientCrashed flag.
     */
    @Override
    public void pingMethod() {
        try {
            this.client.handleMsgRemote("PING");
        } catch (RemoteException e) {
            close();
            this.handler.handleCrash(this);
            this.clientCrashed.set(true);
        }
    }

    /**
     * Sets the message.
     *
     * @param msg The message to set.
     * @throws RemoteException If there's a remote exception.
     */
    public void setMsg(String msg) throws RemoteException{
        setChosen(msg);
    }

    /**
     * Method called from the Client to place a card.
     * 
     * @param msg A string containing the placement information.
     */
    @Override
    public void placeCard(String msg) throws RemoteException {
        getGame().placeCard(this, msg);
    }

    /**
     * Method called from the Client to draw a card.
     * 
     * @param msg A string containing the draw index.
     */
    @Override
    public void drawCard(String msg) throws RemoteException{
         getGame().drawCard(this, msg);
    }

    /**
     * Sends a chat message.
     *
     * @param msg The message to send.
     * @throws RemoteException If there's a remote exception.
     */
    public void chat(String msg) throws RemoteException{
        this.getGame().chat(msg);
    }

    /**
     * Closes the connection.
     */
    @Override
    public void close(){
        this.client = null;
    }
}
