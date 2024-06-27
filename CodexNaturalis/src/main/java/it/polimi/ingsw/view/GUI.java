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
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class GUI extends UserView{
    private final Map<GameScene, Scene> scenes;
    private final Map<Scene, Controller> sceneToController;
    private final Stage stage;
    private Controller currController;
    private StringProperty currPlayer;
    private IntegerProperty capacity;
    private Phase futurePhase;

    /**
     * Constructor of the class
     *
     *  @param stage The JavaFX stage for the GUI.
     *  @param roots A map containing GameScene keys and corresponding FXMLLoader values.
     */
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

    /**
     * Fills the colors list by adding all available pawn colors from the PawnColor enum.
     */
    public void fillColors(){
        this.colors.addAll(Arrays.stream(PawnColor.values()).toList());
    }

    /**
     * Loads the specified scenes from the given map of FXMLs.
     *
     * @param roots A map containing GameScene keys and corresponding FXMLLoader values.
     */
    private void load(Map<GameScene, FXMLLoader> roots){
        Parent root;
        for(GameScene g : roots.keySet()){
            try{
                root = roots.get(g).load();
            }catch (IOException e) {
                new ProjectLogger(getClass().getSimpleName()).
                        log("logs/clientLog.txt",Level.SEVERE,"Can't load scenes",e);
                throw new RuntimeException(e);
            }
            Controller c = roots.get(g).getController();
            c.setGui(this);
            this.addScene(g, new Scene(root), c);
        }
    }

    /**
     * Binds relevant properties and data to the various corresponding controllers.
     */
    private void bind(){
        WaitController wc = (WaitController) this.sceneToController.get(this.scenes.get(GameScene.WAITPLAYERS));
        wc.bind(this.capacity, (ObservableSet<String>) this.playersInLobby);
        ColorController cc = (ColorController) this.sceneToController.get(this.scenes.get(GameScene.COLOR));
        cc.bind((ObservableSet<PawnColor>) this.colors);
        GameController gc = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
        gc.bind(this.currPlayer, (ObservableMap<String, Integer>) this.leaderboard);
    }

    /**
     * Sets the current phase and changes to it.
     *
     * @param phase The new phase to set.
     * @param items Optional item views associated with the phase.
     */
    @Override
    public void setCurrPhase(Phase phase, ItemView... items) {
        super.setCurrPhase(phase, items);
        changePhase(phase);
    }

    /**
     * Sends a confirmation message.
     *
     * @param confirmValue The confirmation value (true or false).
     * @param confirmMsg   The confirmation message to display.
     */
    @Override
    public void sendConfirm(boolean confirmValue,String confirmMsg){
        synchronized (this){
            Platform.runLater(()->this.currController.confirm(confirmValue, confirmMsg));
        }
    }

    /**
     * Changes the current phase.
     * If the current phase is the ENDGAME one, sets the future phase as the one passed to it.
     *
     * @param phase The new phase to set.
     */
    @Override
    public void changePhase(Phase phase) {
        if(this.currPhase == Phase.ENDGAME){
            this.futurePhase = phase;
            return;
        }
        change(phase);
    }

    /**
     * Changes the current phase.
     */
    public void changePhase() {
        if(this.futurePhase != null){
            change(this.futurePhase);
        }
    }

    /**
     * Receives the current phase of the game and sets the stage up with its controller and scene accordingly.
     *
     * @param phase The new phase to set.
     */
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
                Controller c = this.sceneToController.get(scene);
                this.currController = c;
                Platform.runLater(()-> {
                    ((CreateController)c).clear();
                    this.stage.setScene(scene);
                });
            }
            case Phase.JOIN -> {
                scene = this.scenes.get(GameScene.IDSCENE);
                Controller c = this.sceneToController.get(scene);
                this.currController = c;
                Platform.runLater(()-> {
                    ((IdController)c).clear();
                    this.stage.setScene(scene);
                });
            }
            case Phase.NAME -> {
                scene = this.scenes.get(GameScene.NAME);
                Controller c = this.sceneToController.get(scene);
                this.currController = c;
                Platform.runLater(()-> {
                    ((NameController)c).clear();
                    this.stage.setScene(scene);
                });
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
                    setNewGame();
                    Stage s = prepareCrashedScene();
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
                    setNewGame();
                    Stage s = prepareCrashedScene();
                    ((CrashedController)this.sceneToController.get(this.scenes.get(GameScene.CRASHED))).setTitle("Server crashed");
                    s.setScene(crash);
                    s.showAndWait();
                    this.stage.setScene(scene);
                });
            }
            case Phase.EXCEPTIONOCCURED -> {
                scene = this.scenes.get(GameScene.CRASHED);
                this.currController = this.sceneToController.get(scene);
                Platform.runLater(()->{
                    setNewGame();
                    Stage s = prepareCrashedScene();
                    ((CrashedController)this.sceneToController.get(this.scenes.get(GameScene.CRASHED))).setTitle("An exception occurred");
                    s.setScene(scene);
                    s.showAndWait();
                });
            }
            case Phase.ENDGAME -> {
                scene = this.scenes.get(GameScene.ENDGAME);
                this.currController = this.sceneToController.get(scene);
                Platform.runLater(()-> {
                    setNewGame();
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

    /**
     * Handles players joining the lobby
     *
     * @param players The set of players who joined.
     */
    @Override
    public void playerJoined(Set<String> players){
        Platform.runLater(()-> {
            synchronized (this.lobbyLock) {
                this.playersInLobby.addAll(players);
            }
        });
    }

    /**
     * Creates the ground view with specified cards and objectives.
     *
     * @param resCards An array of resource cards.
     * @param goldCards An array of gold cards.
     * @param objs An array of objective cards.
     */
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

    /**
     * Updates the ground view with the specified card at the given index.
     *
     * @param idx The index representing the type of item being updated
     * @param card The Card being placed.
     */
    public void updGroundView(int idx,CardView card){
        super.updGroundView(idx, card);
        GameController c = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
        Platform.runLater(()-> c.updGroundView(idx, card));
    }

    /**
     * Sets the player's hand with the specified list of CardViews.
     *
     * @param hand The list of CardViews representing the player's hand.
     */
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

    /**
     * Updates the player's hand view with the specified card.
     *
     * @param card The CardView to update in the hand.
     */
    @Override
    public void updHandView(CardView card){
        super.updHandView(card);
        GameController c = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
        Platform.runLater(()-> c.updHandView(this.idx, card));
    }

    /**
     * Updates the available colors of the pawns.
     *
     * @param newColors A set of new pawn colors.
     */
    @Override
    public void updColors(Set<PawnColor> newColors){
        Platform.runLater(()-> super.updColors(newColors));
    }

    /**
     * Adds a game scene along with its associated scene and controller.
     *
     * @param gameScene The game scene to add.
     * @param scene The JavaFX scene associated with the game scene.
     * @param c The controller responsible for managing the game scene.
     */
    public void addScene(GameScene gameScene, Scene scene, Controller c){
        this.scenes.put(gameScene, scene);
        this.sceneToController.put(scene, c);
    }

    /**
     * Sets the stage scene to the initial game scene.
     */
    public void start(){
        stage.setScene(this.scenes.get(GameScene.START));
    }

    /**
     * Places a card on the game board at the specified position.
     *
     * @param idx The index of the card in the hand.
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     */
    public void placeCard(int idx, int x, int y){
        this.client.placeCard(idx, x, y, this.modelView.getHand().get(idx).getFace());
        this.tmpIdx = idx;
    }

    /**
     * Draws a card from the deck.
     *
     * @param idx The index of the card to draw.
     */
    public void drawCard(int idx){
        this.client.drawCard(idx);
    }

    /**
     * Flips the card at the specified index in the hand.
     *
     * @param idx The index of the card to flip.
     */
    public void flip(int idx){
        CardView card = this.modelView.getHand().get(idx);
        card.setFace(!card.getFace());
        GameController c = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
        Platform.runLater(()-> c.updHandView(idx, card));
    }

    /**
     * Updates the board view with the specified parameters.
     *
     * @param turnIdx   The turn index.
     * @param card      The card to place.
     * @param x         The x-coordinate.
     * @param y         The y-coordinate.
     * @param inventory The inventory to be updated.
     */
    @Override
    public void updBoardView(int turnIdx, CardView card, int x, int y, Map<Symbol,Integer> inventory){
        super.updBoardView(turnIdx, card, x, y, inventory);
        GameController c = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
        Platform.runLater(()->c.updBoardView(turnIdx, card, x, y, inventory));
    }

    /**
     * Sets the current player.
     *
     * @param currPlayer The name of the current player.
     */
    @Override
    public void setCurrPlayer(String currPlayer) {
        synchronized (this.currPlayerLock) {
            this.currPlayer.set(currPlayer);
        }
    }

    /**
     * Sets the capacity of the lobby.
     *
     * @param capacity The maximum number of players.
     */
    @Override
    public void setCapacity(int capacity) {
        this.capacity.set(capacity);
        super.setCapacity(capacity);
    }

    /**
     * Loads the board view with the specified parameters.
     *
     * @param idx        The index of the board.
     * @param boardView  The board view containing placed cards, card order, and inventory.
     */
    @Override
    public void loadBoard(int idx, BoardView boardView){
        super.loadBoard(idx, boardView);
        Map<Coord, CardView> board = boardView.getPlacedCards();
        List<Coord> cardOrder = boardView.getCardOrder();
        for(Coord c : cardOrder){
            updBoardView(idx, board.get(c), c.x(), c.y(), boardView.getInventory());
        }
    }

    /**
     * Sets an index.
     */
    public void setIdx(){
        this.idx = this.tmpIdx;
    }

    /**
     * Sets a certain objective.
     *
     * @param idx Index of the objective.
     */
    public void setObj(int idx){
        this.modelView.setObj(this.scrtObjs[idx]);
    }

    /**
     * Sets the player names and updates the game controller.
     *
     * @param turnOrder The list of player names in turn order.
     */
    @Override
    public void setNames(List<String> turnOrder){
        super.setNames(turnOrder);
        Platform.runLater(()->{
            GameController gc = (GameController) this.sceneToController.get(this.scenes.get(GameScene.GAME));
            gc.setName(this.name,(ObservableList<String>) this.turnOrder,(ObservableList<Message>) chats);
        });
    }

    /**
     * Sends a chat message to the specified receiver.
     *
     * @param text     The message text to send.
     * @param receiver The recipient of the message.
     */
    public void sendChatMsg(String text, String receiver) {
        this.client.sendChatMessage(MsgCreator.chatMsg(this.name, receiver, text));
    }

    /**
     * Resets the objects for another game and reloads the game scene.
     */
    private void setNewGame(){
        //RESETTING THE OBJECTS FOR ANOTHER GAME
        super.cleanView();
        this.currPlayer = new SimpleStringProperty("");
        this.capacity = new SimpleIntegerProperty();
        this.colors = FXCollections.observableSet();
        this.colors.addAll(Arrays.stream(PawnColor.values()).toList());

        //RELOADING ANOTHER GAME SCENE
        FXMLLoader loader = new FXMLLoader(getClass().getResource(GameScene.GAME.path));
        try {
            Parent parent = loader.load();
            GameController c = loader.getController();
            c.setGui(this);
            c.bind(this.currPlayer, (ObservableMap<String, Integer>) this.leaderboard);
            ColorController cc = (ColorController) this.sceneToController.get(this.scenes.get(GameScene.COLOR));
            cc.bind((ObservableSet<PawnColor>) this.colors);
            addScene(GameScene.GAME,new Scene(parent,500,800),c);
        } catch (IOException e) {
            new ProjectLogger(getClass().getSimpleName()).
                    log("logs/clientLogs.txt",Level.SEVERE,"Can't load scenes",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new stage in case a crash happens
     * @return the created stage
     */
    private Stage prepareCrashedScene(){
        Stage s = new Stage();
        s.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/icons/OpenBook16.png"))));
        s.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/icons/OpenBook32.png"))));
        s.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/icons/OpenBook64.png"))));
        s.initModality(Modality.APPLICATION_MODAL);
        ((CrashedController)this.sceneToController.get(this.scenes.get(GameScene.CRASHED))).setStage(s);
        return s;
    }
}
