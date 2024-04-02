package it.polimi.ingsw.model.item;

/**
 * Interface representing a disposition check for a game board.
 * Implementing classes must provide a method to check the disposition of symbols on the board.
 */
public interface Disposition {
    /**
     * Checks the disposition of symbols on the provided game board.
     *
     * @param board The game board to check.
     * @return The number of occurrences of the specified disposition found on the board.
     */
    int checkDisposition(Card[][] board);
}
