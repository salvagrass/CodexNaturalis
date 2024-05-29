package it.polimi.ingsw.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class IdController extends Controller{
    @FXML
    private Button submit;
    @FXML
    private TextField getId;
    public void initialize(){
        this.getId.textProperty().addListener((observable, oldVal, newVal) -> this.submit.setDisable(!newVal.matches("^[a-zA-Z0-9]{6}$")));
    }
    public void submit() {
        this.gui.scanInput(this.getId.getText());
    }
}
