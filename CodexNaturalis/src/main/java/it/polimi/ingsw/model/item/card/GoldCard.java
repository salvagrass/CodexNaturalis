package it.polimi.ingsw.model.item.card;

import it.polimi.ingsw.util.Symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing gold cards with fixed points in the game.
 * This class is intended to be extended by specific gold card types.
 */
public class GoldCard extends DrawableCard {
    final private Map<Symbol, Integer> requirements;

    /**
     * Constructs a GoldCard with the specified front corners, center, requirements, and points.
     *
     * @param frontCorners The symbols representing the front corners of the card.
     * @param center The symbols representing the center of the card.
     * @param requirements A map containing how many of each symbol are required from the card.
     * @param points The number of points awarded by the card.
     * @param id A unique identifier for a card.
     */
    public GoldCard(Symbol[] frontCorners, Symbol[] center, Map<Symbol, Integer> requirements, int points, String id){
        super(frontCorners, center, points, id);
        this.requirements = requirements;
    }

    /**
     * Returns a copy of the requirements attribute.
     *
     * @return A copy of the requirements attribute.
     */
    public Map<Symbol,Integer> getRequirements(){
        return new HashMap<>(this.requirements);
    }

    /**
     * Checks if the requirements of the gold card are satisfied with the current
     * board state before placing the card.
     * For each necessary symbol, check that the number of appearances on the board
     * is greater than or equal to the required quantity.
     *
     * @param inventory a map containing how many of each symbol are visible on the game board.
     * @return true if the requirements are satisfied, false otherwise.
     */
    public boolean checkRequirements(Map<Symbol, Integer> inventory) {
        if(!getShowingFace()){
            return true;
        }
        for (Map.Entry<Symbol, Integer> set : requirements.entrySet()) {
            if (inventory.get(set.getKey()) < set.getValue()) {
                return false;
            }
        }
        return true;
    }

    public String toString(){
        return super.toString() +
                "\nRequirements: " + this.requirements.toString()+
                "\nPoints: " + this.getPoints();
    }
}