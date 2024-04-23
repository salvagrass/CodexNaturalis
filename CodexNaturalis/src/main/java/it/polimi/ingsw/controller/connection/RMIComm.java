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
     * Sends a message to the client and saves the answer in an attribute.
     *
     * @param msg The message to be sent.
     */
    @Override
    public void sendMsg(String msg){
        try{
            this.chosen = this.client.handleMsg(msg);
        } catch (RemoteException e) {
            //TODO handle
        }
    }

    /**
     * Retrieves the answer previously obtained with the sendMsg method.
     *
     * @return The answer obtained from the last message sent.
     */
    @Override
    public String getMsg() {
        return this.chosen;
    }

    @Override
    public String placeCard(String msg) throws RemoteException {
        return getGame().placeCard(this, msg) ? "true" : "false";
    }

    @Override
    public String drawCard(String msg) throws RemoteException{
        return getGame().drawCard(this, msg) ? "true" : "false";
    }

    @Override
    public void close(){
        //TODO implement
    }
}
