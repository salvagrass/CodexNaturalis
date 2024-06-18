package it.polimi.ingsw.controller.userclient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemote extends Remote {
  void handleMsgRemote(String message) throws RemoteException;
}
