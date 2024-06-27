package it.polimi.ingsw.controller.serverfunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.Coord;
import it.polimi.ingsw.util.PawnColor;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.util.json.CardSerializeLogic;
import it.polimi.ingsw.view.coreview.BoardView;
import it.polimi.ingsw.view.coreview.CardView;
import it.polimi.ingsw.view.coreview.ItemView;
import it.polimi.ingsw.view.coreview.ObjectiveView;
import it.polimi.ingsw.util.Symbol;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that handles the creation of messages from the server to the client.
 */
public class MsgCreator {

    private final static Gson gson = new GsonBuilder().
            enableComplexMapKeySerialization()
            .registerTypeAdapter(Card.class,new CardSerializeLogic())
            .create();

    public static String chooseMsg(){
        return "CHOOSE";
    }

    /**
     * Creates a message containing information to update a specific board's
     * position.
     *
     * @param idx       The index of the player whose board is to be updated.
     * @param x         Relative x-position.
     * @param y         Relative y-position.
     * @param card      The card that has been placed.
     * @param inventory The new inventory values.
     * @return          The message created.
     */
    public static String updBoardMsg(int idx, int x, int y, CardView card, Map<Symbol, Integer> inventory) {
        return ("UPDBOARDVIEW;" + idx + ";" + x
                + ";" + y + ";" + gson.toJson(card, CardView.class) + ";" + gson.toJson(inventory));
    }

    /**
     * Creates the string to inform the client that the colors available to choose have been changed
     * @param colors the new set of colors available to choose
     * @return the string to be sent to the client
     */
    public static String updColorMsg(Set<PawnColor> colors){
        return "UPDCOLORS;" + gson.toJson(colors);
    }

    /**
     * Creates a message to send the players' turn order.
     * 
     * @param names The ordered list of nicknames.
     * @return The message created.
     */
    public static String turnOrderMsg(List<String> names) {
        return "TURNORDER;" + gson.toJson(names);
    }

    /**
     * Creates a message with the player's hand cards.
     * 
     * @param hand The cards in the player's hand.
     * @return The message created.
     */
    public static String createHandViewMsg(CardView[] hand) {
        return "CREATEHANDVIEW;" + gson.toJson(hand, CardView[].class);
    }

    /**
     * Creates a string to inform the client of the created ground view
     * @param resCards the resource cards on the ground
     * @param goldCards the gold cards on the ground
     * @param objs the objectives on the ground
     * @return the string created
     */

    public static String createGroundViewMsg(CardView[] resCards,CardView[] goldCards,ObjectiveView[] objs){
        String items = gson.toJson(resCards, CardView[].class);
        items += ";" + gson.toJson(goldCards,CardView[].class);
        items += ";" + gson.toJson(objs,ObjectiveView[].class);
        return "CREATEGROUNDVIEW;" + items;
    }

    /**
     * Creates a message with the leaderboard.
     * 
     * @param leaderboard A map containing each player nickname and its position
     * @return The message created.
     */
    public static String leadeboardMsg(Map<String, Integer> leaderboard) {
        return "LEADERBOARD;" + gson.toJson(leaderboard);
    }

    /**
     * Creates a message to update the hand view.
     * 
     * @param card The card that has been drawn.
     * @return The message created.
     */
    public static String updHandViewMsg(CardView card) {
        return "UPDHANDVIEW;" + gson.toJson(card, CardView.class);
    }

    /**
     * Creates a message containing the new points of the player
     * identified with that idx in the turn order.
     * 
     * @param idx    The index of the player whose points should be updated.
     * @param points The updated points.
     * @return The message created.
     */
    public static String updPointsMsg(int idx, int points) {
        return "UPDPOINTS;" + idx + ";" + points;
    }
    
    /**
     * Creates a message to confirm an action.
     * 
     * @param value {@code true} if the action succeded,{@code false} otherwise.
     * @return The message created.
     */
    public static String confirmMsg(boolean value,String info) {
        return (value ? "CONFIRM" : "DENIED") + ";" + MsgCreator.format(info);
    }

    /**
     * Creates a message containing the updated ground state.
     * 
     * @param idx  The index of the ground item to be updated.
     * @param item The new view of the item on the ground.
     * @return The message created.
     */
    public static String updGroundViewMsg(int idx, CardView item) {
        return "UPDGROUNDVIEW;" + idx + ";" + gson.toJson(item);
    }

    /**
     * Creates a message with the name of the player added to the game
     *
     * @param name The name to include in the message.
     * @return The formatted message string.
     */
    public static String nameMsg(String name) {
        return "NAME;" + name;
    }

    /**
     * Creates a message informing the client of the number of players
     * that will be playing in the game
     *
     * @param cap The capacity of the game.
     * @return The formatted message string.
     */
    public static String capacityMsg(int cap) {
        return "CAPACITY;" + cap;
    }

