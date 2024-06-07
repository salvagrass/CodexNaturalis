package it.polimi.ingsw.view.controller;

import it.polimi.ingsw.view.GUI;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ConfirmQuitController extends Controller {

    private Stage stage;
    private GUI gui;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setGUI(GUI gui){
        this.gui = gui;
    }

    @FXML
    private void confirmAction() {
        this.gui.scanInput("exit");
        this.stage.close();
    }

    @FXML
    private void cancelAction() {
        this.stage.close();
    }
}

