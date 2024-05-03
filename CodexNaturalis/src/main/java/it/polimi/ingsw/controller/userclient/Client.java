package it.polimi.ingsw.controller.userclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.util.CardSerializeLogic;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.view.UserView;
import it.polimi.ingsw.view.coreview.ModelView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/** 
 * Client class represents a client entity.
 */

public abstract class Client extends UnicastRemoteObject {
    final UserView userView;
    private final ModelView model;

    private final Gson gson;
    private final boolean[] token;

    private List<String> names;
    private Map<String, Integer> leaderboard;
    private int idx;

    /**
     * Constructs a new Client instance.
     *
     * @throws RemoteException if a communication-related exception occurs.
     */
    public Client(UserView userView) throws RemoteException {
        this.userView = userView;
        model = new ModelView();
        token = new boolean[2];
        gson = new GsonBuilder().
                registerTypeAdapter(DrawableCard.class,new CardSerializeLogic()).
                create();
    }

    /**
     * Connects the client to the specified server address and port.
     *
     * @param addr the server address to connect to.
     * @param port the port number to use for the connection.
     */
    public abstract void connect(String addr, int port);

    /**
     * Reads user input and processes commands.
     * The method continuously prompts the user for input until the "exit" command is entered.
     */
    private void chooseCommand(){
        String[] cmd;
        boolean stay = true;
        while (stay) {
            String command = userView.getInput();
            cmd = command.split(" ");
            switch (cmd[0]) { //TODO complete switch cases
                case "show" -> {
                    switch (cmd[1]) {
                        case "hand" -> this.userView.show(this.model.getHand().toString()+'\n');
                        case "objective" -> this.userView.show(this.model.getSecrObj()+'\n');
                        case "ground" -> this.userView.show(this.model.getGround().toString()+'\n');
                        case "board" -> {}
                        case "leaderboard" -> {
                            List<String> tmp = this.leaderboard.keySet().stream()
                                                .sorted(Comparator.comparingInt(x -> leaderboard.get(x)))
                                                .toList();

                            for (String name : tmp) {
                                this.userView.show(name + " (id: " + names.indexOf(name) + "): " + leaderboard.get(name)+'\n');
                            }
                        }
                    }
                }
                case "placeCard" -> {
                    if(this.token[0]){
                        placeCard();
                    }
                }
                case "drawCard" -> {
                    if (this.token[1]) {
                        drawCard();
                    }
                }
                case "exit" -> stay = false;
            }
        }
    }

    //TODO SEE INVENTORY; FLIP CARD

    /**
     * Method called by the server for message handling
     * @param command the string containing the commands to be executed
     */
    public void handleMsg(String command){
        String[] s = command.split(";");
        switch (s[0]) {
            case "CHOOSE" -> {
                choose();
            }
            case "SECROBJ" -> this.model.setObj(s[1]);
            case "CREATEHANDVIEW" -> this.model.setHand(this.gson.fromJson(s[1],
                                                        new TypeToken<List<DrawableCard>>(){}.getType()));
            case "TURNORDER" -> {
                this.names = this.gson.fromJson(s[1], new TypeToken<List<String>>() {
                }.getType());
                this.leaderboard = new HashMap<>();
                for(String name: names){
                    this.leaderboard.put(name,0);
                }
            }
            case "UPDHANDVIEW" -> {
                this.model.updHand(this.gson.fromJson(s[1], DrawableCard.class), idx);
                setToken(1, false);
            }
            case "UPDGROUNDVIEW" -> this.model.updGround(Integer.parseInt(s[1]), s[2]);
            case "UPDBOARDVIEW" -> //TODO updBoardView
                    setToken(0, false);
            case "TOKEN" -> {
                switch (s[1]) {
                    case "0" -> setToken(0, true);
                    case "1" -> setToken(1, true);
                }
            }
            case "GAMESTART" -> (new Thread(this::chooseCommand)).start();
            case "DISPLAY" -> userView.show(s[1].replace('_', ' ').replace('&', '\n'));
            case "LEADERBOARD" -> this.leaderboard = this.gson.fromJson(s[1],new TypeToken<Map<String,Integer>>(){}.getType());
        }
    }


    /**
     * Sets the value of a token at the specified index.
     *
     * @param i the index of the token.
     * @param value the new value for the token.
     */
    private void setToken(int i, boolean value) {
        this.token[i] = value;
    }


    abstract void choose(); //TODO ADD CONTROLS FOR THE CLIENT INTPUT

    /**
     * This method represents the action of placing a card.
     */
    abstract void placeCard();

    /**
    * Represents the action of drawing a card.
    */
    abstract void drawCard();

    /**
     * Sets up the placement of a card. Return null if invalid placement.
     *
     * @return A formatted message containing information about the card placement.
     */
    public String placeCardSetUp() {
        String msg = "PLACECARD;";
        String coordinates;
        int idx;
        System.out.println("IDX: ");
        try {
            idx = Integer.parseInt(userView.getInput());
            if (idx < 0 || idx >= this.model.getHandSize()) {
                return null;
            }
            msg += idx + ";";
            this.idx = idx;
        } catch (NumberFormatException e) {
            return null;
        }
        try {
            System.out.println("X,Y: ");
            coordinates = userView.getInput();
            coordinates = coordinates.replace(" ", "");
            if (coordinates.split(",").length != 2) {
                return null;
            }
            Integer.parseInt(coordinates.split(",")[0]);
            Integer.parseInt(coordinates.split(",")[1]);
            msg += coordinates + ";" + this.model.getHandCard(idx).getFace() +";";
        } catch (NumberFormatException e) {
            return null;
        }
        return msg;
    }

    /**
     * Sets up the drawing of a card.
     *
     * @return A formatted message indicating the card to be drawn.
     */
    public String drawCardSetUp() {
        String msg = "DRAW;";
        int idx;
        System.out.println("IDX: ");
        try {
            idx = Integer.parseInt(userView.getInput());
            if (idx < 0 || idx > 5) { //TODO check possible draws
                return null;
            }
            msg += idx + ";";
        } catch (NumberFormatException e) {
            return null;
        }
        return msg;
    }

    /**
     * Indicates that the placement of a card has been accepted.
     * Removes the card from the player's hand.
     */
    void acceptedPlacement() {
        this.model.removeHandCard(idx);
        //TODO: THIS IS NOT NECESSARY
        userView.show("Correct Placement\n");
    }

    /**
     * Indicates that the draw action has been accepted.
     */
    void acceptedDraw() {
        userView.show("Correct draw\n");
    }
}