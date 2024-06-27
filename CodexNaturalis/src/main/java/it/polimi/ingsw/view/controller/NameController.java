package it.polimi.ingsw.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Submit the name choice.
 */
public class NameController extends Controller {
    @FXML
    Button submit;
    @FXML
    TextField getName;
    @FXML
    Label error;

    /**
     * Initializes the components and event listeners,
     * enabling or disabling the `submit` button based on the input.<br/>
     * Valid input: 1 to 8 alphanumeric characters.
     */
    public void initialize(){
        this.getName.textProperty().addListener((observable,oldVal,newVal)->{
            submit.setDisable(!newVal.matches("^[a-zA-Z]{1,8}$"));
            if(this.error.isVisible())
                this.error.setVisible(false);
        });
        this.getName.setOnAction(event -> {
            if(!this.submit.isDisable()){
                submit();
            }
        });
    }

    /**
     * Clears the input field and disables the submit button.
     */
    public void clear(){
        this.getName.clear();
        this.submit.setDisable(true);
    }

    /**
     * Submits the inputted text.
     */
    public void submit() {
        this.gui.scanInput(this.getName.getText());
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
