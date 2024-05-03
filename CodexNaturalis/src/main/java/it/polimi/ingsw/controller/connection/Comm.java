package it.polimi.ingsw.controller.connection;

import it.polimi.ingsw.controller.serverfunction.GameLogic;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * A class that handles communication with the server based on the connected client type.
 * It is intended to be inherited by classes that implement specific types of connections.
 */
public abstract class Comm extends UnicastRemoteObject {
    private GameLogic game;

    /**
     * Constructs a new Comm instance.
     *
     * @throws RemoteException if a communication-related exception occurs.
     */
    public Comm() throws RemoteException {

    }

    /**
     * Sends a message to the client.
     *
     * @param msg The message to be sent.
     */
    public abstract void sendMsg(String msg);

    /**
     * Retrieves the answer previously obtained with the setMsg method.
     *
     * @return The answer obtained from the Client.
     */
    public abstract String getMsg();

    public void setGame(GameLogic game){
        this.game = game;
    }

    public GameLogic getGame() {
        return this.game;
    }

    /**
     * Sends a message to every Communication (Comm) object present in the turnOrder list.
     *
     * @param msg The message to be sent to everyone.
     * @param turnOrder The list of Communication (Comm) objects to which the message will be sent.
     */
    public static void sendBroadcast(String msg, List<Comm> turnOrder) {
        for (Comm comm : turnOrder) {
            comm.sendMsg(msg);
        }
    }

    /*
     * Waits for a message.
     */
    public abstract void waitMsg();

    /**
     * Closes the connection.
     */
    public abstract void close();
}
