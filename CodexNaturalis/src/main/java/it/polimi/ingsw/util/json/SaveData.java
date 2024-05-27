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

    public String getId() {
        return id;
    }

    public CommonGround getGround() {
        return ground;
    }

    public boolean isEndGame() {
        return endGame;
    }

    public List<Player> getTurnOrder() {
        return turnOrder;
    }

    public String getCurrPlayer() {
        return currPlayer;
    }
}
