package it.polimi.ingsw.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class IdController extends Controller{
    @FXML
    private Button submit;
    @FXML
    private TextField getId;
    @FXML
    private Label error;
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
    public void submit() {
        this.gui.scanInput(this.getId.getText());
    }

    public void exit() {
        this.gui.scanInput("exit");
        clear();
    }

    public void clear(){
        this.getId.clear();
        this.submit.setDisable(true);
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
