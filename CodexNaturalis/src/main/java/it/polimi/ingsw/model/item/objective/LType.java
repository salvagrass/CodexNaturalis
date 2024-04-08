package it.polimi.ingsw.model.item.objective;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.util.Symbol;

/**
 * Implementation of the Disposition interface for checking L type disposition on a game board.
 */
public class LType implements Disposition {
    final private Symbol mainColor, secondaryColor;
    final private int firstCardPos;

    /**
     * Constructs an LType object with the specified colors, first card position, and disposition dimensions.
     *
     * @param c1 The main color of the LType.
     * @param c2 The secondary color of the LType.
     * @param firstCardPos The position of the first card (0 = NW, 1 = NE, 2 = SE, 3 = SW).
     */
    public LType(Symbol c1, Symbol c2, int firstCardPos) {
        this.mainColor = c1;
        this.secondaryColor = c2;
        this.firstCardPos = firstCardPos;
    }

    /**
     * Checks the disposition of symbols on the provided game board.
     *
     * @param board The game board to check.
     * @return The number of occurrences of the specified disposition found on the board.
     */
    public int checkDisposition(Card[][] board) {
        int i, j;
        final int xDim = 2, yDim = 4; //The dimensions of the minimum matrix that contains this disposition.
        int countDisp;
        int yLim, xLim, yStart = 0, xStart = 0;
        int dirX = 1, dirY = 1; // Initialize variables for the x and y directions of subsequent disposition from the first card.

        yLim = board.length - 1;
        xLim = board[0].length - 1;

        // Set the starting point, the ending point, and the corresponding directions for each case based on the position of the first card.
        switch (this.firstCardPos) {
            case 0:
                // Case where the first card is in the top-left corner of the board.
                // The ending points are calculated as offsets from the board dimensions minus the dimensions of the shape.
                yLim = board.length - yDim;
                xLim = board[0].length - xDim;
                break;
            case 1:
                // Case where the first card is in the top-right corner of the board.
                // The starting point is adjusted to the top-right corner.
                xStart = xDim - 1;
                yLim = board.length - yDim;
                dirX = -1;
                break;
            case 2:
                // Case where the first card is in the bottom-right corner of the board.
                // The starting point is adjusted to the bottom-right corner, and directions are set accordingly.
                xStart = xDim - 1;
                yStart = yDim - 1;
                dirX = -1;
                dirY = -1;
                break;
            case 3:
                // Case where the first card is in the bottom-left corner of the board.
                // The starting point is adjusted to the bottom-left corner, and directions are set accordingly.
                yStart = yDim - 1;
                xLim = board[0].length - xDim;
                dirY = -1;
                break;
        }

        for (j = xStart, countDisp = 0; j < xLim; j++) {
            for (i = (yStart + j) % 2 == 0 ? yStart : yStart + 1; i < yLim; i+=2) {
                if (board[i][j] != null &&  board[i][j].getColor() == secondaryColor) {
                    if (board[i + dirY][j + dirX] != null && board[i + dirY][j + dirX].getColor() == mainColor &&
                            board[i + (yDim - 1) * dirY][j + dirX] != null && board[i + (yDim - 1) * dirY][j + dirX].getColor() == mainColor) {
                        countDisp++;
                        i += yDim - 2;
                    }
                }
            }
        }
        return countDisp;
    }
}

