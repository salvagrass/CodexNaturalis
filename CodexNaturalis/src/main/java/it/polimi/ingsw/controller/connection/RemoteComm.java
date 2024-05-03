package it.polimi.ingsw.controller.connection;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface containing the methods visible from a remote client.
 */
public interface RemoteComm extends Remote {
    /**
     * Starts the placeCard method in the GameLogic.
     *
     * @param msg A string representing which card to place and how to place it.
     * @return True if the card placement was successful, False otherwise.
     * @throws RemoteException In case of a connection problem.
     */
    boolean placeCard(String msg) throws RemoteException;

    /**
     * Starts the drawCard method in the GameLogic.
     *
     * @param msg A string containing the index of the card we want to draw.
     * @return True if the card was successfully drawn, False otherwise.
     * @throws RemoteException In case of a connection problem.
     */
    boolean drawCard(String msg) throws RemoteException;

    /**
     * Sets a message to be read from the server.
     * 
     * @param msg The message we want to save.
     * @throws RemoteException If there's a connection problem.
     */
    void setMsg(String msg) throws RemoteException;
}
