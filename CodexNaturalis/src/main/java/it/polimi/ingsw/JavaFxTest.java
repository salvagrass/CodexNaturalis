package it.polimi.ingsw;

import it.polimi.ingsw.util.GameScene;
import it.polimi.ingsw.view.GUI;
import it.polimi.ingsw.view.controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;


public class JavaFxTest extends Application {
    public GUI gui;
    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage) {
        try {
            gui = new GUI(false, stage);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        for(GameScene g : GameScene.values()){
            FXMLLoader fxml = new FXMLLoader(getClass().getResource(g.path));
            Parent root;
            try {
                root = fxml.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Controller c = fxml.getController();
            c.setGui(this.gui);
            this.gui.addScene(g, new Scene(root, 800, 500), c);
        }
        this.gui.start();
        stage.setTitle("CODEX NATURALIS");
        stage.show();
    }

}
