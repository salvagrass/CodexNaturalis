package it.polimi.ingsw.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the Id scene.
 */
public class IdController extends Controller{
    @FXML
    private Button submit;
    @FXML
    private TextField getId;
    @FXML
    private Label error;

    /**
     * Initializes the controller by setting up event listeners and handling user input.
     * If the getId TextField contains an invalid Id it disables the submit button.<br/>
     * Valid id: 6 alphanumeric characters.
     */
    public void initialize(){
        this.getId.textProperty().addListener((observable, oldVal, newVal) -> {
            this.submit.setDisable(!newVal.matches("^[a-zA-Z0-9]{6}$"));
            if(this.error.isVisible())
                this.error.setVisible(false);
        });
        this.getId.setOnAction(event -> {
            if(!this.submit.isDisable()){
                submit();
            }
        });
    }

    /**
     * Submits the user text input.
     */
    public void submit() {
        this.gui.scanInput(this.getId.getText());
    }

    /**
     * Submits the desire to return to the menu.
     */
    public void exit() {
        this.gui.scanInput("exit");
        clear();
    }

    /**
     * Clears the input field and disables the submit button.
     */
    public void clear(){
        this.getId.clear();
        this.submit.setDisable(true);
    }

    /**
     * Confirms an action based on the provided parameters.
     * Displays an error message if the confirmation value is false.
     *
     * @param confirmValue The confirmation value (true or false).
     * @param confirmMsg   The message to display.
     */
    @Override
    public void confirm(boolean confirmValue,String confirmMsg){
        if(!confirmValue){
            this.error.setText(confirmMsg);
            this.error.setVisible(true);
        }
    }
}
