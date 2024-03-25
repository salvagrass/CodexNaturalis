package it.polimi.ingsw;

/**
 * Class representing objectives in the game.
 * This class is intended to be extended by specific types of objectives.
 */
public abstract class Objective {
    final private int points;

    /**
     * Creates an objective with the specified points.
     *
     * @param p The number of points of the objective.
     */
    public Objective(int p) {
        this.points = p;
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
     * @param g The board status.
     * @return The points given by the objective.
     */
    public abstract int countObjectivePoints(GameBoard g);
}
