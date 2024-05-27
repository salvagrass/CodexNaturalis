package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.RemoteComm;
import it.polimi.ingsw.controller.userclient.ClientRemote;

import java.rmi.*;

/**
 * Interface containing the methods visible from a remote client.
 */
public interface RemoteServer extends Remote{

    /**
     * Establishes a connection with a remote client.
     *
     * @param client The client initiating the connection.
     * @throws RemoteException If there is a connection problem.
     */
    RemoteComm join(ClientRemote client) throws RemoteException;
}
