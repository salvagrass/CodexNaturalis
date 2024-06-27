package it.polimi.ingsw.view.controller;

import it.polimi.ingsw.util.PawnColor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Controller for the EndGame scene.
 */
public class EndGameController extends Controller{
    @FXML
    private Label message;
    @FXML
    private VBox leaderboard;

    /**
     * Displays the final results of a game.
     *
     * @param name      The name of the player.
     * @param colors    A map of player names to their corresponding pawn colors.
     * @param positions A map of player names to their positions.
     * @param points    A map of player names to their points.
     */
    public void show(String name, Map<String, PawnColor> colors, Map<String, Integer> positions, Map<String, Integer> points){
        if(positions.get(name) == 1){
            this.message.setText("Winner");
        }else{
            this.message.setText("Loser");
        }

        List<String> names = positions.keySet().stream().sorted(Comparator.comparingInt(
                positions::get)).toList();
        for (String n : names) {
            Label player = new Label(positions.get(n) + " " + n + ": " + points.get(n));
            player.setStyle("-fx-text-fill: " + colors.get(n) + "; -fx-font-size: 30px; -fx-font-family: 'BlackChauncery'; -fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0);");
            this.leaderboard.getChildren().add(player);
        }
    }

    /**
     * Allows the GUI to change phase in order to change scene.
     */
    public void changePhase(){
        this.gui.changePhase();
    }
}
