package it.polimi.ingsw.controller.connection;

import it.polimi.ingsw.controller.serverfunction.CrashHandler;
import it.polimi.ingsw.controller.serverfunction.GameLogic;
import it.polimi.ingsw.util.ProjectLogger;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * A class that handles communication with the server based on the connected client type.
 * It is intended to be inherited by classes that implement specific types of connections.
 */
public abstract class Comm{
    private GameLogic game;
    private String chosen;
    private ScheduledExecutorService pingExec;
    final AtomicBoolean clientCrashed;
    CrashHandler handler;

    /**
     * Creates a new instance of the `Comm` class.
     * Initializes the `clientCrashed` field with `false`.
     */
    public Comm() {
        this.clientCrashed = new AtomicBoolean(false);
    }

    /**
     * Sets the crash handler for this instance.
     *
     * @param handler The crash handler to set.
     */
    public void setCrashHandler(CrashHandler handler){
        this.handler = handler;
    }

    /**
     * Sends a message to the client.
     *
     * @param msg The message to be sent.
     */
    public abstract void sendMsg(String msg);

    /**
     * Retrieves the answer previously obtained with the setMsg method.
     *
     * @return The answer obtained from the Client.
     */
    public String getMsg() {
        String s;
        synchronized (this) {
            while (this.chosen == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    new ProjectLogger(getClass().getSimpleName()).
                            log("logs/commLog.txt",Level.INFO,"Thread interrupted while waiting",e);
                    throw new RuntimeException();
                }
            }
            s = this.chosen;
            this.chosen = null;
        }
        return s;
    }
    /**
     * Clears the `chosen` field by setting it to `null`.
     */
    public void clean(){
        this.chosen = null;
    }

    /**
     * Sets the value of the `chosen` field and notifies all waiting threads.
     *
     * @param msg The value to set for `chosen`.
     */
    void setChosen(String msg){
        synchronized(this) {
            this.chosen = msg;
            this.notifyAll();
        }
    }

    /**
     * Sets the game logic for this object.
     *
     * @param game The GameLogic instance to set.
     */
    public void setGame(GameLogic game){
        this.game = game;
    }

    /**
     * Returns the `GameLogic` instance associated with this object.
     *
     * @return The `GameLogic` instance.
     */
    public GameLogic getGame() {
        return this.game;
    }

    /**
     * Sends a message to every Communication (Comm) object present in the turnOrder list.
     *
     * @param msg The message to be sent to everyone.
     * @param turnOrder The list of Communication (Comm) objects to which the message will be sent.
     */
    public static void sendBroadcast(String msg, List<Comm> turnOrder) {
        for (Comm comm : turnOrder) {
            comm.sendMsg(msg);
        }
    }

    /**
     * Stops the ping execution by shutting down the associated executor.
     */
    public void stopPing(){
        pingExec.shutdownNow();
    }

    /**
     * Starts the ping execution by initializing the necessary components.
     */
    public void startPing(){
        this.clientCrashed.set(false);
        pingExec = Executors.newSingleThreadScheduledExecutor();
        pingExec.scheduleAtFixedRate(this::pingMethod, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * This abstract class defines the contract for ping methods.
     */
    public abstract void pingMethod();

    /**
     * Closes the connection.
     */
    public abstract void close();
}
