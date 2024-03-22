package it.polimi.ingsw;

import java.util.Map.Entry;

/**
 * Class representing gold cards with fixed points in the game.
 * This class is intended to be extended by specific gold card types.
 */
public class GoldCard extends DrawableCard {
    final Map<Symbol, Integer> requirements;

    /**
     * Constructs a card using information provided by the specified enum.
     *
     * @param e An enum containing all the necessary information to create the card.
     */
    public GoldCard(GoldCardData e) {
        this.frontCorners = e.getFrontCorners();
        this.center = e.getCenter();
        this.showingFace = e.getShowingFace();
        this.cardPoints = e.getCardPoints();
        this.requirements= e.getRequirements();
    }

    /**
     * Checks if the requirements of the gold card are satisfied with the current
     * board state before placing the card.
     * For each necessary symbol, check that the number of appearances on the board
     * is greater than or equal to the required quantity.
     *
     * @param board The state of the game board before placing the card.
     * @return true if the requirements are satisfied, false otherwise.
     */
    public boolean checkRequirements(GameBoard board) {
        Map<Symbol, Integer> map = board.getCountResources();
        for (Entry<Symbol, Integer> set : requirements.entrySet()) {
            if (map.get(set.getKey()) < set.getValue()) {
                return false;
            }
        }
        return true;
    }
}