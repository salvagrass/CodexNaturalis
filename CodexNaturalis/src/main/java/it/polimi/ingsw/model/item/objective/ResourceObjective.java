package it.polimi.ingsw.model.item.objective;
import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.util.Symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing objectives that require a specified number of symbols in the game.
 */
public class ResourceObjective extends Objective {
    final private Map<Symbol, Integer> resources;

    /**
     * Constructs an objective with the specified points and symbol requirements.
     *
     * @param p   The number of points of the objective.
     * @param sym The map associating each symbol with its required quantity.
     * @param id  A unique identifier for a objective.
     */
    public ResourceObjective(int p, String id, Map<Symbol, Integer> sym) {
        super(p, id);
        this.resources = sym;
    }

    /**
     * Counts the amount of times each symbol of the objective appears in the game board resources,
     * normalises it based on the correspondent number in the objective map, and selects the minimum of these values.
     * This minimum value is then multiplied by the card points to determine the points gained by the objective.
     *
     * @param board The board status.
     * @param boardRes The board's resources.
     * @return The points gained by the objective.
     */
    public int countObjectivePoints(Card[][] board, Map<Symbol, Integer> boardRes) throws ArithmeticException {
        Symbol s = (Symbol) this.resources.keySet().toArray()[0];
        int min = boardRes.get(s) / this.resources.get(s);
        for (Map.Entry<Symbol, Integer> e : this.resources.entrySet()) {
            if (boardRes.get(e.getKey()) / this.resources.get(e.getKey()) < min) {
                min = boardRes.get(e.getKey()) / this.resources.get(e.getKey());
            }
        }
        return min * getPoints();
    }

    /**
     * Gets the resources.
     *
     * @return A map containing the resources.
     */

    public Map<Symbol, Integer> getResources() {
        return new HashMap<>(this.resources);
    }

    /**
     * Returns a string representation of the objective.
     *
     * @return A string representation of the objective.
     */
    @Override
    public String toString() {
        return "ID: " + this.getId() +
                "\nResources: " + this.resources.toString() +
                "\nPoints: " + this.getPoints();
    }
}