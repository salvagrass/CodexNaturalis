package it.polimi.ingsw.view.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


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
    ExecutorService executor = Executors.newSingleThreadExecutor();

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
            Future<Void> exec = executor.submit(() -> {
                this.gui.tryConnection(this.address.getText());
                return null;
            });
            exec.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            this.error.setVisible(true);
        }
    }
}
