package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.controller.connection.RMIComm;
import it.polimi.ingsw.controller.connection.RemoteComm;
import it.polimi.ingsw.controller.connection.SocketComm;
import it.polimi.ingsw.controller.userclient.ClientRemote;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.util.ProjectLogger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * Class that represents the main server of the app, dispatches the various client
 * based on their request
 */
public class CentralServer extends UnicastRemoteObject implements RemoteServer,CrashHandler{
    private final int port;
    private final Map<ExecutorService,Comm> executors;
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
        this.executors = new HashMap<>();
        for(int i=0;i<10;i++){
            executors.put(Executors.newSingleThreadExecutor(),null);
        }
        this.games = new ConcurrentHashMap<>();
        this.loadingGames = new ConcurrentHashMap<>();
        this.memorized = new HashSet<>();
        try(BufferedReader fw = new BufferedReader(new FileReader("memorized_games.txt"))) {
            String line;
            while((line = fw.readLine())!=null){
                memorized.add(line);
            }
        } catch (IOException e) {
            new ProjectLogger(getClass().getSimpleName()).
                    log("logs/serverLog.txt",Level.WARNING,"Can't open loaded games",e);
        }
        this.loadLocks = new ConcurrentHashMap<>();
        this.lock = new Object();
    }

    /**
     * Starts listening for clients on a socket.
     */
    public void startServer() {
        try(ServerSocket serverSocket = new ServerSocket(port)){
            while(true){
                try {
                    Socket socket = serverSocket.accept();
                    SocketComm socketComm = new SocketComm(socket);
                    socketComm.setCrashHandler(this);
                    socketComm.startPing();
                    ExecutorService executor = findAssignedExecutor(socketComm);
                    executor.submit(()->handleClient(socketComm));
                } catch (IOException e) {
                    new ProjectLogger(getClass().getSimpleName()).
                            log("logs/serverLog.txt",Level.INFO,"Can't open client socket",e);
                }
            }
        }catch(IOException e){
            new ProjectLogger(getClass().getSimpleName()).
                    log("logs/serverLog.txt",Level.SEVERE,"Can't open client socket",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Joins an RMI Client to the server
     *
     * @param client The client to join.
     * @return The communication instance for the joined client.
     * @throws RemoteException if there's an issue with RMI communication.
     */
    public RemoteComm join(ClientRemote client) throws RemoteException {
        RMIComm comm = new RMIComm(client);
        comm.setCrashHandler(this);
        comm.startPing();
        ExecutorService executor = findAssignedExecutor(comm);
        executor.submit(()->handleClient(comm));
        return comm;
    }


    /**
     * Finds the executor assigned to the comm passed as a parameter
     * if there isn't one, it creates it and links it to the comm
     * @param comm the comm to be linked to the executor
     * @return the executor associated with that comm
     */
    private ExecutorService findAssignedExecutor(Comm comm){
        Map<ExecutorService,Comm> tmp;
        synchronized(this.executors) {
            tmp = new HashMap<>(executors);
        }
        return tmp.keySet()
                .stream()
                .filter(x->tmp.get(x)==comm).
                findFirst().
                orElseGet(()->{
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        synchronized(this.executors){
                            this.executors.put(executor,comm);
                        }
                        return executor;
                });
    }

    /**
     * Asks the client which action they want to perform and handles the corresponding operation,
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
                    comm.stopPing();
                    comm.close();
                    ExecutorService executor = findAssignedExecutor(comm);
                    executors.put(executor,null);
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
                    comm.setCrashHandler(game);
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
     * Connects a client to a game.
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
                            comm.setCrashHandler(game);
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
        boolean available = false,valid;
        comm.sendMsg(MsgCreator.changePhaseMsg(Phase.JOIN));
        while(!available) {
            comm.sendMsg(MsgCreator.chooseMsg());
            id = comm.getMsg();
            if(id.equals("exit")){
                comm.sendMsg(MsgCreator.confirmMsg(true,"returned to menu"));
                return false;
            }
            synchronized (this.games) {
                if (!this.memorized.contains(id) || this.games.containsKey(id)) {
                    valid = false;
                    System.out.println(memorized);
                    comm.sendMsg(MsgCreator.confirmMsg(false, "id not memorized"));
                }else{
                    valid = true;
                }
            }
            if(valid){
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
                    }else{
                        try{
                            LoadLogic logic = new LoadLogic(this,id);
                            this.loadingGames.put(id, logic);
                            logic.addRecord(comm);
                            available = true;
                        } catch (IOException e) {
                            new ProjectLogger(getClass().getSimpleName()).
                                    log("logs/serverLog.txt",Level.INFO,"Can't open selected game",e);
                            comm.sendMsg(MsgCreator.confirmMsg(false, "problem loading game"));
                        }
                    }
                }

            }
        }
        LoadLogic logic = this.loadingGames.get(id);
        comm.setCrashHandler(logic);
        comm.sendMsg(MsgCreator.confirmMsg(true,"Linking name"));
        logic.submitLinkName(comm);
        return true;
    }

    /**
     * Adds the game to the ones currently memorized in the server
     *
     * @param id The unique identifier for the game.
     * @param gc The GameLogic instance representing the game.
     */
    void addGame(String id,GameLogic gc){
        synchronized(this.games){
            this.games.put(id,gc);
        }
        synchronized(this.loadingGames){
            this.loadingGames.remove(id);
        }
    }

    /**
     * Saves a game to a JSON file.
     *
     * @param id        The unique identifier for the game.
     * @param jsonGame  The JSON representation of the game state.
     */
    void saveGame(String id,String jsonGame){
        try {
            FileWriter fw = new FileWriter(id+"_saved.json");
            fw.write(jsonGame);
            fw.flush();
            fw.close();
        }catch(IOException e) {
            new ProjectLogger(getClass().getSimpleName()).
                    log("logs/serverLog.txt",Level.INFO,"Can't save game",e);
        }
    }

    /**
     * Adds a game ID to the list of memorized games and saves the id to a text file.
     *
     * @param id The unique identifier for the game.
     */
    synchronized void addGameToMemorized(String id){
        synchronized(this.games){
            this.memorized.add(id);
        }
        try(FileWriter fw = new FileWriter("memorized_games.txt",true)){
            fw.write(id+"\n");
            fw.flush();
        }catch(IOException e){
            new ProjectLogger(getClass().getSimpleName()).
                    log("logs/serverLogs.txt",Level.WARNING,"Can't find the memorized games",e);
        }
    }

    /**
     * Submits a client to the server based on its executor.
     *
     * @param comm The communication object representing the client request.
     */
    void submitClient(Comm comm) {
        ExecutorService executor = findAssignedExecutor(comm);
        executor.submit(() -> handleClient(comm));
    }

    /**
     * Removes a game from the list of currently active games on the server.
     * <p>
     * This method removes the game identified by the given unique identifier (`id`)
     * from the active games list. However, it does not remove the game from the list
     * of memorized games. To remove the game from the memorized games list, use
     * the {@link CentralServer#closeGame(String)} method.
     * @param id The unique identifier of the game to be removed.
     * @see CentralServer#closeGame(String)
     */
    void removeGame(String id){
        synchronized(this.games){
            this.games.remove(id);
            this.loadingGames.remove(id);
            this.loadLocks.remove(id);
        }
    }

    /**
     * Closes a currently active game on the server.
     * This method eliminates all objects and resources related to the game
     * identified by the given unique identifier (`id`). This includes removing
     * the game from the active games list, clearing any associated data structures,
     * and deleting the save files for that game.
     *
     * @param id The unique identifier of the game to be closed.
     */
    void closeGame(String id){
        synchronized(this.games){
            this.games.remove(id);
            this.memorized.remove(id);
        }
        synchronized(this){
            try(BufferedReader fr = new BufferedReader(new FileReader("memorized_games.txt"))){
                List<String> tmpMemorized = new ArrayList<>();
                String line;
                while((line = fr.readLine())!=null){
                    if(!line.equals(id)){
                        tmpMemorized.add(line);
                    }
                }
                fr.close();
                BufferedWriter bw = new BufferedWriter(new FileWriter("memorized_games.txt"));
                for(String str : tmpMemorized){
                    bw.write(str);
                    bw.newLine();
                }
                bw.flush();
                bw.close();
                Files.delete(Paths.get(id+"_saved.json"));
            }catch(IOException e){
                new ProjectLogger(getClass().getSimpleName()).
                        log("logs/serverLog.txt",Level.WARNING,"Can't delete game file",e);
            }
        }
    }

    /**
     * Handles a crash in the communication process.
     *
     * @param comm The communication object associated with the crashed process.
     */
    @Override
    public void handleCrash(Comm comm) {
        ExecutorService executor = findAssignedExecutor(comm);
        executor.shutdownNow();
        synchronized(this.executors) {
            this.executors.remove(executor);
        }
    }
}