package it.polimi.ingsw.controller.userclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.controller.serverfunction.MsgCreator;
import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.json.CardSerializeLogic;
import it.polimi.ingsw.util.ObjectiveSerializeLogic;
import it.polimi.ingsw.util.Symbol;
import it.polimi.ingsw.view.UserView;
import it.polimi.ingsw.view.coreview.BoardView;
import it.polimi.ingsw.view.coreview.CardView;
import it.polimi.ingsw.view.coreview.ModelView;
import it.polimi.ingsw.view.coreview.ObjectiveView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

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
                registerTypeHierarchyAdapter(Card.class,new CardSerializeLogic()).
                registerTypeHierarchyAdapter(Objective.class,new ObjectiveSerializeLogic()).
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
            //TODO SYNCHRONIZE THIS PART WITH THE CONFIRM
            String command = userView.getInput();
            cmd = command.split("(\\s+(?=-?\\d))|(\\s+$)");
            switch (cmd[0]) { //TODO complete switch cases
                case "show hand" -> this.userView.showCards(this.model.getHand());
                case "show objective" -> this.userView.show(
                        ObjectiveView.showAsciiArt(this.model.getSecrObj()));
                case "show ground" -> this.userView.show(this.model.getGround().asciiGround());
                case "show card" -> {
                    try {
                        int userIdx = Integer.parseInt(cmd[1]);
                        int x = Integer.parseInt(cmd[2]);
                        int y = Integer.parseInt(cmd[3]);
                        this.userView.show(this.model.getBoard(userIdx).showCard(x,y));
                    }catch(Exception e){
                        this.userView.show("Not an index\n");
                    }
                }
                case "show board" -> {
                    try {
                        int idx = Integer.parseInt(cmd[1]);
                        this.userView.show(this.model.getBoard(idx).asciiBoard());
                    }catch(Exception e){
                        this.userView.show("Not an index\n");
                    }
                }
                case "show leaderboard" -> {
                    List<String> tmp = this.leaderboard.keySet().stream()
                            .sorted(Comparator.comparingInt(x -> leaderboard.get(x)).reversed())
                            .toList();
                    for (String name : tmp) {
                        this.userView.show(name + " (id: " + names.indexOf(name) + "): " + leaderboard.get(name)+'\n');
                    }
                }
                case "flip card" -> {
                    try{
                        int idx = Integer.parseInt(cmd[1]);
                        if(idx>=0 && idx<this.model.getHand().size()){
                            CardView view = this.model.getHandCard(idx);
                            view.setFace(!view.getFace());
                        }else{
                            this.userView.show("Index not supported\n");
                        }
                    }catch(NumberFormatException e){
                        this.userView.show("Not a number\n");
                    }
                }
                case "place card" -> {
                    if(this.token[0]){
                        placeCard();
                    }
                }
                case "draw card" -> {
                    if (this.token[1]) {
                        drawCard();
                    }
                }
                case "exit" -> stay = false;
                default -> this.userView.show("Command not supported\n");
            }
        }
    }

    /**
     * Method called by the server for message handling
     * @param command the string containing the commands to be executed
     */
    public void handleMsg(String command){
        String[] s = command.split(";");
        switch (s[0]) {
            case "CHOOSE" -> {
                if(!handleChoose(s[1])){
                    this.userView.show(MsgCreator.deformat(s[2])+'\n');
                    this.handleMsg(command);
                }
            }
            case "SECROBJ" -> this.model.setObj(gson.fromJson(s[1],Objective.class));
            case "CREATEHANDVIEW" -> {
                List<DrawableCard> cards = this.gson.fromJson(s[1],new TypeToken<List<DrawableCard>>(){}.getType());
                this.model.setHand(cards);
            }
            case "STARTINGCARD" -> {
                StartingCard card = gson.fromJson(s[1],StartingCard.class);
                CardView[] startCardView = new CardView[2];
                startCardView[0] = new CardView(card);
                card.setShowingFace(true);
                startCardView[1] = new CardView(card);
                this.userView.showCards(startCardView);
            }
            case "TURNORDER" -> {
                this.names = this.gson.fromJson(s[1], new TypeToken<List<String>>() {
                }.getType());
                this.leaderboard = new HashMap<>();
                for(String name: names){
                    this.leaderboard.put(name,0);
                }
                this.model.setBoards(this.leaderboard.size());
            }
            case "UPDHANDVIEW" -> {
                this.model.updHand(this.gson.fromJson(s[1], Card.class), idx);
                setToken(1, false);
            }
            case "COMMONOBJECTIVES" -> {
                Objective[] commonObjectives = gson.fromJson(s[1],Objective[].class);
                ObjectiveView[] objectivesView = new ObjectiveView[commonObjectives.length];
                for(int i=0;i<objectivesView.length;i++){
                    objectivesView[i] = new ObjectiveView(commonObjectives[i]);
                }
                this.userView.show(ObjectiveView.showAsciiArt(objectivesView));
            }
            case "UPDGROUNDVIEW" -> this.model.updGround(Integer.parseInt(s[1]), s[2]);
            case "UPDBOARDVIEW" -> {
                //TODO REVISE
                int turnIdx = Integer.parseInt(s[1]);
                BoardView board = this.model.getBoard(turnIdx);
                String[] boardIdxs = s[2].split(",");
                int x = Integer.parseInt(boardIdxs[0].substring(1));
                int y = Integer.parseInt(boardIdxs[1].substring(0,boardIdxs[1].length()-1));
                Card card = this.gson.fromJson(s[3],Card.class);
                Map<Symbol,Integer> inventory = this.gson.fromJson(s[4],new TypeToken<Map<Symbol,Integer>>(){}.getType());
                board.updBoardView(card,x,y,inventory);
                setToken(0, false);
            }
            case "UPDPOINTS" -> {
                int playerIdx = Integer.parseInt(s[1]);
                int points = Integer.parseInt(s[2]);
                leaderboard.put(names.get(playerIdx),points);
            }
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


    boolean handleChoose(String domain) {
        if (domain.matches("\\[-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?]")) { //[min,max]
            int min, max;
            domain = domain.substring(1, domain.length() - 1);
            min = Integer.parseInt(domain.split(",")[0]);
            max = Integer.parseInt(domain.split(",")[1]);
            return choose(min, max);
        } else if (domain.matches("\\{([^,]+,)*[^,]+}")) { //{x,y,...,z}
            List<String> choices = new LinkedList<>();
            domain = domain.substring(1, domain.length() - 1);
            String[] parts = domain.split(",");
            for (String part : parts) {
                choices.add(part.trim());
            }
            return choose(choices);
        } else {
            return choose(domain);
        }
    }

    abstract boolean choose(int min, int max);

    abstract boolean choose(List<String> choices);

    abstract boolean choose(String pattern);

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