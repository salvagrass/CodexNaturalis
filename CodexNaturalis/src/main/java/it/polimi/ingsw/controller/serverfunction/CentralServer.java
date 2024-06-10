package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.controller.connection.RMIComm;
import it.polimi.ingsw.controller.connection.RemoteComm;
import it.polimi.ingsw.controller.connection.SocketComm;
import it.polimi.ingsw.controller.userclient.ClientRemote;
import it.polimi.ingsw.util.Phase;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
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
    private final Map<String, LoadLogic> loadingGames;
    private final Set<String> memorized;
    private final Map<String,Object> loadLocks;
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
        this.loadingGames = new ConcurrentHashMap<>();
        this.memorized = new HashSet<>();
        try(BufferedReader fw = new BufferedReader(new FileReader("memorized_games.txt"))) {
            String line;
            while((line = fw.readLine())!=null){
                memorized.add(line);
            }
        } catch (IOException e) {
            //LOG CAN'T OPEN THE LOADED GAMES
        }
        this.loadLocks = new ConcurrentHashMap<>();
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
                Socket socket = serverSocket.accept();
                SocketComm socketComm = new SocketComm(socket);
                executor.submit(()->handleClient(socketComm));
            } catch (IOException e) {
                String s = "Can't open the client socket: " + e.getMessage();
                System.out.println(s);
            }
        }
    }

    public RemoteComm join(ClientRemote client) throws RemoteException {
        RMIComm comm = new RMIComm(client);
        executor.submit(()->handleClient(comm));
        return comm;
    }

    /**
     * Asks the client which action they want to perform and handles the corresponding operation,
     * including joining the game if the client chooses to create one.
     *
     * @param comm The connection to the specific client.
     */
    private void handleClient(Comm comm){
        boolean valid;
        do {
            comm.sendMsg(MsgCreator.changePhaseMsg(Phase.MENU));
            comm.sendMsg(MsgCreator.chooseMsg());
            String cmd = comm.getMsg();
            valid = switch(cmd) {
                case "1" -> createGame(comm);
                case "2" -> joinGame(comm);
                case "3" -> loadGame(comm);
                case "exit" -> {
                    comm.sendMsg(MsgCreator.changePhaseMsg(Phase.EXIT));
                    comm.close();
                    yield true;
                }
                default -> false;
            };
        } while(!valid);
    }

    /**
     * Creates a GameLogic instance to handle a game.
     * @param comm The connection to the specific client.
     */
    private boolean createGame(Comm comm) {
        String input, id;
        int numPlayers;
        boolean isContained;
        comm.sendMsg(MsgCreator.changePhaseMsg(Phase.CREATE));
        do{
            comm.sendMsg(MsgCreator.chooseMsg());
            input = comm.getMsg();
            id = input.split(" ")[0];
            if (id.equals("exit")) {
                comm.sendMsg(MsgCreator.confirmMsg(true, "Returned to menu"));
                return false;
            }
            synchronized(this.games) {
                numPlayers = Integer.parseInt(input.split(" ")[1]);
                if (this.games.containsKey(id)||this.memorized.contains(id)) {
                    comm.sendMsg(MsgCreator.confirmMsg(false, "ID already exists"));
                    isContained = true;
                }
                else {
                    comm.sendMsg(MsgCreator.confirmMsg(true, "ID available"));
                    isContained = false;
                    GameLogic game = new GameLogic(this, id, numPlayers);
                    comm.setGame(game);
                    game.addRecord(comm);
                    games.put(id, game);
                    synchronized(this.games.get(id)){
                        game.submitPlayer(comm);
                    }
                }
            }
        } while (isContained);
        return true;
    }

    /**
     * Connects a client with a game.
     *
     * @param comm The connection to the specific client.
     */
    private boolean joinGame(Comm comm) {
        GameLogic game = null;
        String id;
        boolean isAvailable;

        comm.sendMsg(MsgCreator.changePhaseMsg(Phase.JOIN));
        do{
            comm.sendMsg(MsgCreator.chooseMsg());
            id = comm.getMsg();
            if (id.equals("exit")) {
                comm.sendMsg(MsgCreator.confirmMsg(true, "Returned to menu"));
                return false;
            }
            synchronized(lock){
                if (!games.containsKey(id)) {
                    comm.sendMsg(MsgCreator.confirmMsg(false, "Game with ID doesn't exist"));
                    isAvailable = false;
                } else {
                    game = games.get(id);
                    synchronized (games.get(id)) {
                        if (game.isFull()) {
                            comm.sendMsg(MsgCreator.confirmMsg(false, "Full lobby"));
                            isAvailable = false;
                        } else{
                            comm.sendMsg(MsgCreator.confirmMsg(true, "Joining Game"));
                            isAvailable = true;
                            comm.setGame(game);
                            game.addRecord(comm);
                        }
                    }
                }
            }
        } while(!isAvailable);
        GameLogic tmp = game;
        synchronized(this.games.get(id)) {
            tmp.submitPlayer(comm);
        }
        return true;
    }

    /**
     * Load a previously saved game.
     *
     * @param comm The connection to the specific client.
     */
    private boolean loadGame(Comm comm) {
        String id=null;
        boolean available = false;
        comm.sendMsg(MsgCreator.changePhaseMsg(Phase.JOIN));
        while(!available) {
            comm.sendMsg(MsgCreator.chooseMsg());
            id = comm.getMsg();
            if(id.equals("exit")){
                comm.sendMsg(MsgCreator.confirmMsg(true,"returned to menu"));
                return false;
            }
            synchronized (this.games) {
                if (!this.memorized.contains(id)) {
                    comm.sendMsg(MsgCreator.confirmMsg(false, "id not memorized"));
                }else{
                    available = true;
                }
            }
        }
        available = false;
        while(!available) {
            this.loadLocks.putIfAbsent(id, new Object());
            synchronized (this.loadLocks.get(id)) {
                if (this.loadingGames.containsKey(id)) {
                    LoadLogic logic = loadingGames.get(id);
                    if (logic.isFull()) {
                        comm.sendMsg(MsgCreator.confirmMsg(false, "full lobby"));
                    } else {
                        logic.addRecord(comm);
                        available = true;
                    }
                } else {
                    try {
                        LoadLogic logic = new LoadLogic(id);
                        this.loadingGames.put(id, logic);
                        logic.addRecord(comm);
                        available = true;
                    } catch (IOException e) {
                        //TODO HANDLE
                    }
                }

            }
        }
        String tmp = id;
        comm.sendMsg(MsgCreator.confirmMsg(true,"Linking name"));
        executor.submit(()->this.loadingGames.get(tmp).linkName(this,comm));
        return true;
    }


    void saveGame(String id,String jsonGame){
        try {
            FileWriter fw = new FileWriter(id+"_saved.json");
            fw.write(jsonGame);
            fw.flush();
            fw.close();
            try {
                File myObj = new File("memorized_games.txt");
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    if(myReader.nextLine().contains(id)){
                        return;
                    }
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
            }
            fw = new FileWriter("memorized_games.txt",true);
            fw.write(id+'\n');
            fw.flush();
            fw.close();
        } catch (IOException e) {
            //TODO LOG FILE
            System.out.println("Can't save game");
        }
    }

    void submitClient(Comm comm) {
        this.executor.submit(() -> handleClient(comm));
    }

    void closeGame(String id) {
        synchronized(this.games.get(id)) {
            this.games.remove(id);
        }
    }
}