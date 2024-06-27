package it.polimi.ingsw.view.controller;

import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for the Wait for PLayers scene.
 */
public class WaitController extends Controller{
    @FXML
    private VBox list;
    @FXML
    private Label ready;
    private int capacity;

    /**
     * Binds the capacity and players properties.
     * Updates the UI to display the list of players and the current player count.
     *
     * @param capacity The maximum capacity for players.
     * @param players  The set of players' names.
     */
    public void bind(IntegerProperty capacity, ObservableSet<String> players){
        players.addListener((SetChangeListener<String>) change -> {
            if (change.wasAdded()) {
                Label label = new Label(change.getElementAdded());
                this.list.getChildren().add(label);
            }
            if (change.wasRemoved()) {
                this.list.getChildren().removeIf(node -> ((Label) node).getText().equals(change.getElementRemoved()));
            }
            this.ready.setText(players.size() + "/" + this.capacity);
        });
        capacity.addListener((observable, oldVal, newVal)->this.capacity = (int) newVal);
    }
}
