package it.polimi.ingsw.view.controller;

import it.polimi.ingsw.util.Coord;
import it.polimi.ingsw.util.Message;
import it.polimi.ingsw.util.Symbol;
import it.polimi.ingsw.view.controller.sceneutil.ChatLogic;
import it.polimi.ingsw.view.controller.sceneutil.DragLogic;
import it.polimi.ingsw.view.coreview.CardView;
import it.polimi.ingsw.view.coreview.ItemView;
import it.polimi.ingsw.view.coreview.ObjectiveView;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
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
    private StackPane board, stack;
    @FXML
    private Label name, boardView;
    @FXML
    private VBox leaderboard;
    private List<GridPane> boards;
    private List<VBox> inventories;
    private List<Coord> starts;
    private IntegerProperty currIdx;
    private double scaleValue = 1.0;
    private final double scaleIncrement = 0.1;
    private String currPlayer;
    private int myIdx;
    private VBox lastPlaced;
    private List<VBox> boxes;
    private DragLogic dragLogic;
    private int placingIdx;
    private List<String> names;
    private ChatLogic chat;
    private boolean dragStart;

    public void initialize(){
        this.resDeck.imageProperty().addListener((observable, oldVal, newVal) -> this.resDeck.setOnMouseClicked(newVal == null ? null : this.resDeck.getOnMouseClicked()));
        this.res1.imageProperty().addListener((observable, oldVal, newVal) -> this.res1.setOnMouseClicked(newVal == null ? null : this.resDeck.getOnMouseClicked()));
        this.res2.imageProperty().addListener((observable, oldVal, newVal) -> this.res2.setOnMouseClicked(newVal == null ? null : this.resDeck.getOnMouseClicked()));
        this.goldDeck.imageProperty().addListener((observable, oldVal, newVal) -> this.goldDeck.setOnMouseClicked(newVal == null ? null : this.resDeck.getOnMouseClicked()));
        this.gold1.imageProperty().addListener((observable, oldVal, newVal) -> this.gold1.setOnMouseClicked(newVal == null ? null : this.resDeck.getOnMouseClicked()));
        this.gold2.imageProperty().addListener((observable, oldVal, newVal) -> this.gold2.setOnMouseClicked(newVal == null ? null : this.resDeck.getOnMouseClicked()));
        this.boards = new LinkedList<>();
        this.starts = new LinkedList<>();
        this.currIdx = new SimpleIntegerProperty();
        this.inventories = new LinkedList<>();
        this.boxes = new ArrayList<>();
        this.dragLogic = new DragLogic(boxes, this.scroll);
        this.names = new ArrayList<>(4);
        this.dragStart = false;

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
                this.board.getTransforms().clear();
                this.board.getTransforms().add(scale);
                event.consume();
            }
        });

        this.dragLogic.makeDraggable(this.hand0, this::addCard);
        this.dragLogic.makeDraggable(this.hand1, this::addCard);
        this.dragLogic.makeDraggable(this.hand2, this::addCard);
        this.lastPlaced = null;
    }

    public void updBoardView(int turnIdx, CardView card, int x, int y, Map<Symbol,Integer> inventory){
        updInventory(turnIdx, inventory);
        if(turnIdx!=myIdx || !dragStart) {
            addCard(turnIdx, card, x, y);
        }
    }

    private void updInventory(int turnIdx, Map<Symbol, Integer> inventory) {
        VBox v = this.inventories.get(turnIdx);
        for(Symbol s : inventory.keySet()){
            HBox h = (HBox) v.lookup("#" + turnIdx + s.name());
            if(h != null){
                for(Node l : h.getChildren()){
                    if(l instanceof Label){
                        ((Label) l).setText(inventory.get(s) + "");
                    }
                }
            }
        }
    }

    private void addCard(ImageView card, VBox v){
        int i = GridPane.getRowIndex(v);
        int j = GridPane.getColumnIndex(v);
        int turnIdx = this.boards.indexOf((GridPane) v.getParent());
        if(card.equals(this.hand0)){
            this.placingIdx = 0;
        }else if(card.equals(this.hand1)){
            this.placingIdx = 1;
        }else if(card.equals(this.hand2)){
            this.placingIdx = 2;
        }
        this.gui.placeCard(this.placingIdx, i - this.starts.get(turnIdx).x(), j - this.starts.get(turnIdx).y());
        addImage(turnIdx, i, j, card.getImage());
    }

    private void addCard(int turnIdx, CardView card, int x, int y){
        int j = this.starts.get(turnIdx).y() + y;
        int i = this.starts.get(turnIdx).x() + x;
        addImage(turnIdx, i, j, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/" + card.getId() + ".png"))));
    }

    private void addImage(int idx, int i, int j, Image card){
        VBox v = new VBox();
        v.setAlignment(Pos.CENTER);
        GridPane.setValignment(v, VPos.CENTER);
        GridPane.setHalignment(v, HPos.CENTER);
        ImageView img = new ImageView(card);
        img.setPreserveRatio(true);
        img.setFitHeight(150);
        img.setFitWidth(200);
        v.getChildren().add(img);
        this.boards.get(idx).getChildren().removeIf(node -> {
            Integer column = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            if(column != null && column == j && rowIndex != null && rowIndex == i){
                this.boxes.remove((VBox) node);
                return true;
            }
            return false;
        });
        this.boards.get(idx).add(v, j, i);
        expand(idx,i,j);
        this.lastPlaced = v;
    }

    private void expand(int turnIdx, int i, int j){
        if(i == 1){
            addRowAbove(turnIdx);
        }else if(i == this.boards.get(turnIdx).getRowCount() - 2){
            addRowBelow(turnIdx);
        }
        if(j == 1){
            addColumnLeft(turnIdx);
        }else if(j == this.boards.get(turnIdx).getColumnCount() - 2){
            addColumnRight(turnIdx);
        }
    }

    private void addRowAbove(int turnIdx){
        GridPane gridPane = this.boards.get(turnIdx);
        boolean startVbox = (Boolean) gridPane.getUserData();
        List<RowConstraints> constraints = gridPane.getRowConstraints();
        constraints.add(constraints.getFirst());
        gridPane.getChildren().forEach(node -> {
            Integer row = GridPane.getRowIndex(node);
            if (row != null) {
                GridPane.setRowIndex(node, row + 1);
            }
        });

        if(this.myIdx==turnIdx) {
            for (int i = (startVbox ? 1 : 0); i < gridPane.getColumnCount(); i += 2) {
                VBox vbox = new VBox();
                gridPane.add(vbox, i, 0);
                vbox.setStyle("-fx-background-color: #55ff00;");
                boxes.add(vbox);
                gridPane.setUserData(!startVbox);
            }
        }
        this.starts.set(turnIdx, new Coord(this.starts.get(turnIdx).x() + 1, this.starts.get(turnIdx).y()));
    }

    private void addRowBelow(int turnIdx){
        GridPane gridPane = this.boards.get(turnIdx);
        boolean startVbox = (Boolean) gridPane.getUserData();
        int start,end;
        List<RowConstraints> constraints = gridPane.getRowConstraints();
        constraints.add(constraints.getFirst());

        if(gridPane.getRowCount()%2==0){
            start = 1;
            end = 0;
        }else{
            start = 0;
            end = 1;
        }

        if(this.myIdx == turnIdx) {
            for (int i = (startVbox ? start : end); i < gridPane.getColumnCount(); i += 2) {
                VBox vbox = new VBox();
                gridPane.add(vbox, i, gridPane.getRowCount() - 1);
                vbox.setStyle("-fx-background-color: #55ff00;");
                boxes.add(vbox);
            }
        }
    }

    private void addColumnLeft(int turnIdx){
        GridPane gridPane = this.boards.get(turnIdx);
        boolean startVbox = (Boolean) gridPane.getUserData();
        List<ColumnConstraints> constraints = gridPane.getColumnConstraints();
        constraints.add(constraints.getFirst());
        gridPane.getChildren().forEach(node -> {
            Integer columnIndex = GridPane.getColumnIndex(node);
            if (columnIndex != null) {
                GridPane.setColumnIndex(node,columnIndex+1);
            }
        });

        if(this.myIdx==turnIdx) {
            for (int i = (startVbox ? 1 : 0); i < gridPane.getRowCount(); i += 2) {
                VBox vbox = new VBox();
                gridPane.add(vbox, 0, i);
                vbox.setStyle("-fx-background-color: #f56969;");
                boxes.add(vbox);
                gridPane.setUserData(!startVbox);
            }
        }
        this.starts.set(turnIdx, new Coord(this.starts.get(turnIdx).x(), this.starts.get(turnIdx).y() + 1));
    }


    private void addColumnRight(int turnIdx){
        GridPane gridPane = this.boards.get(turnIdx);
        boolean startVbox = (Boolean) gridPane.getUserData();
        int start,end;
        List<ColumnConstraints> constraints = gridPane.getColumnConstraints();
        constraints.add(constraints.getFirst());

        if(gridPane.getColumnCount()%2==0){
            start = 1;
            end = 0;
        }else{
            start = 0;
            end = 1;
        }

        for(int i=(startVbox? start : end);i<gridPane.getRowCount();i+=2){
            VBox vbox = new VBox();
            gridPane.add(vbox,gridPane.getColumnCount()-1,i);
            vbox.setStyle("-fx-background-color: #f56969;");
            boxes.add(vbox);
        }
    }



    private void addVbox(GridPane gridPane, int i, int j){
        VBox v = new VBox();
        this.boxes.add(v);
        v.setStyle("-fx-background-color: #11e5e5;");
        v.visibleProperty().addListener((observable, oldVal, newVal) -> v.setMouseTransparent(!newVal));

        gridPane.add(v, j, i);
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

    public void bind(StringProperty currPlayer, ObservableList<String> turnOrder, ObservableMap<String, Integer> points, ObservableList<Message> chats){
        currPlayer.addListener((observable, oldVal, newVal) -> {
            this.currPlayer = newVal;
            for(Node node : this.leaderboard.getChildren()){
                if(((Label)node).getText().matches(currPlayer.get() + ": \\d*")){
                    node.setStyle("-fx-text-fill: red;");
                }else{
                    node.setStyle("-fx-text-fill: black;");
                }
            }
        });
        turnOrder.addListener((ListChangeListener<String>) change -> {
            while(change.next()){
                if (change.wasAdded()) {
                    int rows = 7, cols = 7;
                    for(String s : change.getAddedSubList()){
                        this.names.add(s);
                        Label label = new Label(s + ": ");
                        label.setFont(new Font(25));
                        label.setTextAlignment(TextAlignment.RIGHT);
                        label.setOnMouseClicked(e -> switchBoard(turnOrder.indexOf(s)));
                        this.leaderboard.getChildren().add(label);

                        GridPane gp;
                        VBox v;
                        gp = createGrid(rows, cols);
                        gp.setUserData(true);
                        gp.setVisible(false);
                        this.boards.add(gp);

                        gp.setGridLinesVisible(true);
                        v = createInventory(this.inventories.size());
                        v.setVisible(false);
                        this.inventories.add(v);

                        this.starts.add(new Coord((rows - 1) / 2, (cols - 1) / 2));

                        this.board.getChildren().add(gp);
                        this.stack.getChildren().add(v);
                    }
                }
            }
            this.chat = new ChatLogic(this, chats, turnOrder);
            this.chat.createChat(this.stack);
            this.name.requestFocus();
        });
        points.addListener((MapChangeListener<String, Integer>) change -> Platform.runLater(()->{
            if (change.wasAdded()) {
                for(Node node : this.leaderboard.getChildren()){
                    if(((Label)node).getText().matches(change.getKey() + ": \\d*")){
                        ((Label)node).setText(change.getKey() + ": " + change.getValueAdded());
                    }
                }
            }
        }));
    }

    public void sendChatMsg(String msg, String receiver){
        this.gui.sendChatMsg(msg, receiver);
    }

    @Override
    public void confirm(boolean confirmValue,String confirmMsg){
        if(confirmValue){
            if(confirmMsg.equals("Successful placement")){
                this.gui.setIdx();
            }
        }else{
            if(this.lastPlaced != null){
                VBox v = this.lastPlaced;
                this.lastPlaced.getChildren().clear();
                this.boxes.add(v);
                switch(this.placingIdx){
                    case 0 -> this.hand0.setVisible(true);
                    case 1 -> this.hand1.setVisible(true);
                    case 2 -> this.hand2.setVisible(true);
                }
            }
        }
        this.lastPlaced = null;
    }

    private GridPane createGrid(int rows, int cols){
        GridPane gp = new GridPane();
        gp.visibleProperty().addListener((observable, oldVal, newVal) -> gp.setMouseTransparent(!newVal));
        for (int i = 0; i < cols; i++) {
            ColumnConstraints column = new ColumnConstraints(156);
            gp.getColumnConstraints().add(column);
        }

        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints(79);
            gp.getRowConstraints().add(row);
        }
        return gp;
    }

    private VBox createInventory(int idx){
        HBox[] hBoxes = new HBox[7];
        List<Symbol> s = Arrays.stream(Symbol.values()).filter(x -> x != Symbol.EMPTY && x != Symbol.NOTAVAILABLE).toList();
        for(int i = 0; i < hBoxes.length; i++){
            hBoxes[i] = new HBox();
            hBoxes[i].setSpacing(10);
            ImageView img = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/" + s.get(i).name() + ".png"))));
            img.setPreserveRatio(true);
            img.setFitWidth(20);
            hBoxes[i].getChildren().add(img);
            Label label = new Label("0");
            label.setFont(new Font(20));
            hBoxes[i].getChildren().add(label);
            hBoxes[i].setId(idx + s.get(i).name());
        }
        VBox v = new VBox(hBoxes);
        v.setMouseTransparent(true);
        v.setPadding(new Insets(30,50,0,0));
        v.setSpacing(10);
        return v;
    }

    public void draw(MouseEvent mouseEvent) {
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

    public void switchBoard(int idx){
        this.boards.get(this.currIdx.get()).setVisible(false);
        this.inventories.get(this.currIdx.get()).setVisible(false);
        this.currIdx.set(idx);
        this.boardView.setText(this.names.get(idx));
        this.boards.get(this.currIdx.get()).setVisible(true);
        this.inventories.get(this.currIdx.get()).setVisible(true);
        Platform.runLater(() -> {
            this.boards.get(this.currIdx.get()).requestLayout();
            scrollToCenter(this.scroll, this.boards.get(this.currIdx.get()), this.starts.get(this.currIdx.get()).x(), this.starts.get(this.currIdx.get()).y());
        });
    }

    public void setName(String name, StringProperty currPlayer, List<String> turnOrder){
        this.name.setText(name);
        this.chat.setName(name);
        this.myIdx = turnOrder.indexOf(name);
        this.dragStart = true;
        this.dragLogic.setDraggable((this.myIdx == this.currIdx.get()) && currPlayer.get().equals(name));
        this.currIdx.addListener((observable, oldVal, newVal) -> this.dragLogic.setDraggable((this.myIdx == (int)newVal) && this.currPlayer.equals(name)));
        currPlayer.addListener((observable, oldVal, newVal) -> this.dragLogic.setDraggable((this.myIdx == this.currIdx.get()) && currPlayer.get().equals(name)));
        addBoardVBoxes(this.boards.get(turnOrder.indexOf(name)));
    }

    private void addBoardVBoxes(GridPane gp){
        int rows = gp.getRowCount();
        int cols = gp.getColumnCount();
        for (int row = 0; row < rows; row++) {
            for (int col = row % 2 == 0 ? 0 : 1; col < cols; col+=2) {
                addVbox(gp, row, col);
            }
        }
    }


    public void setSecretObj(ObjectiveView obj){
        this.scrtObj.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/" + obj.getId() + ".png"))));
    }
}
