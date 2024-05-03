package it.polimi.ingsw;

import it.polimi.ingsw.controller.serverfunction.CentralServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        CentralServer server = new CentralServer(5432);
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("RMIServer", server);
        server.startServer();
        System.out.println("Server bound and ready");
    }
}
