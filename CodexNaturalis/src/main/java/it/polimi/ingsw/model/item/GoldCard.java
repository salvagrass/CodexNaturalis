package it.polimi.ingsw.model.item;

import it.polimi.ingsw.model.gameplane.GameBoard;
import it.polimi.ingsw.Symbol;

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
     * @param requirements The symbols representing the requirements of the card.
     * @param points The number of points awarded by the card.
     */
    public GoldCard(Symbol[] frontCorners, Symbol[] center, Symbol[] requirements, int points){
        super(frontCorners, center, points);
        this.requirements = new HashMap<>();
        setRequirements(requirements);
    }

    /**
     * Takes an array of symbols and creates a map associating each symbol with its number of appearances.
     * The resulting map is stored in the class attribute.
     *
     * @param req The array of symbols.
     */
    private void setRequirements(Symbol[] req){
        for(Symbol s: req){
            if(this.requirements.containsKey(s)){
                this.requirements.put(s, this.requirements.get(s)+1);
            }else{
                this.requirements.put(s, 1);
            }
        }
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
        for (Map.Entry<Symbol, Integer> set : requirements.entrySet()) {
            if (map.get(set.getKey()) < set.getValue()) {
                return false;
            }
        }
        return true;
    }
}