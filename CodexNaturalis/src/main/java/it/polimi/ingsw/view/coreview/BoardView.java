package it.polimi.ingsw.view.coreview;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.util.Coord;
import it.polimi.ingsw.util.Symbol;

import java.util.*;

/**
 * A class representing a view of the game board
 */
public class BoardView {
    private final Map<Coord,CardView> placedCards;
    private Map<Symbol,Integer> inventory;
    private int yMax,yMin,xMax,xMin;
    private List<Coord> cardOrder;

    /**
     * Constructor for the BoardView class.
     * Initializes the board view and inventory.
     */
    public BoardView(){
        this.placedCards = new HashMap<>();
        this.inventory = new HashMap<>();
        this.xMax = 0;
        this.xMin = 0;
        this.yMax = 0;
        this.yMin = 0;
    }

    public BoardView(Card[][] board,Map<Symbol,Integer> inventory, List<Coord> cardOrder){
        int i,j,xDiv,yDiv;
        this.xMax = 0;
        this.xMin = 0;
        this.yMax = 0;
        this.yMin = 0;
        xDiv = (board.length-1)/2;
        yDiv = (board.length-1)/2;
        this.placedCards = new HashMap<>();
        for(i=0;i<board.length;i++){
            for(j=0;j<board[0].length;j++){
                if(board[i][j]!=null){
                    this.updBoardView(new CardView(board[i][j]),i-xDiv,j-yDiv,null);
                }
            }
        }
        this.cardOrder = cardOrder;
        this.inventory = inventory;
    }

    /**
     * Updates the board view with a placed card and updates inventory.
     *
     * @param card      The card to be placed on the board.
     * @param x         The x-coordinate of the position on the board.
     * @param y         The y-coordinate of the position on the board.
     * @param inventory The inventory to be updated.
     */
    public void updBoardView(CardView card, int x, int y, Map<Symbol,Integer> inventory){
        if(x>=xMax)
            xMax = x;
        else if(x<=xMin) {
            xMin = x;
        }
        if(y>=yMax)
            yMax = y;
        else if(y<=yMin) {
            yMin = y;
        }
        this.inventory = inventory;
        this.placedCards.put(new Coord(x,y),card);
    }

    /**
     * Generates an ASCII representation of the game board.
     *
     * @return The ASCII representation of the game board.
     */
    public String asciiBoard(){
        int i,j;
        StringBuilder board = new StringBuilder();
        CardView view;
        for(i=xMin;i<=xMax;i++){
            board.append(String.format("%-5s",i));
            for(j=yMin;j<=yMax;j++){
                view = placedCards.get(new Coord(i,j));
                if(view!=null)
                    board.append(String.format("%-13s","["+view.getColor().ANSI_COLOR+"X"+Symbol.ANSI_RESET+"]"))
                            .append(view.getColor().ANSI_COLOR.equals(Symbol.ANSI_RESET) ? "" : " ");
                else
                    board.append(String.format("%-5s","[ ]"));
            }
            board.append("\n");
        }
        board.append("     ");
        for(j=yMin;j<=yMax;j++){
            board.append(String.format("%-5s",j));
        }
        board.append("\n\nInventory:\n");
        for(Symbol sym:this.inventory.keySet()){
            board.append(sym.name()).append(": ").append(this.inventory.get(sym)).append('\n');
        }
        return board.toString();
    }

    /**
     * Retrieves the ASCII art representation of a card at a specified position on the board.
     *
     * @param x The x-coordinate of the position on the board.
     * @param y The y-coordinate of the position on the board.
     * @return The ASCII art representation of the card at the specified position.
     */
    public String showCard(int x,int y){
        CardView view = placedCards.get(new Coord(x,y));
        if(view!=null)
            return CardView.showAsciiArt(view);
        else
            return "Card not placed in this position\n";
    }

    public Map<Coord, CardView> getPlacedCards(){
        return this.placedCards;
    }

    public Map<Symbol, Integer> getInventory(){
        return this.inventory;
    }

    public List<Coord> getCardOrder(){
        return this.cardOrder;
    }
}
