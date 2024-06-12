package it.polimi.ingsw.view.controller;

import it.polimi.ingsw.util.PawnColor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class EndGameController extends Controller{
    @FXML
    private Label message;
    @FXML
    private VBox leaderboard;

    public void show(String name, Map<String, PawnColor> colors, Map<String, Integer> positions, Map<String, Integer> points){
        if(positions.get(name) == 1){
            this.message.setText("WINNER");
        }else{
            this.message.setText("LOSER");
        }

        List<String> names = positions.keySet().stream().sorted(Comparator.comparingInt(
                positions::get)).toList();
        for (String n : names) {
            Label player = new Label(positions.get(n) + " " + n + ": " + points.get(n));
            player.setStyle("-fx-text-fill: " + colors.get(n) + ";-fx-font: 30 arial");
            this.leaderboard.getChildren().add(player);
        }
    }

    public void changePhase(){
        this.gui.changePhase();
    }
}
