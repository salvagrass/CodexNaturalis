package it.polimi.ingsw.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class CreateController extends Controller {
    @FXML
    Button submit;
    @FXML
    TextField getId;
    @FXML
    Slider getCapacity;
    @FXML
    Label error;

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
    public void exit(){
        this.gui.scanInput("exit");
        clear();
    }

    public void clear(){
        this.getId.clear();
        this.getCapacity.setValue(0);
        this.submit.setDisable(true);
    }

    public void getIdCapacity() {
        this.gui.scanInput(this.getId.getText() + " " + (int)this.getCapacity.getValue());
    }

    @Override
    public void confirm(boolean confirmValue,String confirmMsg){
        if(!confirmValue){
            this.error.setText(confirmMsg);
            this.error.setVisible(true);
        }
    }
}
