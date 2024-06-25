package it.polimi.ingsw.model.item.objective;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.util.Symbol;

/**
 * Implementation of the Disposition interface for checking diagonal disposition on a game board.
 */
public class DiagonalType implements Disposition {
    final private Symbol color;
    final private int direction, len;

    /**
     * Constructs a DiagonalType object with the specified color, direction, and diagonal length requirements.
     *
     * @param c The color required for the diagonal disposition.
     * @param d The direction required for the diagonal disposition (1 for decreasing, -1 for increasing).
     * @param len The diagonal length required (>0).
     */
    public DiagonalType(Symbol c, int d, int len) {
        this.len = len;
        this.color = c;
        this.direction = d;
    }

    /**
     * Checks the disposition of symbols on the provided game board.
     *
     * @param board The game board to check.
     * @return The number of occurrences of the specified disposition found on the board.
     */
    public int checkDisposition(Card[][] board) {
        int i, j, k;
        int offset = this.direction == 1 ? 0 : this.len - 1;
        int dispositionCount, count;
        //Loop through the board
        for (i = 0, dispositionCount = 0; i < board.length - this.len + 1; i++) {
            //Consent to check only alternate cells starting
            for (j = ((i % 2 == 0) ? 0 : 1) + offset; j < board[0].length - this.len + 1 + offset; j += 2) {
                //Check if there's a card with the right color suitable as a starting point for the sequence
                if (board[i][j] != null && board[i][j].getColor() == color &&
                        (i - 1 < 0 || j - this.direction < 0 || j - this.direction >= board[0].length || // Check for the preceding sequence position
                                (board[i - 1][j - this.direction] == null ||
                                        board[i - 1][j - this.direction].getColor() != color))) {
                    //Check the entire diagonal starting at i,j and with {direction} direction
                    for (count = 1, k = 1; i + k < board.length &&
                            j + (k * this.direction) < board[0].length &&
                            j + (k * this.direction) >= 0; k++) {
                        if (board[i + k][j + (k * this.direction)] != null &&
                                board[i + k][j + (k * this.direction)].getColor() == color) {
                            count++;
                        }
                    }
                    // Increment the disposition count for each correct sequence found, counting each card only once
                    dispositionCount += count / len;
                }
            }
        }
        return dispositionCount;
    }

    public Symbol getColor() {
        return this.color;
    }

    public int getDirection() {
        return this.direction;
    }

    /**
     * Returns a string representation of the disposition.
     *
     * @return A string representation of the disposition.
     */
    @Override
    public String toString(){
        return "DiagonalType\nColor: " + this.color.toString() +
                "\nDirection: " + this.direction+
                "\nLength: " + this.len;
    }
}

