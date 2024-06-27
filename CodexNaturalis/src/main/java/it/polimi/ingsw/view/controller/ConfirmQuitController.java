package it.polimi.ingsw.view.controller;

import it.polimi.ingsw.view.GUI;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ConfirmQuitController extends Controller {

    private Stage stage;
    private GUI gui;

    /**
     * Sets the stage for this object.
     *
     * @param stage The stage to set.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the GUI for this object.
     *
     * @param gui The GUI to set.
     */
    public void setGUI(GUI gui){
        this.gui = gui;
    }

    /**
     * Closes the stage and sends input to close the GUI.
     */
    @FXML
    private void confirmAction() {
        this.gui.scanInput("exit");
        this.stage.close();
    }

    /**
     * Closes the stage.
     */
    @FXML
    private void cancelAction() {
        this.stage.close();
    }
}

