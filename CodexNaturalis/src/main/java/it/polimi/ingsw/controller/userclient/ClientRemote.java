package it.polimi.ingsw.controller.userclient;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface of the Client class
 * to be implemented by the {@link ClientRMI} class
 */
public interface ClientRemote extends Remote {
  /**
   * Method to be called by the server with RMI for message handling
   * @param message the message from the server to be handled
   * @throws RemoteException if a network error occurs
   */
  void handleMsgRemote(String message) throws RemoteException;
}
