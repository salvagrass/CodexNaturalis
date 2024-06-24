package it.polimi.ingsw.view.controller;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.Objects;

public class RulebookController extends Controller{
    @FXML
    private Pane back;
    private IntegerProperty idx;

    public void initialize(){
        this.idx = new SimpleIntegerProperty();
        this.idx.addListener((obs, oldVal, newVal) -> changeBackGround("/graphics/RB" + newVal + ".png"));
        this.idx.set(1);
    }

    private void changeBackGround(String addr){
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(addr)));
        BackgroundImage img = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );
        this.back.setBackground(new Background(img));
    }

    @FXML
    private void next(){
        int maxIdx = 6;
        if(this.idx.get() < maxIdx){
            this.idx.set(this.idx.get() + 1);
        }
    }

    @FXML
    private void prev(){
        if(this.idx.get() > 0){
            this.idx.set(this.idx.get() - 1);
        }
    }
}
