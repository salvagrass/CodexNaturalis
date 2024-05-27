package it.polimi.ingsw.controller.connection;

import it.polimi.ingsw.controller.userclient.ClientRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Extends Comm to implement an RMI (Remote Method Invocation) connection.
 */
public class RMIComm extends Comm implements RemoteComm {

    private ClientRemote client;
    private String chosen;

    /**
     * Constructs a new RMIComm instance.
     *
     * @param client the remote client to communicate with using RMI.
     * @throws RemoteException if a communication-related exception occurs.
     */
    public RMIComm(ClientRemote client) throws RemoteException{
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
        try{
            this.client.handleMsgRemote(msg);
        } catch (RemoteException e) {
            System.out.println("Client crashed");
        }
    }

    @Override
    public void setMsg(String msg) throws RemoteException{
        synchronized (this) {
            this.chosen = msg;
            this.notifyAll();
        }
    }

    /**
     * Retrieves the answer previously obtained with the setMsg method.
     *
     * @return The answer obtained from the last message sent.
     */
    @Override
    public String getMsg() {
        String s;
        synchronized (this) {
            while (this.chosen == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
            s = this.chosen;
            this.chosen = null;
        }
        return s;
    }

    /**
     * Empty method for RMIComm, intended to be used by other classes that inherit
     * from Comm.
     */
    @Override
    public void waitMsg() {
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

    public void chat(String msg) throws RemoteException{
        this.getGame().chat(msg);
    }

    @Override
    public void close(){
        this.client = null;
    }
}
