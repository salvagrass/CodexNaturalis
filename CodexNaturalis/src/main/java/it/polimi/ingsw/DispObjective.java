package it.polimi.ingsw;

/**
 * Class representing objectives that require a specified disposition in the game.
 */
public class DispObjective extends Objective {
    final private Disposition disp;

    /**
     * Constructs an objective using information provided by the specified enum.
     *
     * @param o An enum containing all the necessary information to create the Disposition objective.
     */
    public DispObjective(ObjectiveData o) {
        super(o.getPoints());
        disp = o.getDisp();
    }

    /**
     * Calculates the number of points obtained with the given board status.
     *
     * @param g The board status.
     * @return The number of points obtained with the specified board status.
     */
    public int countObjectivePoints(GameBoard g) {
        return getPoints() * disp.checkDisposition(g);
    }
}