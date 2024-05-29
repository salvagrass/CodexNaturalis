package it.polimi.ingsw.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class ChooseItemController extends Controller {
    @FXML
    ImageView img0, img1;
    @FXML
    Button submit;
    @FXML
    ToggleGroup items;

    public void initialize(){
        this.items.selectedToggleProperty().addListener((observable,oldVal,newVal)-> this.submit.setDisable(newVal == null));
    }

    public void showImgs(String source1, String source2){
        this.img0.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(("/graphics/" + source1 + ".png")))));
        this.img1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(("/graphics/" + source2 + ".png")))));
    }

    public void submit() {
        ToggleButton sel = (ToggleButton) this.items.getSelectedToggle();
        this.gui.scanInput(sel.getId().equals("b0") ? "0" : "1");
        this.submit.setDisable(true);
    }
}
