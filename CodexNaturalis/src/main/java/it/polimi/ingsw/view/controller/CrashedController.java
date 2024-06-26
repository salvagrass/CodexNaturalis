package it.polimi.ingsw.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CrashedController extends Controller {
    private Stage stage;
    @FXML
    private Label title;

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void back(){ this.stage.close(); }

    public void setTitle(String message) {
        this.title.setText(message);
    }
}
