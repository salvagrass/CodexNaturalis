package it.polimi.ingsw;

import it.polimi.ingsw.util.GameScene;
import it.polimi.ingsw.view.GUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;


public class JavaFxTest extends Application {
    public GUI gui;
    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage) {
        Map<GameScene, FXMLLoader> roots = new HashMap<>();
        stage.setResizable(false);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        for(GameScene g : GameScene.values()){
            FXMLLoader fxml = new FXMLLoader(getClass().getResource(g.path));
            roots.put(g, fxml);
        }
        try {
            gui = new GUI(true, stage, roots);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        this.gui.start();
        stage.setTitle("CODEX NATURALIS");
        stage.show();
    }

}
