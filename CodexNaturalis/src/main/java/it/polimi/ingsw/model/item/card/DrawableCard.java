package it.polimi.ingsw.model.item.card;

import it.polimi.ingsw.model.util.Symbol;
import java.util.Map;

/**
 * Abstract class representing drawable cards in the game.
 */
public abstract class DrawableCard extends Card {
    private final int cardPoints;

    /**
     * Creates a card with the specified corners, center and points.
     *
     * @param corners The symbols representing the front corners of the card.
     * @param center The symbols representing the center of the card.
     * @param p The number of points of the card
     * @param id A unique identifier for a card.
     */
    public DrawableCard(Symbol[] corners, Symbol[] center, int p, String id){
        super(corners, center, id);
        this.cardPoints = p;
    }
    /**
     * Getter method for the cardPoints attribute.
     * This method returns a string representation of the cardPoints attribute.
     * It is intended for displaying the card points, not for mathematical operations.
     *
     * @return A string representation of the cardPoints attribute.
     */
    public String getPoints(){
        return this.cardPoints + "";
    }

    /**
     * Retrieves the points assigned to the card.
     *
     * @return The number of points assigned to the card.
     */
    public int getCardPoints() {
        return this.cardPoints;
    }

    /**
     * Returns true because not every card has requirements.
     * This method is intended to be overridden when necessary.
     *
     * @param inventory a map containing how many of each symbol are visible on the game board.
     * @return true.
     */
    public boolean checkRequirements(Map<Symbol, Integer> inventory) {
        return true;
    }

    /**
     * Returns the points awarded when placing the card face-up;
     * returns 0 otherwise since cards do not have points on their back.
     *
     * @param board The state of the game board after placing the card.
     * @param resources The resources of the board after placing the card.
     * @param x The x-coordinate of the card placement.
     * @param y The y-coordinate of the card placement.
     * @return The points obtained by placing the card.
     */
    public int givePoints(Card[][] board, Map<Symbol, Integer> resources, int x, int y) {
        if (getShowingFace()) {
            return this.cardPoints;
        }
        return 0;
    }
}
