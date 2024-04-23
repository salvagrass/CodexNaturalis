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
            comm.sendMsg("DISPLAY;1)Create_Game&2)Join_game&3)Load_game&|CHOOSE;[1,3]");
            String cmd = comm.getMsg();
            comm.sendMsg("DISPLAY;Insert_room_ID:_|CHOOSE;^");
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
            comm.sendMsg("DISPLAY;Id_already_exists");
            return true;
        }
        comm.sendMsg("DISPLAY;Insert_lobby_capacity:_|CHOOSE;[2,4]");
        int numPlayers = Integer.parseInt(comm.getMsg());
        GameLogic game = new GameLogic(this,numPlayers);
        games.put(id, game);
        comm.sendMsg("DISPLAY;Game_with_id_created");
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
                comm.sendMsg("DISPLAY;Id_does_not_exist");
                return true;
            }
            game = games.get(id);
            synchronized(game){
                if(game.isFull()){
                    comm.sendMsg("DISPLAY;Full_Lobby");
                    return true;
                }
                comm.sendMsg("DISPLAY;Joining_game");
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


    /*
    private void handleClient(Socket socket) throws IOException {
        Scanner in = getScanner(socket);
        PrintWriter out = getPrintWriter(socket);
        if (in == null || out == null)
            return;
        String id = null;
        String name = null;
        Comm comm = null;
        boolean valid = true;
        while(valid) {
            id = in.nextLine();
            name = in.nextLine();
            if (!games.containsKey(id)) {
                out.println("numero");
                out.flush();
                int size = Integer.parseInt(in.nextLine());
                games.put(id,new GameLogic(size));
            } else {
                out.println("no numero");
                out.flush();
            }
            GameLogic game = games.get(id);
            synchronized(game){
                if(!game.isFull()){
                    valid = false;
                    comm = new SocketComm(socket);
                    game.addRecord(comm);
                }
            }
        }
        games.get(id).addPlayer(comm,name);
    }
     */
}