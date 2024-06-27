package it.polimi.ingsw.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

/**
 * Controller for the Create scene.
 */
public class CreateController extends Controller {
    @FXML
    Button submit;
    @FXML
    TextField getId;
    @FXML
    Slider getCapacity;
    @FXML
    Label error;

    /**
     * Initializes the component.
     * When the user enters a valid ID the submit button is enabled.<br/>
     * Valid ID: 6 alphanumerics characters.
     */
    public void initialize(){
        this.getId.textProperty().addListener((observable,oldVal,newVal)->{
            submit.setDisable(!newVal.matches("^[a-zA-Z0-9]{6}$"));
            if(this.error.isVisible())
                this.error.setVisible(false);
        });
        this.getId.setOnAction(event -> {
            if(!this.submit.isDisable()){
                getIdCapacity();
            }
        });
    }

    /**
     * Submits the desire to return to the menu.
     */
    public void exit(){
        this.gui.scanInput("exit");
        clear();
    }

    /**
     * Clears the state of the component to a default state.
     */
    public void clear(){
        this.getId.clear();
        this.getCapacity.setValue(0);
        this.submit.setDisable(true);
    }

    /**
     * Retrieves the capacity from the sliders and combines it with the ID of the room.
     */
    public void getIdCapacity() {
        this.gui.scanInput(this.getId.getText() + " " + (int)this.getCapacity.getValue());
    }

    /**
     * Confirms the creation of the game based on the provided parameters.
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
