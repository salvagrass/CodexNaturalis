package it.polimi.ingsw;

/**
 * Implementation of the Disposition interface for checking diagonal disposition on a game board.
 */
public class DiagonalType implements Disposition {
    Symbol color;
    int direction, len;

    /**
     * Constructs a DiagonalType object with the specified color, direction, and diagonal length requirements.
     *
     * @param c The color required for the diagonal disposition.
     * @param d The direction required for the diagonal disposition (1 for decreasing, -1 for increasing).
     * @param len The diagonal length required.
     */
    public DiagonalType(Symbol c, int d, int len) {
        this.len = len;
        this.color = c;
        this.direction = d;
    }

    /**
     * Checks the disposition of symbols on the provided game board.
     *
     * @param g The game board to check.
     * @return The number of occurrences of the specified disposition found on the board.
     */
    public int checkDisposition(GameBoard g) {
        int i, j, k;
        int dispositionCount, count;
        Card[][] c = g.getBoard();
        //Loop through the board
        for (i = 0, dispositionCount = 0; i < c.length - this.len + 1; i++) {
            //Consent to check only alternate cells
            for (j = (i % 2 == 0) ? 0 : 1; j < c[0].length - this.len + 1; j += 2) {
                //Check if there's a card with the right color suitable as a starting point for the sequence
                if (c[i][j] != null && c[i][j].getColor() == color &&
                        (i - 1 < 0 || j - this.direction < 0 || j - this.direction >= c[0].length || // Check for the preceding sequence position
                                (c[i - 1][j - this.direction] == null ||
                                        c[i - 1][j - this.direction].getColor() != color))) {
                    //Check the entire diagonal starting at i,j and with {direction} direction
                    for (count = 0, k = 1; i + k < c.length; k++) {
                        if (c[i + k][j + (k * this.direction)] != null &&
                                c[i + k][j + (k * this.direction)].getColor() == color) {
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
}

