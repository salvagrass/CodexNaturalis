package it.polimi.ingsw.view;

import it.polimi.ingsw.util.GameScene;
import it.polimi.ingsw.util.PawnColor;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.view.controller.ChooseItemController;
import it.polimi.ingsw.view.controller.ColorController;
import it.polimi.ingsw.view.controller.Controller;
import it.polimi.ingsw.view.controller.WaitController;
import it.polimi.ingsw.view.coreview.ItemView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GUI extends UserView{
    private final Map<GameScene, Scene> scenes;
    private final Map<Scene, Controller> sceneToController;
    private final Stage stage;
    private Controller currController;
    public GUI(boolean type, Stage stage) throws RemoteException {
        super(type);
        this.colors = FXCollections.observableSet();
        this.colors.addAll(Arrays.stream(PawnColor.values()).toList());
        this.scenes = new HashMap<>();
        this.sceneToController = new HashMap<>();
        this.playersInLobby = FXCollections.observableSet();
        this.stage = stage;
    }

    @Override
    public void setCurrPhase(Phase phase, ItemView... items) {
        super.setCurrPhase(phase, items);
        changePhase(phase);
    }

    @Override
    public void sendConfirm(boolean confirmValue,String confirmMsg){
        this.currController.confirm(confirmValue, confirmMsg);
    }

    @Override
    public void changePhase(Phase phase) {
        Scene scene;
        switch(phase){
            case Phase.MENU -> {
                scene = this.scenes.get(GameScene.MENU);
                Platform.runLater(()-> this.stage.setScene(scene));
                this.currController = this.sceneToController.get(scene);
            }
            case Phase.CREATE -> {
                scene = this.scenes.get(GameScene.CREATE);
                Platform.runLater(()-> this.stage.setScene(scene));
                this.currController = this.sceneToController.get(scene);
            }
            case Phase.JOIN -> {
                scene = this.scenes.get(GameScene.IDSCENE);
                Platform.runLater(()-> this.stage.setScene(scene));
                this.currController = this.sceneToController.get(scene);
            }
            case Phase.NAME -> {
                scene = this.scenes.get(GameScene.NAME);
                Platform.runLater(()-> this.stage.setScene(scene));
                this.currController = this.sceneToController.get(scene);
            }
            case Phase.WAITFORPLAYERS -> {
                scene = this.scenes.get(GameScene.WAITPLAYERS);
                this.currController = this.sceneToController.get(scene);
                WaitController c = (WaitController) this.currController;
                c.bind(this.capacity, (ObservableSet<String>) this.playersInLobby);
                Platform.runLater(()-> this.stage.setScene(scene));
            }
            case Phase.STARTCARD -> {
                scene = this.scenes.get(GameScene.CHOOSEITEM);
                this.currController = this.sceneToController.get(scene);
                ChooseItemController c = (ChooseItemController) this.currController;
                c.showImgs(this.starts[0].getId(), this.starts[1].getId());
                Platform.runLater(()-> this.stage.setScene(scene));
            }
            case Phase.COLOR -> {
                scene = this.scenes.get(GameScene.COLOR);
                this.currController = this.sceneToController.get(scene);
                ColorController c = (ColorController) this.currController;
                c.bind((ObservableSet<PawnColor>) this.colors);
                Platform.runLater(()-> this.stage.setScene(scene));
            }
            case Phase.OBJECTIVE -> {
                scene = this.scenes.get(GameScene.CHOOSEITEM);
                this.currController = this.sceneToController.get(scene);
                ChooseItemController c = (ChooseItemController) this.currController;
                c.showImgs(this.scrtObjs[0].getId(), this.scrtObjs[1].getId());
                Platform.runLater(()-> this.stage.setScene(scene));
            }
            case Phase.GAME -> {
                scene = this.scenes.get(GameScene.GAME);
                Platform.runLater(()-> this.stage.setScene(scene));
                this.currController = this.sceneToController.get(scene);
            }
            case Phase.ENDGAME -> {}
            case Phase.EXIT -> System.exit(0);
        }
        if(this.currPhase == Phase.WAITFORPLAYERS){
            Platform.runLater(()->{
                this.playersInLobby.clear();
                this.playersInLobby = FXCollections.observableSet();
            });
        }
        this.currPhase = phase;
    }

    @Override
    public void playerJoined(Set<String> players){
        Platform.runLater(()-> {
            synchronized (this.playersInLobby) {
                this.playersInLobby.addAll(players);
            }
        });
    }

    @Override
    public void updColors(Set<PawnColor> newColors){
        Platform.runLater(()-> super.updColors(newColors));
    }

    public void addScene(GameScene gameScene, Scene scene, Controller c){
        this.scenes.put(gameScene, scene);
        this.sceneToController.put(scene, c);
    }

    public void start(){
        stage.setScene(this.scenes.get(GameScene.START));
    }
}
