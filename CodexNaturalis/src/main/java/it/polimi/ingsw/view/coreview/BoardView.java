package it.polimi.ingsw.view.coreview;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.util.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class representing a view of the game board
 */
public class BoardView {
    private record Coord(int x, int y){

    }
    private final Map<Coord,CardView> placedCards;
    private Map<Symbol,Integer> inventory;
    private int counter;
    private int yMax,yMin,xMax,xMin;

    public BoardView(){
        this.placedCards = new HashMap<>();
        this.inventory = new HashMap<>();
        counter = -1;
        xMax = 0;
        xMin = 0;
        yMax = 0;
        yMin = 0;
    }

    public void updBoardView(Card card, int x, int y, Map<Symbol,Integer> inventory){
        CardView view = new CardView(card);
        counter++;
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
        placedCards.put(new Coord(x,y),view);
    }

    public String asciiBoard(){
        int i,j;
        StringBuilder board = new StringBuilder();
        CardView view=null;
        for(i=xMin;i<=xMax;i++){
            board.append(String.format("%-5s",i));
            for(j=yMin;j<=yMax;j++){
                view = placedCards.get(new Coord(i,j));
                if(view!=null)
                    board.append(String.format("%-14s","["+view.getColor().ANSI_COLOR+"X"+Symbol.ANSI_RESET+"]"));
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


    public String showCard(int x,int y){
        CardView view = placedCards.get(new Coord(x,y));
        if(view!=null)
            return CardView.showAsciiArt(view);
        else
            return "Card not placed in this position\n";
    }

}
