package it.polimi.ingsw.util.json;

import it.polimi.ingsw.controller.connection.Comm;
import it.polimi.ingsw.model.gameplane.CommonGround;
import it.polimi.ingsw.model.gameplane.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveData {
    private final String id;
    private final CommonGround ground;
    private final boolean endGame;
    private final List<Player> turnOrder;
    private final String currPlayer;

    /**
     * Saves data.
     *
     * @param id The ID of the data.
     * @param ground The common ground.
     * @param endGame Whether it's the end of the game.
     * @param players A map of communication channels to players.
     * @param turnOrder The order of turns.
     * @param currPlayer The current player.
     */
    public SaveData(String id, CommonGround ground, boolean endGame, Map<Comm,Player> players, List<Comm> turnOrder, Comm currPlayer) {
        this.id = id;
        this.ground = ground;
        this.endGame = endGame;
        this.turnOrder = new ArrayList<>(turnOrder.size());
        for(Comm comm: turnOrder){
            this.turnOrder.add(players.get(comm));
        }
        this.currPlayer = players.get(currPlayer).getNickname();
    }

    /**
     * Gets the ID of the data.
     *
     * @return The ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the common ground.
     *
     * @return The common ground.
     */
    public CommonGround getGround() {
        return ground;
    }

    /**
     * Checks whether it's the end of the game.
     *
     * @return `true` if it's the end of the game, otherwise `false`.
     */
    public boolean isEndGame() {
        return endGame;
    }

    /**
     * Gets the turn order of players.
     *
     * @return A list of players representing the turn order.
     */
    public List<Player> getTurnOrder() {
        return turnOrder;
    }

    /**
     * Returns the current player.
     *
     * @return The current player's name.
     */
    public String getCurrPlayer() {
        return currPlayer;
    }
}
