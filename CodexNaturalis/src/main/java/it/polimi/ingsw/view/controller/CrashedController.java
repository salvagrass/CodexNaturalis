package it.polimi.ingsw.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the Crash scene.
 */
public class CrashedController extends Controller {
    private Stage stage;
    @FXML
    private Label title;

    /**
     * Sets the stage for this object.
     *
     * @param stage The stage to set.
     */
    public void setStage(Stage stage){
        this.stage = stage;
    }

    /**
     * Closes the stage.
     */
    public void back(){ this.stage.close(); }

    /**
     * Sets the text of the label.
     *
     * @param message The new text to set.
     */
    public void setTitle(String message) {
        this.title.setText(message);
    }
}
