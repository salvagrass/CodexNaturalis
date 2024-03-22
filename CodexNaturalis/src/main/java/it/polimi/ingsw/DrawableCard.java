package it.polimi.ingsw;

/**
 * Abstract class representing drawable cards in the game.
 */
public abstract class DrawableCard extends Card {
    int cardPoints;

    /**
     * Getter method for the cardPoints attribute.
     * This method returns a string representation of the cardPoints attribute.
     * It is intended for displaying the card points, not for mathematical operations.
     *
     * @return A string representation of the cardPoints attribute.
     */
    public String getPoints() {
        return this.cardPoints + "";
    }

    /**
     * Returns true because not every card has requirements.
     * This method is intended to be overridden when necessary.
     *
     * @param board The state of the game board before placing the card.
     * @return true.
     */
    public boolean checkRequirements(GameBoard board) {
        return true;
    }

    /**
     * Returns the points awarded when placing the card face-up;
     * returns 0 otherwise since cards do not have points on their back.
     *
     * @param board The state of the game board after placing the card.
     * @param x The x-coordinate of the card placement.
     * @param y The y-coordinate of the card placement.
     * @return The points obtained by placing the card.
     */
    public int givePoints(GameBoard board, int x, int y) {
        if (getShowingFace()) {
            return this.cardPoints;
        }
        return 0;
    }
}
