package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.RemoteComm;
import it.polimi.ingsw.controller.userclient.RemoteClient;

import java.rmi.*;

/**
 * Interface containing the methods visible from a remote client.
 */
public interface RemoteServer extends Remote{

    /**
     * Establishes a connection with a remote client.
     *
     * @param client The client initiating the connection.
     * @return The communication interface connected to the client.
     * @throws RemoteException If there is a connection problem.
     */
    RemoteComm join(RemoteClient client) throws RemoteException;
}
