package it.polimi.ingsw.model.item;

import it.polimi.ingsw.model.utils.Symbol;

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
     */
    public DispObjective(int p, Disposition d) {
        super(p);
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
}