package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.controller.connection.RMIComm;
import it.polimi.ingsw.controller.connection.RemoteComm;
import it.polimi.ingsw.controller.connection.SocketComm;
import it.polimi.ingsw.controller.userclient.RemoteClient;
import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.ObjectiveSerializeLogic;
import it.polimi.ingsw.util.json.CardSerializeLogic;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.view.GameScene;

/**
 * A class that waits for players to connect, creates a Comm object for each one, and sends them to a game lobby.
 */
public class CentralServer extends UnicastRemoteObject implements RemoteServer {
    private final int port;
    private final ExecutorService executor;
    private final Map<String,GameLogic> games;
    private final List<String> activeGames;
    private final List<String> savedGamesId;
    private final Map<String,SaveLogic> loadedGames;
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
        this.loadedGames = new ConcurrentHashMap<>();
        this.lock = new Object();
        this.activeGames = Collections.synchronizedList(new ArrayList<>());
        this.savedGamesId = Collections.synchronizedList(new ArrayList<>());
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
        boolean valid = true;
        do {
            comm.sendMsg(MsgCreator.chooseMessage("1)Create Game\n2)Join Game\n3)Load Game\n",1,3, "Invalid choice, try again!"));
            String cmd = comm.getMsg();
            comm.sendMsg(MsgCreator.chooseMessage("Insert Room ID (6 alphanumeric characters): ", "^[a-zA-Z0-9]{6}$", "Invalid ID: 6 alphanumeric characters"));
            String id = comm.getMsg();
            switch (cmd) {
                case "1":
                    valid = createGame(id, comm);
                    if(!valid)
                        break;
                case "2":
                    valid = joinGame(id, comm);
                    break;
                case "3":
                    valid = loadGame(id, comm);
                    break;
            }
            if(!valid){
                comm.sendMsg(MsgCreator.changeScenary(GameScene.MENU));
            }
        }while(!valid);
    }

    /**
     * Creates a GameLogic instance to handle a game.
     *
     * @param id The ID of the lobby.
     * @param comm The connection to the specific client.
     * @return True if the creation succeeds, false otherwise.
     */
    private boolean createGame(String id, Comm comm) {
        synchronized (this.activeGames) {
            if(this.activeGames.contains(id)||this.savedGamesId.contains(id)) {
                comm.sendMsg(MsgCreator.displayMessage("ID already exists\n"));
                return false;
            }
            activeGames.add(id);
        }
        comm.sendMsg(MsgCreator.changeScenary(GameScene.NOTHING));
        comm.sendMsg(MsgCreator.chooseMessage("Insert lobby capacity: ", 2,4, "Invalid choice, try again!"));
        int numPlayers = Integer.parseInt(comm.getMsg());
        GameLogic game = new GameLogic(this,numPlayers);
        games.put(id, game);

        comm.sendMsg(MsgCreator.displayMessage("Game with ID created\n"));
        return true;
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
                return false;
            }
            game = games.get(id);
            synchronized(game){
                if(game.isFull()){
                    comm.sendMsg(MsgCreator.displayMessage("Full lobby\n"));
                    return false;
                }
                comm.sendMsg(MsgCreator.displayMessage("Joining Game\n"));
                comm.setGame(game);
                game.addRecord(comm);
            }
        }
        executor.submit(()->game.addPlayer(comm));
        return true;
    }

    /**
     * Load a previously saved game.
     *
     * @param id The ID of the lobby.
     * @param comm The connection to the specific client.
     * @return True if the load succeeds, false otherwise.
     */
    private boolean loadGame(String id, Comm comm) {
        SaveLogic game;
        synchronized (this.activeGames) {
            if (this.activeGames.contains(id)) {
                game = this.loadedGames.get(id);
                if (game.isFull()) {
                    comm.sendMsg(MsgCreator.displayMessage("Game already full\n"));
                    return false;
                }
            } else {
                try {
                    FileReader fileReader = new FileReader(id + "_saved.json");
                    Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Card.class, new CardSerializeLogic())
                            .registerTypeHierarchyAdapter(Objective.class, new ObjectiveSerializeLogic())
                            .setPrettyPrinting()
                            .create();
                    game = gson.fromJson(fileReader, SaveLogic.class);
                    this.activeGames.add(id);
                    this.loadedGames.put(id, game);
                    fileReader.close();
                }catch(FileNotFoundException e) {
                    comm.sendMsg(MsgCreator.displayMessage("Id does not exist\n"));
                    return false;
                }catch(IOException e){
                    //TODO HANDLE
                    throw new RuntimeException("Can't close saved file");
                }
            }
            game.addRecord(comm);
        }
        game.join(comm, this);
        return true;
    }

    public void saveGame(GameLogic game, String jsonGame){
        String s = this.games.keySet().stream().filter(x->games.get(x)==game).findFirst().get();
        try(FileWriter fw = new FileWriter(s+"_saved.json")){
            fw.write(jsonGame);
            synchronized (this.activeGames) {
                this.savedGamesId.add(s);
            }
        } catch (IOException e) {
            //TODO log file cannot save
        }
    }
}