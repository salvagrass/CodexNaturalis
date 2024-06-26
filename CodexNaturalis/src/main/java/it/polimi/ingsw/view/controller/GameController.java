package it.polimi.ingsw.view.controller;

import it.polimi.ingsw.util.Message;
import it.polimi.ingsw.util.PawnColor;
import it.polimi.ingsw.util.Symbol;
import it.polimi.ingsw.view.controller.sceneutil.BoardLogic;
import it.polimi.ingsw.view.controller.sceneutil.ChatLogic;
import it.polimi.ingsw.view.controller.sceneutil.DragLogic;
import it.polimi.ingsw.view.coreview.CardView;
import it.polimi.ingsw.view.coreview.ItemView;
import it.polimi.ingsw.view.coreview.ObjectiveView;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;

import java.util.*;

public class GameController extends Controller{
    @FXML
    private ImageView resDeck, goldDeck, res1, res2, gold1, gold2;
    @FXML
    private ImageView scrtObj, obj0, obj1;
    @FXML
    private ImageView hand0, hand1, hand2;
    @FXML
    private ScrollPane scroll;
    @FXML
    private StackPane field, stack;
    @FXML
    private Label name, boardView;
    @FXML
    private VBox leaderboard;
    private List<BoardLogic> boards;
    private int myIdx;
    private int placingIdx;
    private List<String> names;
    private String currPlayer;
    private IntegerProperty currIdx;
    private DragLogic dragLogic;
    private ChatLogic chat;
    private boolean dragStart;

    private double scaleValue = 1.0;
    private final double scaleIncrement = 0.1;
    private Map<String, PawnColor> colors;

