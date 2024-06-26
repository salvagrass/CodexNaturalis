package it.polimi.ingsw.view.controller;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Objects;

public class RulebookController extends Controller{
    @FXML
    private Pane back;
    @FXML
    private Button prevBtn,nextBtn;
    @FXML
    private ImageView prevImg,nextImg;
    private IntegerProperty idx;
    private final int minIdx = 1;
    private final int maxIdx = 6;

    public void initialize(){
        this.idx = new SimpleIntegerProperty();
        this.idx.addListener((obs, oldVal, newVal) -> {
            changeBackGround("/graphics/RB" + newVal + ".png");
            boolean condition = idx.get()==this.minIdx;
            this.prevBtn.setDisable(condition);
            this.prevBtn.setVisible(!condition);
            this.prevImg.setVisible(!condition);
            condition = idx.get()==this.maxIdx;
            this.nextBtn.setDisable(condition);
            this.nextBtn.setVisible(!condition);
            this.nextImg.setVisible(!condition);
        });
        this.idx.set(this.minIdx);
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
        if(this.idx.get() < this.maxIdx){
            this.idx.set(this.idx.get() + 1);
        }
    }

    @FXML
    private void prev(){
        if(this.idx.get() > this.minIdx){
            this.idx.set(this.idx.get() - 1);
        }
    }
}
