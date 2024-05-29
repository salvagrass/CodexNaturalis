package it.polimi.ingsw.view.controller;

import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WaitController extends Controller{
    @FXML
    private VBox list;
    @FXML
    private Label ready;
    public void bind(int capacity, ObservableSet<String> players){
        players.addListener((SetChangeListener<String>) change -> {
            if (change.wasAdded()) {
                Label label = new Label(change.getElementAdded());
                this.list.getChildren().add(label);
            }
            if (change.wasRemoved()) {
                this.list.getChildren().removeIf(node -> ((Label) node).getText().equals(change.getElementRemoved()));
            }
            this.ready.setText(players.size() + "/" + capacity);
        });
    }

}
