package it.polimi.ingsw.controller.serverfunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.util.CardSerializeLogic;
import it.polimi.ingsw.model.gameplane.Player;
import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.Symbol;

import java.util.List;
import java.util.Map;

/**
 * Class that handles the creation of messages from the server to the client.
 */
public class MsgCreator {

    private final static Gson gson = new GsonBuilder().registerTypeAdapter(DrawableCard.class, new CardSerializeLogic())
            .create();

    /**
     * Creates a message containing information to update a specific board's
     * position.
     * 
     * @param <T>       The type of the card being placed.
     * @param idx       The index of the player whose board is to be updated.
     * @param x         Relative x-position.
     * @param y         Relative y-position.
     * @param card      The card that has been placed.
     * @param inventory The new inventory values.
     * @param points    The updated points.
     * @return          The message created.
     */
    public static <T extends Card> String updBoardMessage(int idx, int x, int y, T card, Map<Symbol, Integer> inventory,
            int points) {
        return ("UPDBOARDVIEW;" + idx + ";" + "(" + x
                + "," + y + ");" + gson.toJson(card) + ";" + gson.toJson(inventory) + ";" + points);
                //TODO pipeline points
    }

    /**
     * Creates a message containing a string to be displayed.
     * 
     * @param displayText The string to be displayed.
     * @return The message created.
     */
    public static String displayMessage(String displayText) {
        return "DISPLAY;" + format(displayText);
    }

    /**
     * Creates a message containing a string to be displayed and information about the choice to make.
     *
     * @param displayText The string to be displayed.
     * @param pattern The regex pattern to be satisfied.
     * @return The message created.
     */
    public static String chooseMessage(String displayText, String pattern, String error) {
        return displayMessage(displayText) + "|CHOOSE;" + pattern + ";" + MsgCreator.format(error);
    }

    /**
     * Creates a message containing a string to be displayed and information about
     * the choice to make.
     * 
     * @param displayText The string to be displayed.
     * @param min         The lower bound value.
     * @param max         The upper bound value.
     * @return The message created.
     */
    public static String chooseMessage(String displayText, int min, int max, String error) {
        return displayMessage(displayText) + "|CHOOSE;[" + min + "," + max + "];" + MsgCreator.format(error);
    }
    
    /**
     * Creates a message containing a string to be displayed and information about
     * the choice to make.
     * 
     * @param displayText The string to be displayed.
     * @param choose      A list of the possible choices.
     * @return The message created.
     */
    public static <T> String chooseMessage(String displayText, List<T> choose, String error) {
        return displayMessage(displayText) + "|CHOOSE;" + choose.toString().replace('[', '{').replace(']', '}') + ";" + MsgCreator.format(error);
    }

    /**
     * Creates a message to send the secret objective.
     * 
     * @param obj The Objective to be sent.
     * @return The message created.
     */
    public static String secretObjectiveMessage(Objective obj) {
        return "SECROBJ;" + obj.toString();
    }

    /**
     * Creates a message to send the players' turn order.
     * 
     * @param names The orderd list of nicknames.
     * @return The message created.
     */
    public static String turnOrderMessage(List<String> names) {
        return "TURNORDER;" + gson.toJson(names);
    }

    /**
     * Creates a message with the player's hand cards.
     * 
     * @param hand The cards in the player's hand.
     * @return The message created.
     */
    public static String handView(DrawableCard[] hand) {
        return "CREATEHANDVIEW;" + gson.toJson(hand, DrawableCard[].class);
    }

    /**
     * Creates a message with the leaderboard.
     * 
     * @param leaderboard The cards in the player's hand.
     * @return The message created.
     */
    public static String leadeboarMessage(Map<Player, Integer> leaderboard) {
        return "LEADERBOARD;" + gson.toJson(leaderboard);
    }

    /**
     * Creates a message to set the player's ability to place or draw cards.
     * 
     * @param displayText Text to be displayed.
     * @param idx     0 for the placeCard token, 1 for the drawCard token.
     * @return The message created.
     */
    public static String tokenMsg(String displayText, int idx) {
        String msg = displayMessage(displayText);
        return msg + "|TOKEN;" + idx;
    }

    /**
     * Creates a message to update the hand status.
     * 
     * @param card The card that has been drawn.
     * @return The message created.
     */
    public static String updHandViewMsg(DrawableCard card) {
        return "UPDHANDVIEW;" + gson.toJson(card, DrawableCard.class);
    }

    /**
     * Creates a message containing updated points.
     * 
     * @param idx    The index of the player whose points should be updated.
     * @param points The updated points.
     * @return The message created.
     */
    public static String updPointsMsg(int idx, int points) {
        return "UPDPOINTS;" + idx + points;
    }
    
    /**
     * Creates a message to confirm an action.
     * 
     * @param value True if the action succeded, False otherwise.
     * @return The message created.
     */
    public static String confirmMsg(boolean value) {
        return value ? "CONFIRM" : "DENIED";
    }

    /**
     * Creates a message containing the updated ground state.
     * 
     * @param idx  The index of the board item to be updated.
     * @param item The new item on the board value.
     * @return The message created.
     */
    public static <T> String updGroundViewMsg(int idx, T item) {
        return "UPDGROUNDVIEW;" + idx + ";" + gson.toJson(item, item.getClass());
    }

    /**
     * Creates a message instructing the client to start the game.
     * 
     * @return The message created.
     */
    public static String gameStartMsg() {
        return displayMessage("Game started\n") + "|GAMESTART";
    }
    
    /**
     * Concatenates two messages with a command separator.
     * 
     * @param s1 The first message.
     * @param s2 The second message.
     * @return The concatenated message.
     */
    public static String cat(String s1, String s2){
        return s1 + "|" + s2;
    }
    
    //TODO CHANGE METHOD NAME
    /**
     * Formats a string for displaying in-game messages.
     * 
     * @param msg The input message.
     * @return The formatted message.
     */
    private static String format(String msg){
        return msg.replace(' ','_').replace('\n','&');
    }
}
