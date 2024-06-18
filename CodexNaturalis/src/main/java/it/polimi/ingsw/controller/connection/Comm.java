package it.polimi.ingsw.controller.connection;

import it.polimi.ingsw.controller.serverfunction.CrashHandler;
import it.polimi.ingsw.controller.serverfunction.GameLogic;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public Comm() {
        this.clientCrashed = new AtomicBoolean(false);
    }

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
                    throw new RuntimeException();
                }
            }
            s = this.chosen;
            this.chosen = null;
        }
        return s;
    }

    public void clean(){
        this.chosen = null;
    }

    void setChosen(String msg){
        synchronized(this) {
            this.chosen = msg;
            this.notifyAll();
        }
    }

    public void setGame(GameLogic game){
        this.game = game;
    }

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



    public void stopPing(){
        pingExec.shutdownNow();
    }

    public void startPing(){
        this.clientCrashed.set(false);
        pingExec = Executors.newSingleThreadScheduledExecutor();
        pingExec.scheduleAtFixedRate(this::pingMethod, 0, 5, TimeUnit.SECONDS);
    }


    public abstract void pingMethod();
    /**
     * Closes the connection.
     */
    public abstract void close();
}
