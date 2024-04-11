package it.polimi.ingsw.model.item.objective;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.util.Symbol;

import java.util.Map;

/**
 * Class representing objectives in the game.
 * This class is intended to be extended by specific types of objectives.
 */
public abstract class Objective {
    final private int points;
    final private String id;

    /**
     * Creates an objective with the specified points.
     *
     * @param p The number of points of the objective.
     * @param id A unique identifier for a objective.
     */
    public Objective(int p, String id) {
        this.points = p;
        this.id = id;
    }

    /**
     * Retrieves the points assigned to the objective.
     *
     * @return The number of points assigned to the objective.
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Returns the points obtained with the specified objective and board status.
     *
     * @param board The board status.
     * @param resources The board's resources.
     * @return The points given by the objective.
     */
    public abstract int countObjectivePoints(Card[][] board, Map<Symbol, Integer> resources);

    /**
     * Retrieves the color of the objective.
     *
     * @return The color of the objective.
     */
    public String getId(){
        return this.id;
    }

    /**
     * Retrieves a string that contains information for the objective.
     *
     * @return The string that contains information for the objective.
     */
    public abstract String getInfo();
}