    public void initialize(){
        this.resDeck.imageProperty().addListener((observable, oldVal, newVal) -> this.resDeck.setOnMouseClicked(newVal == null ? null : this.resDeck.getOnMouseClicked()));
        this.res1.imageProperty().addListener((observable, oldVal, newVal) -> this.res1.setOnMouseClicked(newVal == null ? null : this.resDeck.getOnMouseClicked()));
        this.res2.imageProperty().addListener((observable, oldVal, newVal) -> this.res2.setOnMouseClicked(newVal == null ? null : this.resDeck.getOnMouseClicked()));
        this.goldDeck.imageProperty().addListener((observable, oldVal, newVal) -> this.goldDeck.setOnMouseClicked(newVal == null ? null : this.resDeck.getOnMouseClicked()));
        this.gold1.imageProperty().addListener((observable, oldVal, newVal) -> this.gold1.setOnMouseClicked(newVal == null ? null : this.resDeck.getOnMouseClicked()));
        this.gold2.imageProperty().addListener((observable, oldVal, newVal) -> this.gold2.setOnMouseClicked(newVal == null ? null : this.resDeck.getOnMouseClicked()));
        this.boards = new LinkedList<>();
        this.currIdx = new SimpleIntegerProperty();
        this.dragLogic = new DragLogic(this.scroll);
        this.names = new ArrayList<>(4);
        this.dragStart = false;
        this.colors = new HashMap<>();

        this.scroll.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                if (event.getDeltaY() > 0) {
                    this.scaleValue += this.scaleIncrement;
                } else if (event.getDeltaY() < 0) {
                    this.scaleValue -= this.scaleIncrement;
                    if (this.scaleValue < this.scaleIncrement) {
                        this.scaleValue = this.scaleIncrement;
                    }
                }
                Scale scale = new Scale(this.scaleValue, this.scaleValue);
                this.field.getTransforms().clear();
                this.field.getTransforms().add(scale);
                event.consume();
            }
        });

        this.dragLogic.makeDraggable(this.hand0, this::addCard);
        this.dragLogic.makeDraggable(this.hand1, this::addCard);
        this.dragLogic.makeDraggable(this.hand2, this::addCard);
    }

    @Override
    public void confirm(boolean confirmValue,String confirmMsg){
        if(confirmValue){
            if(confirmMsg.equals("Successful placement")){
                this.gui.setIdx();
            }
        }else{
            this.boards.get(this.myIdx).clearLastPlaced();
            switch(this.placingIdx){
                case 0 -> this.hand0.setVisible(true);
                case 1 -> this.hand1.setVisible(true);
                case 2 -> this.hand2.setVisible(true);
            }
        }
        this.boards.get(this.myIdx).setLastPlaced(null);
    }

    public void bind(StringProperty currPlayer, ObservableMap<String, Integer> points){
        currPlayer.addListener((observable, oldVal, newVal) -> this.currPlayer = newVal);
        points.addListener((MapChangeListener<String, Integer>) change -> Platform.runLater(()->{
            if (change.wasAdded()) {
                for(Node node : this.leaderboard.getChildren()){
                    if(((Label)node).getText().matches(change.getKey() + ": \\d*")){
                        ((Label)node).setText(change.getKey() + ": " + change.getValueAdded());
                        node.setStyle("-fx-text-fill: black; -fx-font-family: 'BlackChancery';");
                    }
                }
            }
        }));
    }

    public void updHandView(int idx, CardView card){
        switch(idx){
            case 0 -> {
                this.hand0.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/" + card.getId() + ".png"))));
                this.hand0.setVisible(true);
            }
            case 1 -> {
                this.hand1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/" + card.getId() + ".png"))));
                this.hand1.setVisible(true);
            }
            case 2 -> {
                this.hand2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/" + card.getId() + ".png"))));
                this.hand2.setVisible(true);
            }
        }
    }

    public void updGroundView(int idx, ItemView item){
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/" + item.getId() + ".png")));
        switch(idx){
            case 0 -> this.resDeck.setImage(img);
            case 1 -> this.res1.setImage(img);
            case 2 -> this.res2.setImage(img);
            case 3 -> this.goldDeck.setImage(img);
            case 4 -> this.gold1.setImage(img);
            case 5 -> this.gold2.setImage(img);
            case 6 -> this.obj0.setImage(img);
            case 7 -> this.obj1.setImage(img);
        }
    }

    public void updBoardView(int turnIdx, CardView card, int x, int y, Map<Symbol,Integer> inventory){
        updInventory(turnIdx, inventory);
        if(turnIdx!=myIdx || !dragStart) {
            this.boards.get(turnIdx).addCard(card, x , y);
        }
    }

    private void updInventory(int turnIdx, Map<Symbol, Integer> inventory) {
        this.boards.get(turnIdx).updInventory(turnIdx, inventory);
    }

    public void draw(MouseEvent mouseEvent) {
        if(!this.currPlayer.equals(this.names.get(this.myIdx))){
            return;
        }
        int idx = switch(((ImageView)mouseEvent.getSource()).getId()){
            case "resDeck" -> 0;
            case "res1" -> 1;
            case "res2" -> 2;
            case "goldDeck" -> 3;
            case "gold1" -> 4;
            case "gold2" -> 5;
            default -> -1;
        };
        this.gui.drawCard(idx);
    }

    public void flip(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
            switch(((ImageView) mouseEvent.getSource()).getId()){
                case "hand0" -> this.gui.flip(0);
                case "hand1" -> this.gui.flip(1);
                case "hand2" -> this.gui.flip(2);
            }
        }
    }

    private void addCard(ImageView card, VBox v){
        int i = GridPane.getRowIndex(v);
        int j = GridPane.getColumnIndex(v);
        BoardLogic bl = this.boards.stream().filter(x -> x.getGridPane().getChildren().contains(v)).findFirst().orElse(null);
        if(card.equals(this.hand0)){
            this.placingIdx = 0;
        }else if(card.equals(this.hand1)){
            this.placingIdx = 1;
        }else if(card.equals(this.hand2)){
            this.placingIdx = 2;
        }
        this.gui.placeCard(this.placingIdx, i - Objects.requireNonNull(bl).getXStart(), j - bl.getYStart());
        bl.placeCard(i, j, card.getImage());
    }

    private void scrollToCenter(ScrollPane scrollPane, GridPane gridPane, int x, int y) {
        double cellWidth = gridPane.getWidth() / gridPane.getColumnCount();
        double cellHeight = gridPane.getHeight() / gridPane.getRowCount();

        double targetX = y * cellWidth + cellWidth / 2;
        double targetY = x * cellHeight + cellHeight / 2;

        double scrollX = targetX - scrollPane.getViewportBounds().getWidth() / 2;
        double scrollY = targetY - scrollPane.getViewportBounds().getHeight() / 2;

        scrollPane.setHvalue(scrollX / (gridPane.getWidth() - scrollPane.getViewportBounds().getWidth()));
        scrollPane.setVvalue(scrollY / (gridPane.getHeight() - scrollPane.getViewportBounds().getHeight()));
    }

    public void sendChatMsg(String msg, String receiver){
        this.gui.sendChatMsg(msg, receiver);
    }

    public void gameStart(StringProperty currPlayer, String name, Map<String, PawnColor> colors){
        this.dragStart = true;
        this.colors = colors;
        this.dragLogic.setDraggable((this.myIdx == this.currIdx.get()) && currPlayer.get().equals(name));
        this.currIdx.addListener((observable, oldVal, newVal) -> this.dragLogic.setDraggable((this.myIdx == (int)newVal) && this.currPlayer.equals(name)));
        currPlayer.addListener((observable, oldVal, newVal) -> {
            this.dragLogic.setDraggable((this.myIdx == this.currIdx.get()) && newVal.equals(name));
            this.leaderboard.lookup("#label"+newVal).setStyle("-fx-text-fill: " + this.colors.get(newVal).color + "; -fx-font-family: 'BlackChancery'; -fx-effect: dropshadow(gaussian, grey, 8, 0.4, 0, 0)");
            this.leaderboard.lookup("#label"+oldVal).setStyle("-fx-text-fill: black; -fx-font-family: 'BlackChancery';");
        });
    }

    public void switchBoard(int idx){
        this.boards.get(this.currIdx.get()).setVisible(false);
        this.currIdx.set(idx);
        this.boardView.setText(this.names.get(idx));
        this.boards.get(this.currIdx.get()).setVisible(true);
        Platform.runLater(() -> {
            this.boards.get(this.currIdx.get()).getGridPane().requestLayout();
            BoardLogic bl = this.boards.get(this.currIdx.get());
            scrollToCenter(this.scroll, bl.getGridPane(), bl.getXStart(), bl.getYStart());
        });
    }

    public void setSecretObj(ObjectiveView obj){
        this.scrtObj.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/" + obj.getId() + ".png"))));
    }

    public void setName(String name, ObservableList<String> turnOrder,ObservableList<Message> chats){
        this.chat = new ChatLogic(this, chats, turnOrder);
        this.name.setText(name);
        this.addBoards(turnOrder);
        chat.setName(name);
        this.myIdx = turnOrder.indexOf(name);
        this.boards.get(this.myIdx).setMainBoard(true);
        this.name.requestFocus();
    }

    private void addBoards(List<String> turnOrder){
        int rows = 7, cols = 7;
        for(String s : turnOrder){
            this.names.add(s);
            Label label = new Label(s + ": 0");
            label.setId("label" + s);
            label.setFont(new Font(25));
            label.setTextAlignment(TextAlignment.RIGHT);
            label.setOnMouseClicked(e -> switchBoard(turnOrder.indexOf(s)));
            this.leaderboard.getChildren().add(label);

            BoardLogic b = new BoardLogic(rows, cols, this.boards.size(), this.dragLogic.getBoxes(), this);

            this.field.getChildren().add(b.getGridPane());
            this.stack.getChildren().add(b.getInventory());
            this.boards.add(b);
        }
        this.chat.createChat(this.stack);
    }

    public void scroll(GridPane pane, int numRows, int numCols) {
        //Vertical
        double cellHeight = pane.getHeight() / pane.getRowCount();

        double contentHeight = this.scroll.getContent().getBoundsInLocal().getHeight();
        double viewportHeight = this.scroll.getViewportBounds().getHeight();
        double newVerticalPixelScrollUnit = (contentHeight - viewportHeight) * this.scroll.getVvalue() + (cellHeight * numRows);
        double newVValue = newVerticalPixelScrollUnit / (contentHeight - viewportHeight);

        newVValue = Math.max(0, Math.min(newVValue, 1));
        this.scroll.setVvalue(newVValue);

        //Horizontal
        double cellWidth = pane.getWidth() / pane.getColumnCount();

        double contentWidth = this.scroll.getContent().getBoundsInLocal().getWidth();
        double viewportWidth = this.scroll.getViewportBounds().getWidth();
        double newHorizontalPixelScrollUnit = (contentWidth - viewportWidth) * this.scroll.getHvalue() + (cellWidth * numCols);
        double newHValue = newHorizontalPixelScrollUnit / (contentWidth - viewportWidth);

        newHValue = Math.max(0, Math.min(newHValue, 1));
        this.scroll.setHvalue(newHValue);
    }
}
