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

    private void bindColors(ToggleButton button, ToggleButton img, boolean newValue){
        this.submit.setDisable(button.equals(this.color.getSelectedToggle()) && newValue);
        img.setDisable(newValue);
    }

    public void bind(ObservableSet<PawnColor> colors){
        bindRadioButtonDisableProperty(this.red, "RED", colors);
        bindRadioButtonDisableProperty(this.blue, "BLUE", colors);
        bindRadioButtonDisableProperty(this.green, "GREEN", colors);
        bindRadioButtonDisableProperty(this.yellow, "YELLOW", colors);
    }

    private void bindRadioButtonDisableProperty(ToggleButton toggle, String colorId, ObservableSet<PawnColor> colors) {
        BooleanBinding disableBinding = Bindings.createBooleanBinding(() -> !colors.contains(PawnColor.valueOf(colorId)), colors);
        toggle.disableProperty().bind(disableBinding);
    }
    public void submit() {
        ToggleButton sel = (ToggleButton) color.getSelectedToggle();
        this.gui.scanInput(sel.getId());
    }

    @Override
    public void confirm(boolean confirmValue,String confirmMsg){
        if(!confirmValue){
            this.error.setText(confirmMsg);
            this.error.setVisible(true);
        }
    }
}
