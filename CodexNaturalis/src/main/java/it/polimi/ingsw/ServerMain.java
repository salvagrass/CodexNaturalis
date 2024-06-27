package it.polimi.ingsw;

import it.polimi.ingsw.controller.serverfunction.CentralServer;
import it.polimi.ingsw.util.ProjectLogger;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Server main.
 */
public class ServerMain {
    public static void main( String[] args ) throws Exception
    {
        ProjectLogger pj = new ProjectLogger("Main");
        pj.clearFile("logs/serverLog.txt");
        pj.clearFile("logs/commLog.txt");
        System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
        System.out.println("Server bound and ready");
        CentralServer server = new CentralServer(5432);
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("RMIServer", server);
        server.startServer();
    }
}
