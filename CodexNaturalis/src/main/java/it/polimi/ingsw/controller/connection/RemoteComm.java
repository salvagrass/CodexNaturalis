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
     * @throws RemoteException In case of a connection problem.
     */
    void placeCard(String msg) throws RemoteException;

    /**
     * Starts the drawCard method in the GameLogic.
     *
     * @param msg A string containing the index of the card we want to draw.
     * @throws RemoteException In case of a connection problem.
     */
    void drawCard(String msg) throws RemoteException;

    /**
     * Sets a message to be read from the server.
     * 
     * @param msg The message to send to the server.
     * @throws RemoteException If there's a connection problem.
     */
    void setMsg(String msg) throws RemoteException;

    /**
     * Sends a chat message to the server.
     *
     * @param msg The chat message to send.
     * @throws RemoteException If a communication error occurs.
     */
    void chat(String msg) throws RemoteException;
}
