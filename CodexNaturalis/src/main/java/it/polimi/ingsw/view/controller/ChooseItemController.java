package it.polimi.ingsw.view.controller;

import it.polimi.ingsw.util.Phase;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class ChooseItemController extends Controller {
    @FXML
    private ImageView img0, img1;
    @FXML
    private Button submit;
    @FXML
    private ToggleGroup items;
    @FXML
    private Label title;
    private Phase phase;

    public void bind(Phase phase){
        this.phase = phase;
        if(phase == Phase.STARTCARD){
            this.title.setText("Choose the orientation");
        }else{
            this.title.setText("Choose objective");
        }
    }

    public void initialize(){
        this.items.selectedToggleProperty().addListener((observable,oldVal,newVal)-> {
            this.submit.setDisable(newVal == null);
            if(newVal == null){
                Platform.runLater(()->this.title.requestFocus());
            }
        });
        Platform.runLater(()->this.title.requestFocus());
    }

    public void showImgs(String source1, String source2){
        this.img0.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(("/graphics/" + source1 + ".png")))));
        this.img1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(("/graphics/" + source2 + ".png")))));
    }

    public void submit() {
        ToggleButton sel = (ToggleButton) this.items.getSelectedToggle();
        int idx = sel.getId().equals("b0") ? 0 : 1;
        if(this.phase == Phase.OBJECTIVE){
            this.gui.setObj(idx);
        }
        this.gui.scanInput(idx + "");
        this.submit.setDisable(true);
        Platform.runLater(()->this.title.requestFocus());
    }
}
