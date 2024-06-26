package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.serverfunction.MsgCreator;
import it.polimi.ingsw.util.*;
import it.polimi.ingsw.view.controller.*;
import it.polimi.ingsw.view.coreview.BoardView;
import it.polimi.ingsw.view.coreview.CardView;
import it.polimi.ingsw.view.coreview.ItemView;
import it.polimi.ingsw.view.coreview.ObjectiveView;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class GUI extends UserView{
    private final Map<GameScene, Scene> scenes;
    private final Map<Scene, Controller> sceneToController;
    private final Stage stage;
    private Controller currController;
    private final StringProperty currPlayer;
    private final IntegerProperty capacity;
    private Phase futurePhase;
    public GUI(Stage stage, Map<GameScene, FXMLLoader> roots){
        super();
        this.colors = FXCollections.observableSet();
        fillColors();
        this.scenes = new HashMap<>();
        this.sceneToController = new HashMap<>();
        this.playersInLobby = FXCollections.observableSet();
        this.stage = stage;
        this.currPlayer = new SimpleStringProperty("");
        this.capacity = new SimpleIntegerProperty();
        this.turnOrder = FXCollections.observableArrayList();
        this.leaderboard = FXCollections.observableHashMap();
        this.chats = FXCollections.observableArrayList();
        load(roots);
        bind();
    }

    public void fillColors(){
        this.colors.addAll(Arrays.stream(PawnColor.values()).toList());
    }

    private void load(Map<GameScene, FXMLLoader> roots){
        Parent root;
        for(GameScene g : roots.keySet()){
            try{
                root = roots.get(g).load();
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
            Controller c = roots.get(g).getController();
            c.setGui(this);
            this.addScene(g, new Scene(root), c);
        }
    }

    private void bind(){
        WaitController wc = (WaitController) this.sceneToController.get(this.scenes.get(GameScene.WAITPLAYERS));
        wc.bind(this.capacity, (ObservableSet<String>) this.playersInLobby);
        ColorController cc = (ColorController) this.sceneToController.get(this.scenes.get(GameScene.COLOR));
        cc.bind((ObservableSet<PawnColor>) this.colors);
        GameController gc = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
        gc.bind(this.currPlayer, (ObservableMap<String, Integer>) this.leaderboard);
    }

    @Override
    public void setCurrPhase(Phase phase, ItemView... items) {
        super.setCurrPhase(phase, items);
        changePhase(phase);
    }

    @Override
    public void sendConfirm(boolean confirmValue,String confirmMsg){
        synchronized (this){
            Platform.runLater(()->this.currController.confirm(confirmValue, confirmMsg));
        }
    }

    @Override
    public void changePhase(Phase phase) {
        if(this.currPhase == Phase.ENDGAME){
            this.futurePhase = phase;
            return;
        }
        change(phase);
    }

    public void changePhase() {
        if(this.futurePhase != null){
            change(this.futurePhase);
        }
    }

    private void change(Phase phase){
        Scene scene;
        switch(phase){
            case Phase.MENU -> {
                scene = this.scenes.get(GameScene.MENU);
                this.currController = this.sceneToController.get(scene);
                Platform.runLater(()-> this.stage.setScene(scene));
            }
            case Phase.CREATE -> {
                scene = this.scenes.get(GameScene.CREATE);
                this.currController = this.sceneToController.get(scene);
                Platform.runLater(()-> this.stage.setScene(scene));
            }
            case Phase.JOIN -> {
                scene = this.scenes.get(GameScene.IDSCENE);
                this.currController = this.sceneToController.get(scene);
                Platform.runLater(()-> this.stage.setScene(scene));
            }
            case Phase.NAME -> {
                scene = this.scenes.get(GameScene.NAME);
                this.currController = this.sceneToController.get(scene);
                Platform.runLater(()-> this.stage.setScene(scene));
            }
            case Phase.WAITFORPLAYERS -> {
                scene = this.scenes.get(GameScene.WAITPLAYERS);
                this.currController = this.sceneToController.get(scene);
                Platform.runLater(()-> this.stage.setScene(scene));
            }
            case Phase.STARTCARD -> {
                scene = this.scenes.get(GameScene.CHOOSEITEM);
                this.currController = this.sceneToController.get(scene);
                ChooseItemController c = (ChooseItemController) this.currController;
                Platform.runLater(()-> {
                    c.showImgs(this.starts[0].getId(), this.starts[1].getId());
                    c.bind(phase);
                    this.stage.setScene(scene);
                });
            }
            case Phase.COLOR -> {
                scene = this.scenes.get(GameScene.COLOR);
                this.currController = this.sceneToController.get(scene);
                Platform.runLater(()-> this.stage.setScene(scene));
            }
            case Phase.OBJECTIVE -> {
                scene = this.scenes.get(GameScene.CHOOSEITEM);
                this.currController = this.sceneToController.get(scene);
                ChooseItemController c = (ChooseItemController) this.currController;
                Platform.runLater(()-> {
                    c.showImgs(this.scrtObjs[0].getId(), this.scrtObjs[1].getId());
                    c.bind(phase);
                    this.stage.setScene(scene);
                });
            }
            case Phase.GAME -> {
                scene = this.scenes.get(GameScene.GAME);
                this.currController = this.sceneToController.get(scene);
                Platform.runLater(()-> {
                    ((GameController)this.currController).gameStart(this.currPlayer,this.name, new HashMap<>(this.colorsChosen));
                    ((GameController)this.currController).switchBoard(this.turnOrder.indexOf(this.name));
                    ((GameController)this.currController).setSecretObj(this.modelView.getSecrObj());
                    this.stage.setScene(scene);
                    this.stage.setFullScreen(true);
                });
            }
            case Phase.CLIENTCRASHED -> {
                scene = this.scenes.get(GameScene.CRASHED);
                this.currController = this.sceneToController.get(scene);
                Platform.runLater(()->{
                    Stage s = new Stage();
                    s.initModality(Modality.APPLICATION_MODAL);
                    ((CrashedController)this.sceneToController.get(this.scenes.get(GameScene.CRASHED))).setStage(s);
                    ((CrashedController)this.sceneToController.get(this.scenes.get(GameScene.CRASHED))).setTitle("A client crashed");
                    s.setScene(scene);
                    s.showAndWait();
                });
            }
            case Phase.SERVERCRASHED -> {
                Scene crash = this.scenes.get(GameScene.CRASHED);
                scene = this.scenes.get(GameScene.START);
                this.currController = this.sceneToController.get(crash);
                Platform.runLater(()->{
                    Stage s = new Stage();
                    s.initModality(Modality.APPLICATION_MODAL);
                    ((CrashedController)this.sceneToController.get(this.scenes.get(GameScene.CRASHED))).setStage(s);
                    ((CrashedController)this.sceneToController.get(this.scenes.get(GameScene.CRASHED))).setTitle("Server crashed");
                    s.setScene(crash);
                    s.showAndWait();
                    this.stage.setScene(scene);
                });
            }
            case Phase.ENDGAME -> {
                scene = this.scenes.get(GameScene.ENDGAME);
                this.currController = this.sceneToController.get(scene);
                Platform.runLater(()-> {
                    ((EndGameController)this.currController).show(this.name, this.colorsChosen, this.positions, this.leaderboard);
                    this.stage.setScene(scene);
                    this.stage.setFullScreen(false);
                });
            }
            case Phase.EXIT -> System.exit(0);
        }
        if(this.currPhase == Phase.WAITFORPLAYERS){
            Platform.runLater(()-> this.playersInLobby.clear());
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
    public void createGroundView(CardView[] resCards, CardView[] goldCards, ObjectiveView[] objs){
        super.createGroundView(resCards, goldCards, objs);
        GameController c = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
        Platform.runLater(()->{
            c.updGroundView(0, resCards[0]);
            c.updGroundView(1, resCards[1]);
            c.updGroundView(2, resCards[2]);
            c.updGroundView(3, goldCards[0]);
            c.updGroundView(4, goldCards[1]);
            c.updGroundView(5, goldCards[2]);
            c.updGroundView(6, objs[0]);
            c.updGroundView(7, objs[1]);
        });
    }

    public void updGroundView(int idx,CardView card){
        super.updGroundView(idx, card);
        GameController c = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
        Platform.runLater(()-> c.updGroundView(idx, card));
    }

    @Override
    public void setHand(List<CardView> hand){
        super.setHand(hand);
        GameController c = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
        Platform.runLater(()->{
            c.updHandView(0, hand.get(0));
            c.updHandView(1, hand.get(1));
            c.updHandView(2, hand.get(2));
        });
    }

    @Override
    public void updHandView(CardView card){
        super.updHandView(card);
        GameController c = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
        Platform.runLater(()-> c.updHandView(this.idx, card));
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

    public void placeCard(int idx, int x, int y){
        this.client.placeCard(idx, x, y, this.modelView.getHand().get(idx).getFace());
        this.tmpIdx = idx;
    }

    public void drawCard(int idx){
        this.client.drawCard(idx);
    }

    public void flip(int idx){
        CardView card = this.modelView.getHand().get(idx);
        card.setFace(!card.getFace());
        GameController c = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
        Platform.runLater(()-> c.updHandView(idx, card));
    }

    @Override
    public void updBoardView(int turnIdx, CardView card, int x, int y, Map<Symbol,Integer> inventory){
        super.updBoardView(turnIdx, card, x, y, inventory);
        GameController c = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
        Platform.runLater(()->c.updBoardView(turnIdx, card, x, y, inventory));
    }

    @Override
    public void setCurrPlayer(String currPlayer) {
        synchronized (this.currPlayerLock) {
            this.currPlayer.set(currPlayer);
        }
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity.set(capacity);
        super.setCapacity(capacity);
    }

    @Override
    public void loadBoard(int idx, BoardView boardView){
        super.loadBoard(idx, boardView);
        Map<Coord, CardView> board = boardView.getPlacedCards();
        List<Coord> cardOrder = boardView.getCardOrder();
        for(Coord c : cardOrder){
            updBoardView(idx, board.get(c), c.x(), c.y(), boardView.getInventory());
        }
    }

    public void setIdx(){
        this.idx = this.tmpIdx;
    }

    public void setObj(int idx){
        this.modelView.setObj(this.scrtObjs[idx]);
    }

    @Override
    public void setNames(List<String> turnOrder){
        super.setNames(turnOrder);
        Platform.runLater(()->{
            GameController gc = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
            gc.setName(this.name,(ObservableList<String>) this.turnOrder,(ObservableList<Message>) chats);
        });
    }

    public void sendChatMsg(String text, String receiver) {
        this.client.sendChatMessage(MsgCreator.chatMsg(this.name, receiver, text));
    }
}
