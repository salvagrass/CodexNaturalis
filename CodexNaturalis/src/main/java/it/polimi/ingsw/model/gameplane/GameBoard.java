package it.polimi.ingsw.model.gameplane;

import it.polimi.ingsw.model.util.Symbol;
import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.model.util.exception.InvalidPlacementException;

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

    /**
     * Constructor for GameBoard.
     * Initializes the board and its resource count.
     */
    public GameBoard(){
        final int startSize = 31;
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
        int xAbs = (board.length-1)/2;
        int yAbs = (board[0].length-1)/2;
        board[xAbs][yAbs] = start;
        updResources(start,xAbs,yAbs);
    }

    /**
     * Retrieves the count for each resource on the board
     * @return a copy of the map containing the count of each resource
     */
    public Map<Symbol,Integer> getCountResources(){
        return new HashMap<>(countResources);
    }


    /**
     * Check if you can place a card given a position on the board
     * @param xRel the x-coordinate of the position (relative to the starting card)
     * @param yRel the y-coordinate of the position (relative to the starting card)
     * @return True if the position is valid, false otherwise
     */
    public boolean checkPosition(int xRel, int yRel){
        int x = xRel + (board.length-1)/2;
        int y = yRel + (board[0].length-1)/2;
        int i, j,counter;
        boolean flag = false;
        Card tmp;

        /* Checks in order:
           1) If the position is out the matrix
           2) If the position is invalid (if we start from the middle of the matrix x+y cant be odd)
           3) If a card is already placed in the same spot
         */
        if( (x>=board.length||y>=board[0].length)||(x<0||y<0) || (x+y)%2!=0 || board[x][y]!=null)
            return false;

        boolean rightToLeft = true;
        for(i=1,counter=0;i>=-1;i-=2){
            // Iterate alternatively from right to left and left to right
            int jStart = (rightToLeft ? 1 : -1);
            int step = (rightToLeft ? -2 : 2);

            for(j=jStart;(rightToLeft ? j>=-1 : j<=1);j += step,counter++){
                int xPos = x+i;
                int yPos = y+j;

                // check to see if xPos and yPos are valid indexes
                if( (xPos>=0 && xPos<board.length) && (yPos>=0 && yPos< board[0].length) ){
                    tmp = board[xPos][yPos];
                    if(tmp!=null){
                        // If there's a card, and it's corner is not available then return false
                        if(tmp.getShowingCorners()[counter]==Symbol.NOTAVAILABLE)
                            return false;
                        // If there's a card and its corner is available then set the flag to true and continue to check the other cards
                        else
                            flag = true;
                    }
                }
            }
            rightToLeft = !rightToLeft;
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
     * @param x the x-coordinate of the card position (absolute)
     * @param y the y-coordinate of the card position (absolute)
     */
    private void updResources(Card card, int x, int y){
        int i,j,counter,xPos,yPos;
        boolean rightToLeft = true;
        for(i=1,counter=0;i>=-1;i-=2){
            // Iterate alternatively from right to left and left to right
            int jStart = (rightToLeft ? 1 : -1);
            int step = (rightToLeft ? -2 : 2);

            for(j=jStart;(rightToLeft ? j>=-1 : j<=1);j += step,counter++){
                xPos = x+i;
                yPos = y+j;
                // check to see if xPos and yPos are valid indexes
                if( (xPos>=0 && xPos<board.length) && (yPos>=0 && yPos< board[0].length) )
                    // if there's a symbol on the corner of the card then it gets covered
                    if(board[xPos][yPos]!=null && board[xPos][yPos].getShowingCorners()[counter]!=Symbol.EMPTY){
                        Card tmp = board[xPos][yPos];
                        countResources.put(tmp.getShowingCorners()[counter],countResources.get(tmp.getShowingCorners()[counter])-1);
                    }
            }
            rightToLeft = !rightToLeft;
        }

        //Adding all the symbols of the placed card
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
     * @param xRel the x-coordinate of the position - relative to the starting card as its origin point
     * @param yRel the y-coordinate of the position - relative to the starting card as its origin point
     * @return the points given by the placement of that card
     */
    public int addCard(DrawableCard card,int xRel, int yRel) throws InvalidPlacementException {
       if(!checkPosition(xRel,yRel)||!checkRequirements(card))
           throw new InvalidPlacementException("Invalid Card Placement");
       int x = xRel + (board.length-1)/2;
       int y = yRel + (board[0].length-1)/2;
       board[x][y] = card;
       updResources(card,x,y);
       if(x==board.length-1||y==board[0].length-1||x==0||y==0)
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


    //TODO: Possibility to optimize the method
    /**
     * Method used for reallocating the game board when an edge is reached
     * The board expands symmetrically around its center
     */

    private void expandBoard(){
        //deltaSize has to be even to keep a single middle cell in the matrix
        final int deltaSize = 10;
        Card[][] newBoard = new Card[board.length+deltaSize][board[0].length+deltaSize];

        for(int i=0;i<board.length;i++)
            for(int j=i%2==0?0:1;j<board[0].length;j+=2)
                newBoard[i+deltaSize/2][j+deltaSize/2] = board[i][j];
        board = newBoard;
    }
}
