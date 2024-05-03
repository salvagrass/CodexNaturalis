package it.polimi.ingsw.controller.userclient;

import it.polimi.ingsw.controller.connection.RemoteComm;

import java.rmi.*;

/**
 * The `RemoteClient` interface defines methods for handling messages and connecting to a remote server.
 */
public interface RemoteClient extends Remote {
    void handleMsg(String s) throws RemoteException;
    void setComm(RemoteComm c) throws RemoteException;
}
