package it.polimi.ingsw.controller.connection;

import it.polimi.ingsw.controller.userclient.RemoteClient;

import java.rmi.RemoteException;

/**
 * Extends Comm to implement an RMI (Remote Method Invocation) connection.
 */
public class RMIComm extends Comm implements RemoteComm {

    private final RemoteClient client;
    private String chosen;

    /**
     * Constructs a new RMIComm instance.
     *
     * @param client the remote client to communicate with using RMI.
     * @throws RemoteException if a communication-related exception occurs.
     */
    public RMIComm(RemoteClient client) throws RemoteException{
        this.client = client;
    }

    /**
     * Sends a message to the client.
     *
     * @param msg The message to be sent.
     */
    @Override
    public void sendMsg(String msg){
        try{
            this.client.handleMsg(msg);
        } catch (RemoteException e) {
            //TODO handle
        }
    }

    @Override
    public void setMsg(String msg) throws RemoteException{
        this.chosen = msg;
    }

    /**
     * Retrieves the answer previously obtained with the setMsg method.
     *
     * @return The answer obtained from the last message sent.
     */
    @Override
    public String getMsg() {
        return this.chosen;
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
     * @return True if the placement is successful, False otherwise.
     */
    @Override
    public boolean placeCard(String msg) throws RemoteException {
        return getGame().placeCard(this, msg);
    }

    /**
     * Method called from the Client to draw a card.
     * 
     * @param msg A string containing the draw index.
     * @return True if the draw succeeds, False otherwise.
     */
    @Override
    public boolean drawCard(String msg) throws RemoteException{
        return getGame().drawCard(this, msg);
    }

    @Override
    public void close(){
        //TODO implement
    }
}
