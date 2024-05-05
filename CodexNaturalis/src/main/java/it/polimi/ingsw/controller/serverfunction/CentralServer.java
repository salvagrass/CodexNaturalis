package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.controller.connection.RMIComm;
import it.polimi.ingsw.controller.connection.RemoteComm;
import it.polimi.ingsw.controller.connection.SocketComm;
import it.polimi.ingsw.controller.userclient.RemoteClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class that waits for players to connect, creates a Comm object for each one, and sends them to a game lobby.
 */
public class CentralServer extends UnicastRemoteObject implements RemoteServer {
    private final int port;
    private final ExecutorService executor;
    private final Map<String,GameLogic> games;
    final Object lock;

    /**
     * Constructor for the server.
     *
     * @param port The port used by the ServerSocket.
     * @throws RemoteException In case of RMI connection problems.
     */
    public CentralServer(int port) throws RemoteException {
        this.port = port;
        this.executor = Executors.newFixedThreadPool(30);
        this.games = new ConcurrentHashMap<>();
        this.lock = new Object();
    }

    /**
     * Starts listening for clients on a socket.
     */
    public void startServer() {
        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(port);
        } catch(IOException e){
            String s = "Can't open the server socket: " + e.getMessage();
            System.out.println(s);
            return;
        }
        while(true){
            try {
                SocketComm socketComm = new SocketComm(serverSocket.accept());
                executor.submit(()->handleClient(socketComm));
            } catch (IOException e) {
                String s = "Can't open the client socket: " + e.getMessage();
                System.out.println(s);
            }
        }
    }

    public RemoteComm join(RemoteClient client) throws RemoteException {
        RMIComm comm = new RMIComm(client);
        client.setComm(comm);
        handleClient(comm);
        return comm;
    }

    /**
     * Closes a game lobby.
     *
     * @param game The GameLogic associated with the lobby to be removed.
     */
    public void close(GameLogic game) {
        String id = games.entrySet().stream().
                    filter(x -> (x.getValue() == game)).
                    findFirst().
                    get().
                    getKey();
        this.games.remove(id);
    }

    /**
     * Asks the client which action they want to perform and handles the corresponding operation,
     * including joining the game if the client chooses to create one.
     *
     * @param comm The connection to the specific client.
     */
    private void handleClient(Comm comm){
        boolean isInvalid = false;
        do {
            comm.sendMsg(MsgCreator.chooseMessage("1)Create Game\n2)Join Game\n3)Load Game\n",1,3, "Invalid choice, try again!"));
            String cmd = comm.getMsg();
            comm.sendMsg(MsgCreator.chooseMessage("Insert Room ID (6 alphanumeric characters): ", "^[a-zA-Z0-9]{6}$", "Invalid ID: 6 alphanumeric characters"));
            String id = comm.getMsg();
            switch (cmd) {
                case "1":
                    isInvalid = createGame(id, comm);
                    if(isInvalid)
                        break;
                case "2":
                    isInvalid = joinGame(id, comm);
                    break;
                case "3":
                    isInvalid = loadGame(id, comm);
                    break;
            }
        }while(isInvalid);
    }

    /**
     * Creates a GameLogic instance to handle a game.
     *
     * @param id The ID of the lobby.
     * @param comm The connection to the specific client.
     * @return True if the creation succeeds, false otherwise.
     */
    private boolean createGame(String id, Comm comm){
        if(games.containsKey(id)) {
            comm.sendMsg(MsgCreator.displayMessage("ID already exists\n"));
            return true;
        }
        comm.sendMsg(MsgCreator.chooseMessage("Insert lobby capacity: ", 2,4, "Invalid choice, try again!"));
        int numPlayers = Integer.parseInt(comm.getMsg());
        GameLogic game = new GameLogic(this,numPlayers);
        games.put(id, game);
        comm.sendMsg(MsgCreator.displayMessage("Game with ID created\n"));
        return false;
    }

    /**
     * Connects a client with a game.
     *
     * @param id The ID of the lobby.
     * @param comm The connection to the specific client.
     * @return True if the connection succeeds, false otherwise.
     */
    private boolean joinGame(String id, Comm comm) {
        GameLogic game;
        synchronized(lock){
            if (!games.containsKey(id)) {
                comm.sendMsg(MsgCreator.displayMessage("Game with ID doesn't exist\n"));
                return true;
            }
            game = games.get(id);
            synchronized(game){
                if(game.isFull()){
                    comm.sendMsg(MsgCreator.displayMessage("Full lobby\n"));
                    return true;
                }
                comm.sendMsg(MsgCreator.displayMessage("Joining Game\n"));
                comm.setGame(game);
                game.addRecord(comm);
            }
        }
        executor.submit(()->game.addPlayer(comm));
        return false;
    }

    /**
     * Load a previously saved game.
     *
     * @param id The ID of the lobby.
     * @param comm The connection to the specific client.
     * @return True if the load succeeds, false otherwise.
     */
    private boolean loadGame(String id, Comm comm) {
        //TODO IMPLEMENT THE METHOD
        return true;
    }
}