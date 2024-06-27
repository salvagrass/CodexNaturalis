package it.polimi.ingsw;

import it.polimi.ingsw.util.GameScene;
import it.polimi.ingsw.util.ProjectLogger;
import it.polimi.ingsw.view.GUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class JavaFxTest extends Application {
    public GUI gui;
    public static void main(String[] args){
        ProjectLogger pj = new ProjectLogger("Main");
        pj.clearFile("logs/clientLog.txt");
        pj.clearFile("logs/commLog.txt");
        launch(args);
    }

    public void start(Stage stage) {
        Map<GameScene, FXMLLoader> roots = new HashMap<>();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        stage.setResizable(false);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        for(GameScene g : GameScene.values()){
            FXMLLoader fxml = new FXMLLoader(getClass().getResource(g.path));
            roots.put(g, fxml);
        }
        gui = new GUI(stage, roots);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/icons/OpenBook16.png"))));
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/icons/OpenBook32.png"))));
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/icons/OpenBook64.png"))));
        this.gui.start();
        stage.setTitle("CODEX NATURALIS");
        stage.show();
    }
}
