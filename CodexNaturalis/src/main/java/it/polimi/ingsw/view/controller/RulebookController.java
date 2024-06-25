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
    private final int maxIdx = 6;
    private final int minIdx = 1;

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
        if(this.idx.get() < maxIdx){
            this.idx.set(this.idx.get() + 1);
        }else{
            this.idx.set(this.minIdx);
        }
    }

    @FXML
    private void prev(){
        if(this.idx.get() > minIdx){
            this.idx.set(this.maxIdx);
        }else{
            this.idx.set(maxIdx);
        }
    }
}