    /**
     * Creates a message informing the client of the players who joined the game
     *
     * @param names the nicknames of the players that joined the game
     * @return The formatted message string.
     */
    public static String joinedMsg(Set<String> names) {
        return "JOINED;" + gson.toJson(names, new TypeToken<Set<String>>() {
        }.getType());
    }

    /**
     * Creates a message string for changing the game phase.
     *
     * @param phase The game phase (e.g., STARTCARD, OBJECTIVE).
     * @param items Optional ItemView objects associated with the phase.
     * @return The formatted message string.
     */
    public static String changePhaseMsg(Phase phase,ItemView... items) {
        String msg = "CHANGEPHASE;" + phase.name();
        if(items.length>0){
            msg += ";";
            msg += switch(phase){
                case Phase.STARTCARD -> gson.toJson(items,CardView[].class);
                case Phase.OBJECTIVE -> gson.toJson(items, ObjectiveView[].class);
                default -> "";
            };
        }
        return msg;
    }

    /**
     * Sends to the client the secret objective the player picked in the saved game
     *
     * @param objective The picked objective.
     * @return The formatted message string.
     */
    public static String loadHiddenObjMsg(Objective objective){
        return "LOADHIDDENOBJ;" + gson.toJson(new ObjectiveView(objective),ObjectiveView.class);
    }

    /**
     * Creates a message that informs the client of its game board in the saved game
     *
     * @param id The identifier for the saved game.
     * @param index The index of the player whose board is being loaded.
     * @return A formatted game board message.
     * @throws IOException If there's an error reading the saved game file.
     */
    public static String loadBoardMsg(String id,int index) throws IOException{
        String msg = "LOADBOARD;" + index + ";";
        FileReader fr = new FileReader(id+"_saved.json");
        JsonObject savedGame = gson.fromJson(fr, JsonElement.class).getAsJsonObject();
        fr.close();
        JsonObject jsonBoard = savedGame.get("turnOrder")
                .getAsJsonArray().get(index).getAsJsonObject().get("playingField").getAsJsonObject();
        JsonElement jsonMatrix = jsonBoard.get("board");
        Map<Symbol,Integer> inventory =
                gson.fromJson(jsonBoard.get("countResources"),new TypeToken<HashMap<Symbol,Integer>>(){}.getType());
        Card[][] matrix = gson.fromJson(jsonMatrix,Card[][].class);
        List<Coord> cardOrder = gson.fromJson(jsonBoard.get("cardOrder"), new TypeToken<List<Coord>>(){}.getType());
        BoardView loadedView = new BoardView(matrix,inventory,cardOrder);
        msg += gson.toJson(loadedView,BoardView.class);
        return msg;
    }

    /**
     * Creates a message indicating the current player.
     *
     * @param name The name of the current player.
     * @return A formatted current player message.
     */
    public static String currPlayerMsg(String name) {
        return "CURRPLAYER;" + name;
    }

    /**
     * Creates a message for placing a card on the game board.
     *
     * @param idx The index of the card.
     * @param x The x-coordinate where the card is placed.
     * @param y The y-coordinate where the card is placed.
     * @param face Whether the card is face-up (true) or face-down (false).
     * @return A formatted place card message.
     */
    public static String placeCardMsg(int idx,int x,int y,boolean face){
        return "PLACECARD;" + idx + ";" + x + ";" + y + ";" + face;
    }

    /**
     * Creates a message for drawing a card.
     *
     * @param idx The index of the card to be drawn.
     * @return A formatted draw card message.
     */
    public static String drawCardMsg(int idx){
        return "DRAWCARD;" + idx;
    }

    /**
     * Represents a chat message.
     *
     * @param sender   The sender of the message.
     * @param receiver The recipient of the message.
     * @param text     The content of the message.
     * @return A formatted chat message.
     */
    public static String chatMsg(String sender, String receiver, String text){
        return "CHAT;" + receiver + ";" + sender + ";" + text;
    }

    /**
     * Creates a formatted message indicating the chosen color for a player.
     *
     * @param player The player's name.
     * @param color  The chosen color (e.g., RED, BLUE, GREEN, YELLOW).
     * @return A formatted message with the chosen color and player name.
     */
    public static String colorChosenMsg(String player, PawnColor color){
        return "COLORCHOSEN;" + player + ";" + color.name();
    }

    /**
     * Concatenates two messages with a command separator.
     * 
     * @param s1 The first message.
     * @param s2 The second message.
     * @return The concatenated message.
     */
    public static String cat(String s1, String s2) {
        return s1 + "|" + s2;
    }

    /**
     * Replaces spaces with underscores and newline characters with ampersands in the given message.
     *
     * @param msg The input message.
     * @return The formatted message.
     */
    private static String format(String msg) {
        return msg.replace(' ', '_').replace('\n', '&');
    }

    /**
     * Replaces underscores with spaces and ampersands with newline characters in the given message.
     *
     * @param msg The input message.
     * @return The deformatted message.
     */
    public static String deformat(String msg) {
        return msg.replace('_', ' ').replace('&', ' ');
    }
}
