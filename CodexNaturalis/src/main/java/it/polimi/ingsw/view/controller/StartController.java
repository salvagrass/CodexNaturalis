package it.polimi.ingsw.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class StartController extends Controller {
    @FXML
    private TextField address;
    @FXML
    private Button connect;
    @FXML
    private Label error;

    public void initialize(){
        this.address.textProperty().addListener((obs, oldVal, newVal)-> {
            this.connect.setDisable(newVal.isEmpty());
            this.error.setVisible(false);
        });
    }

    public void connect(){
        try {
            this.gui.tryConnection(address.getText());
        } catch (Exception e) {
            this.error.setVisible(true);
        }
    }
}
