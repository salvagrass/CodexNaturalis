package it.polimi.ingsw.view.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;


public class StartController extends Controller {
    @FXML
    private TextField address;
    @FXML
    private Button connect;
    @FXML
    private Label error;
    @FXML
    private ToggleGroup connection;
    @FXML
    private AnchorPane back;

    public void initialize(){
        this.address.textProperty().addListener((obs, oldVal, newVal)-> {
            this.connect.setDisable(newVal.isEmpty() || this.connection.getSelectedToggle() == null);
            this.error.setVisible(false);
        });
        this.connection.selectedToggleProperty().addListener((obs, oldVal, newVal) -> this.connect.setDisable(newVal == null || this.address.getText().isEmpty()));
        Platform.runLater(()->this.back.requestFocus());
    }

    public void connect(){
        try {
            this.gui.connect(((RadioButton)this.connection.getSelectedToggle()).getText().equals("Socket"));
            this.gui.tryConnection(this.address.getText());
        } catch (Exception e) {
            this.error.setVisible(true);
        }
    }
}
