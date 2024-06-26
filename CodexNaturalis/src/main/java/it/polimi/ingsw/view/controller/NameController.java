package it.polimi.ingsw.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class NameController extends Controller {
    @FXML
    Button submit;
    @FXML
    TextField getName;
    @FXML
    Label error;

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

    public void clear(){
        this.getName.clear();
        this.submit.setDisable(true);
    }

    public void submit() {
        this.gui.scanInput(this.getName.getText());
    }

    @Override
    public void confirm(boolean confirmValue,String confirmMsg){
        if(!confirmValue){
            this.error.setText(confirmMsg);
            this.error.setVisible(true);
            return;
        }
        clear();
    }
}
