package it.polimi.ingsw.view.controller.sceneutil;

import it.polimi.ingsw.util.Symbol;
import it.polimi.ingsw.view.controller.GameController;
import it.polimi.ingsw.view.coreview.CardView;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BoardLogic {
    private final GameController gc;
    private final GridPane gridPane;
    private final List<VBox> boxes;
    private final VBox inventory;
    private VBox lastPlaced;
    private int xStart, yStart;
    private boolean mainBoard;

    /**
     * Initializes a new instance of the `BoardLogic` class.
     *
     * @param rows The number of rows in the grid.
     * @param cols The number of columns in the grid.
     * @param num The number of items in the inventory.
     * @param boxes A list of `VBox` objects representing game boxes.
     * @param gameController The `GameController` instance.
     */
    public BoardLogic(int rows, int cols, int num, List<VBox> boxes, GameController gameController){
        this.gridPane = createGrid(rows, cols);
        this.gridPane.setUserData(true);
        this.gridPane.setVisible(false);

        this.inventory = createInventory(num);
        this.inventory.setVisible(false);

        this.xStart = (rows - 1) / 2;
        this.yStart = (cols - 1) / 2;

        this.boxes = boxes;

        this.gc = gameController;
    }

    /**
     * Creates a new `GridPane` with the specified number of rows and columns.
     *
     * @param rows The number of rows in the grid.
     * @param cols The number of columns in the grid.
     * @return A new `GridPane` instance.
     */
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

    /**
     * Creates a new `VBox` representing the inventory.
     *
     * @param idx The index of the inventory.
     * @return A new `VBox` instance representing the inventory.
     */
    private VBox createInventory(int idx){
        HBox[] hBoxes = new HBox[7];
        List<Symbol> s = Arrays.stream(Symbol.values()).filter(x -> x != Symbol.EMPTY && x != Symbol.NOTAVAILABLE).toList();
        for(int i = 0; i < hBoxes.length; i++){
            hBoxes[i] = new HBox();
            hBoxes[i].setSpacing(10);
            hBoxes[i].setAlignment(Pos.CENTER_LEFT);
            ImageView img = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/icons/" + s.get(i).name() + ".png"))));
            img.setPreserveRatio(true);
            img.setFitWidth(20);
            hBoxes[i].getChildren().add(img);
            Label label = new Label("0");
            label.setFont(new Font(20));
            label.setStyle("-fx-text-fill: white;");
            hBoxes[i].getChildren().add(label);
            hBoxes[i].setId(idx + s.get(i).name());
        }
        VBox v = new VBox(hBoxes);
        v.setMouseTransparent(true);
        v.setPadding(new Insets(30,50,0,10));
        v.setSpacing(5);
        return v;
    }

    /**
     * Adds a card to the grid at the specified position.
     *
     * @param card The `CardView` to add.
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     */
    public void addCard(CardView card, int x, int y){
        int j = this.yStart + y;
        int i = this.xStart + x;
        addImage(i, j, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/" + card.getId() + ".png"))));
    }

    /**
     * Places an image at the specified position.
     *
     * @param i The row index.
     * @param j The column index.
     * @param card The image to be placed.
     */
    public void placeCard(int i, int j, Image card){
        this.lastPlaced = addImage(i, j, card);
    }

    /**
     * Adds an image to the specified position in the grid.
     *
     * @param i The row index.
     * @param j The column index.
     * @param card The image to be added.
     * @return The VBox containing the added image.
     */
    private VBox addImage(int i, int j, Image card){
        VBox v = new VBox();
        v.setAlignment(Pos.CENTER);
        GridPane.setValignment(v, VPos.CENTER);
        GridPane.setHalignment(v, HPos.CENTER);
        ImageView img = new ImageView(card);
        img.setPreserveRatio(true);
        img.setFitHeight(150);
        img.setFitWidth(200);
        v.getChildren().add(img);
        this.gridPane.getChildren().removeIf(node -> {
            Integer column = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            if(column != null && column == j && rowIndex != null && rowIndex == i){
                this.boxes.remove((VBox) node);
                return true;
            }
            return false;
        });
        this.gridPane.add(v, j, i);
        expand(i,j);
        return v;
    }

    /**
     * Expands the grid layout by adding rows or columns if necessary.
     *
     * @param i The row index.
     * @param j The column index.
     */
    private void expand(int i, int j){
        if(i == 1){
            addRowAbove();
        }else if(i == this.gridPane.getRowCount() - 2){
            addRowBelow();
        }
        if(j == 1){
            addColumnLeft();
        }else if(j == this.gridPane.getColumnCount() - 2){
            addColumnRight();
        }
    }

    /**
     * Adds a row above the current grid layout.
     * If the grid is the main board, it also scrolls horizontally and adds VBox elements.
     */
    private void addRowAbove(){
        boolean startVbox = (Boolean) this.gridPane.getUserData();
        List<RowConstraints> constraints = this.gridPane.getRowConstraints();
        constraints.add(constraints.getFirst());
        this.gridPane.getChildren().forEach(node -> {
            Integer row = GridPane.getRowIndex(node);
            if (row != null) {
                GridPane.setRowIndex(node, row + 1);
            }
        });

        if(this.mainBoard) {
            gc.scroll(this.gridPane, 1, 0);
            for (int i = (startVbox ? 1 : 0); i < this.gridPane.getColumnCount(); i += 2) {
                addVbox(0,i);
            }
            this.gridPane.setUserData(!startVbox);
        }
        this.xStart++;
    }

    /**
     * Adds a row below the current grid layout.
     * If the grid is the main board, it also adds VBox elements to the new row.
     */
    private void addRowBelow(){
        boolean startVbox = (Boolean) this.gridPane.getUserData();
        int start,end;
        List<RowConstraints> constraints = this.gridPane.getRowConstraints();
        constraints.add(constraints.getFirst());

        if(this.gridPane.getRowCount()%2==0){
            start = 1;
            end = 0;
        }else{
            start = 0;
            end = 1;
        }

        if(this.mainBoard) {
            for (int i = (startVbox ? start : end); i < this.gridPane.getColumnCount(); i += 2) {
                addVbox(gridPane.getRowCount()-1,i);
            }
        }
    }

    /**
     * Adds a column on the left of the current grid layout.
     * If the grid is the main board, it also adds VBox elements to the new column.
     */
    private void addColumnLeft(){
        boolean startVbox = (Boolean) this.gridPane.getUserData();
        List<ColumnConstraints> constraints = this.gridPane.getColumnConstraints();
        constraints.add(constraints.getFirst());
        this.gridPane.getChildren().forEach(node -> {
            Integer columnIndex = GridPane.getColumnIndex(node);
            if (columnIndex != null) {
                GridPane.setColumnIndex(node,columnIndex+1);
            }
        });

        if(this.mainBoard) {
            gc.scroll(this.gridPane, 0, 1);
            for (int i = (startVbox ? 1 : 0); i < this.gridPane.getRowCount(); i += 2) {
                addVbox(i,0);
            }
            this.gridPane.setUserData(!startVbox);
        }
        this.yStart++;
    }

    /**
     * Adds a column on the right of the current grid layout.
     * If the grid is the main board, it also adds VBox elements to the new column.
     */
    private void addColumnRight(){
        boolean startVbox = (Boolean) this.gridPane.getUserData();
        int start,end;
        List<ColumnConstraints> constraints = this.gridPane.getColumnConstraints();
        constraints.add(constraints.getFirst());

        if(this.gridPane.getColumnCount()%2==0){
            start = 1;
            end = 0;
        }else{
            start = 0;
            end = 1;
        }

        if(this.mainBoard){
            for(int i=(startVbox? start : end);i<this.gridPane.getRowCount();i+=2){
                addVbox(i,gridPane.getColumnCount()-1);
            }
        }
    }

    /**
     * Adds a VBox to the gridPane at the specified row and column.
     *
     * @param i The row index.
     * @param j The column index.
     */
    private void addVbox(int i, int j){
        VBox v = new VBox();
        this.boxes.add(v);
        v.visibleProperty().addListener((observable, oldVal, newVal) -> v.setMouseTransparent(!newVal));

        this.gridPane.add(v, j, i);
    }

    /**
     * Adds VBox (vertical boxes) to the gridPane for the entire board.
     * The boxes are added in a checkerboard pattern.
     */
    private void addBoardVBoxes(){
        int rows = this.gridPane.getRowCount();
        int cols = this.gridPane.getColumnCount();
        for (int row = 0; row < rows; row++) {
            for (int col = row % 2 == 0 ? 0 : 1; col < cols; col+=2) {
                addVbox(row, col);
            }
        }
    }

    /**
     * Updates the inventory display for a specific turn index.
     *
     * @param turnIdx The index of the turn.
     * @param inventory A map containing symbols and their corresponding counts.
     */
    public void updInventory(int turnIdx, Map<Symbol, Integer> inventory) {
        for(Symbol s : inventory.keySet()){
            HBox h = (HBox) this.inventory.lookup("#" + turnIdx + s.name());
            if(h != null){
                for(Node l : h.getChildren()){
                    if(l instanceof Label){
                        ((Label) l).setText(inventory.get(s) + "");
                    }
                }
            }
        }
    }

    /**
     * Sets the last placed VBox (vertical box) to the specified value.
     *
     * @param newVal The new VBox to set as the last placed.
     */
    public void setLastPlaced(VBox newVal){
        this.lastPlaced = newVal;
    }

    /**
     * Clears the contents of the last placed VBox and adds it back to the list of boxes.
     * If the last placed VBox is null, no action is taken.
     */
    public void clearLastPlaced() {
        if(this.lastPlaced != null){
            VBox v = this.lastPlaced;
            this.lastPlaced.getChildren().clear();
            this.boxes.add(v);
        }
    }

    /**
     * Gets the GridPane associated with this object.
     *
     * @return The GridPane instance.
     */
    public GridPane getGridPane(){
        return this.gridPane;
    }

    /**
     * Get the inventory.
     *
     * @return The VBox instance.
     */
    public VBox getInventory(){
        return this.inventory;
    }

    /**
     * Gets the starting X-coordinate associated with this object.
     *
     * @return The starting X-coordinate.
     */
    public int getXStart(){
        return this.xStart;
    }

    /**
     * Gets the y-coordinate start value.
     *
     * @return The y-coordinate start value.
     */
    public int getYStart() {
        return yStart;
    }

    /**
     * Sets the main board flag.
     *
     * @param mainBoard The value to set for the main board flag.
     */
    public void setMainBoard(boolean mainBoard){
        this.mainBoard = mainBoard;
        if(mainBoard){
            addBoardVBoxes();
        }
    }

    /**
     * Sets the visibility of the grid pane and inventory.
     *
     * @param b The visibility flag (true to show, false to hide).
     */
    public void setVisible(boolean b) {
        this.gridPane.setVisible(b);
        this.inventory.setVisible(b);
    }
}
