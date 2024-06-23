package it.polimi.ingsw;

import it.polimi.ingsw.controller.serverfunction.CentralServer;

import java.net.InetAddress;
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
        System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
        System.out.println("Server bound and ready");
        CentralServer server = new CentralServer(5432);
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("RMIServer", server);
        server.startServer();
        System.out.println("Server bound and ready");
    }
}
