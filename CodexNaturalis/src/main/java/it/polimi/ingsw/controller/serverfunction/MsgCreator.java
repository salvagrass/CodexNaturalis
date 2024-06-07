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

    public static String updColorMsg(Set<PawnColor> colors){
        return "UPDCOLORS;" + gson.toJson(colors);
    }

    /**
     * Creates a message to send the players' turn order.
     * 
     * @param names The orderd list of nicknames.
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

    public static String createGroundViewMsg(CardView[] resCards,CardView[] goldCards,ObjectiveView[] objs){
        String items = gson.toJson(resCards, CardView[].class);
        items += ";" + gson.toJson(goldCards,CardView[].class);
        items += ";" + gson.toJson(objs,ObjectiveView[].class);
        return "CREATEGROUNDVIEW;" + items;
    }

    /**
     * Creates a message with the leaderboard.
     * 
     * @param leaderboard The cards in the player's hand.
     * @return The message created.
     */
    public static String leadeboardMsg(Map<String, Integer> leaderboard) {
        return "LEADERBOARD;" + gson.toJson(leaderboard);
    }

    /**
     * Creates a message to update the hand status.
     * 
     * @param card The card that has been drawn.
     * @return The message created.
     */
    public static String updHandViewMsg(CardView card) {
        return "UPDHANDVIEW;" + gson.toJson(card, CardView.class);
    }

    /**
     * Creates a message containing updated points.
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
     * @param value True if the action succeded, False otherwise.
     * @return The message created.
     */
    public static String confirmMsg(boolean value,String info) {
        return (value ? "CONFIRM" : "DENIED") + ";" + MsgCreator.format(info);
    }

    /**
     * Creates a message containing the updated ground state.
     * 
     * @param idx  The index of the board item to be updated.
     * @param item The new item on the board value.
     * @return The message created.
     */
    public static String updGroundViewMsg(int idx, CardView item) {
        return "UPDGROUNDVIEW;" + idx + ";" + gson.toJson(item);
    }

    public static String nameMsg(String name) {
        return "NAME;" + name;
    }
    
    public static String capacityMsg(int cap) {
        return "CAPACITY;" + cap;
    }

    public static String joinedMsg(Set<String> names) {
        return "JOINED;" + gson.toJson(names, new TypeToken<Set<String>>() {
        }.getType());
    }
    
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

    public static String loadHiddenObjMsg(Objective objective){
        return "LOADHIDDENOBJ;" + gson.toJson(new ObjectiveView(objective),ObjectiveView.class);
    }

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

    public static String currPlayerMsg(String name) {
        return "CURRPLAYER;" + name;
    }


    public static String placeCardMsg(int idx,int x,int y,boolean face){
        return "PLACECARD;" + idx + ";" + x + ";" + y + ";" + face;
    }

    public static String drawCardMsg(int idx){
        return "DRAWCARD;" + idx;
    }
    public static String chatMsg(String sender, String receiver, String text){
        return "CHAT;" + receiver + ";" + sender + ";" + text;
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
    
    private static String format(String msg) {
        return msg.replace(' ', '_').replace('\n', '&');
    }

    public static String deformat(String msg) {
        return msg.replace('_', ' ').replace('&', ' ');
    }
}
