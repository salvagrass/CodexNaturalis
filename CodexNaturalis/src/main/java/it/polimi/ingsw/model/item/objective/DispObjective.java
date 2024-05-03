package it.polimi.ingsw.model.item.objective;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.util.Symbol;

import java.util.Map;

/**
 * Class representing objectives that require a specified disposition in the game.
 */
public class DispObjective extends Objective {
    final private Disposition disposition;

    /**
     * Creates an objective with the specified points and disposition.
     *
     * @param p The number of points of the objective.
     * @param d The disposition required by the objective.
     * @param id A unique identifier for a objective.
     */
    public DispObjective(int p, String id, Disposition d) {
        super(p, id);
        disposition = d;
    }

    /**
     * Calculates the number of points obtained with the given board status.
     *
     * @param board The board status.
     * @param resources The board's resources.
     * @return The number of points obtained with the specified board status.
     */
    public int countObjectivePoints(Card[][] board, Map<Symbol, Integer> resources) {
        return getPoints() * disposition.checkDisposition(board);
    }

    /**
     * Returns a string representation of the objective.
     *
     * @return A string representation of the objective.
     */
    @Override
    public String toString() {
        return  "ID: " + this.getId() +
                "\nDisposition: " + this.disposition.toString() +
                "\nPoints: " + this.getPoints();
    }
}