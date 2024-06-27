package it.polimi.ingsw.view.controller;

import it.polimi.ingsw.util.PawnColor;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ColorController extends Controller {
    @FXML
    private ToggleButton red, blue, green, yellow;
    @FXML
    private ToggleButton redBackPawn, blueBackPawn, yellowBackPawn, greenBackPawn;
    @FXML
    private ToggleGroup color;
    @FXML
    private Button submit;
    @FXML
    private Label error;
    @FXML
    private AnchorPane pane;

    /**
     * Initializes the component.
     */
    public void initialize(){
        this.color.selectedToggleProperty().addListener((observable,oldVal,newVal) -> {
            this.submit.setDisable(newVal == null);
            this.error.setVisible(false);
            if(newVal == null){
                Platform.runLater(()->this.pane.requestFocus());
            }
        });
        Platform.runLater(()->this.pane.requestFocus());
        this.red.disableProperty().addListener((observable, oldValue, newValue) -> bindColors(this.red, this.redBackPawn, newValue));
        this.blue.disableProperty().addListener((observable, oldValue, newValue) -> bindColors(this.blue, this.blueBackPawn, newValue));
        this.green.disableProperty().addListener((observable, oldValue, newValue) -> bindColors(this.green, this.greenBackPawn, newValue));
        this.yellow.disableProperty().addListener((observable, oldValue, newValue) -> bindColors(this.yellow, this.yellowBackPawn, newValue));
    }

    /**
     * Binds colors to UI elements.
     *
     * @param button The toggle button for color selection.
     * @param img The toggle button for image selection.
     * @param newValue The new value for button state.
     */
    private void bindColors(ToggleButton button, ToggleButton img, boolean newValue){
        this.submit.setDisable(button.equals(this.color.getSelectedToggle()) && newValue);
        img.setDisable(newValue);
    }

    /**
     * Binds radio buttons to an ObservableSet of PawnColor.
     *
     * @param colors The ObservableSet of PawnColor to bind to.
     */
    public void bind(ObservableSet<PawnColor> colors){
        bindRadioButtonDisableProperty(this.red, "RED", colors);
        bindRadioButtonDisableProperty(this.blue, "BLUE", colors);
        bindRadioButtonDisableProperty(this.green, "GREEN", colors);
        bindRadioButtonDisableProperty(this.yellow, "YELLOW", colors);
    }

    /**
     * Binds the disable property of a ToggleButton to an ObservableSet of PawnColor.
     *
     * @param toggle   The ToggleButton to bind.
     * @param colorId  The ID of the PawnColor to check.
     * @param colors   The ObservableSet of PawnColor to use for binding.
     */
    private void bindRadioButtonDisableProperty(ToggleButton toggle, String colorId, ObservableSet<PawnColor> colors) {
        BooleanBinding disableBinding = Bindings.createBooleanBinding(() -> !colors.contains(PawnColor.valueOf(colorId)), colors);
        toggle.disableProperty().bind(disableBinding);
    }

    /**
     * Submits the ID of the selected ToggleButton.
     */
    public void submit() {
        ToggleButton sel = (ToggleButton) color.getSelectedToggle();
        this.gui.scanInput(sel.getId());
    }

    /**
     * Confirms an action based on the provided parameters.
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
