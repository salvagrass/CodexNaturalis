package it.polimi.ingsw.model.gameplane;

import it.polimi.ingsw.model.utils.Symbol;
import it.polimi.ingsw.model.item.Card;
import it.polimi.ingsw.model.item.DrawableCard;
import it.polimi.ingsw.model.item.Objective;
import it.polimi.ingsw.model.item.StartingCard;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the game board for the game.
 * The board consists of cards and the resources currently placed on it.
 * manages the game board, including adding starting cards, checking positions, and adding drawable cards.
 */

public class GameBoard {
    private Card[][] board;
    private final Map<Symbol,Integer> countResources;
    private final int startSize = 31;

    /**
     * Constructor for GameBoard.
     * Initializes the board and its resource count.
     */
    GameBoard(){
        board = new Card[startSize][startSize];
        countResources = new EnumMap<>(Symbol.class);
        for(Symbol s : Symbol.values())
            if(s!=Symbol.EMPTY && s!=Symbol.NOTAVAILABLE)
                countResources.put(s,0);
    }

    /**
     * Adds the starting card to the center of the board
     * @param start the starting card to be added to the board
     */
    public void addStartingCard(StartingCard start){
        board[(startSize-1)/2][(startSize-1)/2] = start;
        updResources(start,(startSize-1)/2,(startSize-1)/2);
    }

    /**
     * Retrieves the count for each resource on the board
     * @return a copy of the map containing the count of each resource
     */
    public Map<Symbol,Integer> getCountResources(){
        return new HashMap<>(countResources);
    }


    //TODO: TEST THE NEW METHOD

    /**
     * Check if you can place a card given a position on the board
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return True if the position is valid, false otherwise
     */
    public boolean checkPosition(int x, int y) {
        int i, j,counter;
        boolean flag = false;
        Card tmp;

        if(x+y%2!=0||board[x][y]!=null)
            return false;

        j = 1;
        counter = 0;
        for(i=1;i>=-1;i-=2){
            for(;(i>0 && j>=-i)||(i<0 && j<=-i);j-=2*i){
                tmp = board[x+i][y+j];
                if(tmp!=null){
                    if(tmp.getShowingCorners()[counter]==Symbol.NOTAVAILABLE)
                        return false;
                    else
                        flag = true;
                }
                counter++;
            }
            j = -1;
        }
        return flag;
    }

    /**
     * Checks if the requirements for a specific card are met
     * @param card the card to be place
     * @return True if the requirements for the card are met, false otherwise
     */
    public boolean checkRequirements(DrawableCard card){
        return card.checkRequirements(this.countResources);
    }


    /**
     * Updates the resources' count of the board after placing a card
     * @param card the card that was placed
     * @param x the x-coordinate of the card position
     * @param y the y-coordinate of the card position
     */
    //TODO: TEST THE METHOD
    private void updResources(Card card, int x, int y){
        int i;
        int j = 1;
        int counter = 0;
        for(i=1;i>=-1;i-=2){
            for(;(i>0 && j>=-i)||(i<0 && j<=-i);j-=2*i){
                if( (x+i< board.length) && (y+j< board[0].length) ) {
                    Card tmp = board[x+i][y+j];
                    if (tmp!=null && tmp.getShowingCorners()[counter] != Symbol.EMPTY)
                        countResources.put(tmp.getShowingCorners()[counter], countResources.get(tmp.getShowingCorners()[counter])-1);
                }
                counter++;
            }
            j = -1;
        }

        for(Symbol s : card.getShowingCorners())
            if(s!=Symbol.EMPTY && s!=Symbol.NOTAVAILABLE)
                countResources.put(s,countResources.get(s)+1);
        for(Symbol s : card.getCenter())
            if(s!=Symbol.EMPTY && s!=Symbol.NOTAVAILABLE)
                countResources.put(s,countResources.get(s)+1);
    }

    /**
     * Places a card on the board in the specified position
     * @param card the card to be placed
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return the points given by the placement of that card
     */
    public int addCard(DrawableCard card,int x, int y){
       board[x][y] = card;
       updResources(card,x,y);
       if(x==board.length-2&&y==board[0].length-2)
            expandBoard();
       return card.givePoints(this.board,this.countResources,x,y);

    }

    /**
     * Calculates the point scored for the given objective
     * @param obj the objective to calculate the points for
     * @return the number of points scored
     */
    public int countObjPoints(Objective obj){
        return obj.countObjectivePoints(this.board,this.countResources);
    }

    /**
     * Method used for reallocating the gameboard when an edge is reached
     */

    private void expandBoard(){
        Card[][] newBoard = new Card[board.length+10][board[0].length+10];

        for(int i=0;i<board.length;i++)
            for(int j=i%2==0?0:1;j<board[0].length;j+=2)
                newBoard[i+5][j+5] = board[i][j];
        board = newBoard;
    }
}
