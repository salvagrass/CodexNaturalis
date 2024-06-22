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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class that waits for players to connect, creates a Comm object for each one, and sends them to a game lobby.
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
            //LOG CAN'T OPEN THE LOADED GAMES
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
                    ExecutorService executor = findAvailableExecutor();
                    this.executors.put(executor,socketComm);
                    executor.submit(()->handleClient(socketComm));
                } catch (IOException e) {
                    String s = "Can't open the client socket: " + e.getMessage();
                    System.out.println(s);
                }
            }
        }catch(IOException e){
            String s = "Can't open the server socket: " + e.getMessage();
            System.out.println(s);
        }
    }

    public RemoteComm join(ClientRemote client) throws RemoteException {
        RMIComm comm = new RMIComm(client);
        comm.setCrashHandler(this);
        comm.startPing();
        ExecutorService executor = findAvailableExecutor();
        this.executors.put(executor,comm);
        executor.submit(()->handleClient(comm));
        return comm;
    }

    private ExecutorService findAvailableExecutor(){
        Map<ExecutorService,Comm> tmp;
        synchronized(this.executors) {
            tmp = new HashMap<>(executors);
        }
        Optional<ExecutorService> optExecutor =
                tmp.keySet().stream().filter(x->tmp.get(x)==null).findFirst();
        return optExecutor.orElseGet(Executors::newSingleThreadExecutor);
    }

    private ExecutorService findAssignedExecutor(Comm comm){
        Map<ExecutorService,Comm> tmp;
        synchronized(this.executors) {
            tmp = new HashMap<>(executors);
        }
        Optional<ExecutorService> optExecutor =
                tmp.keySet().stream().filter(x->tmp.get(x).equals(comm)).findFirst();
        return optExecutor.orElse(null);
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
                if (!this.memorized.contains(id) || this.games.containsKey(id)) {
                    System.out.println(memorized);
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
                        LoadLogic logic = new LoadLogic(this,id);
                        this.loadingGames.put(id, logic);
                        logic.addRecord(comm);
                        available = true;
                    } catch (IOException e) {
                        //TODO HANDLE
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

    void addGame(String id,GameLogic gc){
        synchronized(this.games){
            this.games.put(id,gc);
        }
        synchronized(this.loadingGames){
            this.loadingGames.remove(id);
        }
    }


    void saveGame(String id,String jsonGame){
        try {
            FileWriter fw = new FileWriter(id+"_saved.json");
            fw.write(jsonGame);
            fw.flush();
            fw.close();
        }catch(IOException e) {
            //TODO LOG FILE
            System.out.println("Can't save game");
        }
    }

    synchronized void addGameToMemorized(String id){
        synchronized(this.games){
            this.memorized.add(id);
        }
        try(FileWriter fw = new FileWriter("memorized_games.txt",true)){
            fw.write(id+"\n");
            fw.flush();
        }catch(IOException e){
            //TODO LOG FILE
        }
    }

    void submitClient(Comm comm) {
        ExecutorService executor = findAssignedExecutor(comm);
        executor.submit(() -> handleClient(comm));
    }

    void removeGame(String id){
        synchronized(this.games){
            this.games.remove(id);
            this.loadingGames.remove(id);
            this.loadLocks.remove(id);
        }
    }

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
                //TODO LOG
            }
        }
    }

    @Override
    public void handleCrash(Comm comm) {
        ExecutorService executor = findAssignedExecutor(comm);
        executor.shutdownNow();
        synchronized(this.executors) {
            this.executors.remove(executor);
        }
    }
}