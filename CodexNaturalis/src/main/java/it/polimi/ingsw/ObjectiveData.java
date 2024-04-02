package it.polimi.ingsw;

import it.polimi.ingsw.model.item.DiagonalType;
import it.polimi.ingsw.model.item.Disposition;
import it.polimi.ingsw.model.item.LType;

/**
 * Enumeration with all the data for the different objectives
 */
public enum ObjectiveData {
    O1(ObjectiveType.DISPOSITION, 2, new DiagonalType(Symbol.FUNGUS, -1, 3)),
    O2(ObjectiveType.DISPOSITION, 2, new DiagonalType(Symbol.GRASS, 1, 3)),
    O3(ObjectiveType.DISPOSITION, 2, new DiagonalType(Symbol.WOLF, -1, 3)),
    O4(ObjectiveType.DISPOSITION, 2, new DiagonalType(Symbol.BUTTERFLY, -1, 3)),
    O5(ObjectiveType.DISPOSITION, 3, new LType(Symbol.FUNGUS, Symbol.GRASS, 2)),
    O6(ObjectiveType.DISPOSITION, 3, new LType(Symbol.GRASS, Symbol.BUTTERFLY, 3)),
    O7(ObjectiveType.DISPOSITION, 3, new LType(Symbol.WOLF, Symbol.FUNGUS, 1)),
    O8(ObjectiveType.DISPOSITION, 3, new LType(Symbol.BUTTERFLY, Symbol.WOLF, 0)),
    O9(ObjectiveType.RESOURCE, 2, new Symbol[] { Symbol.FUNGUS }),
    O10(ObjectiveType.RESOURCE, 2, new Symbol[] { Symbol.GRASS }),
    O11(ObjectiveType.RESOURCE, 2, new Symbol[] { Symbol.WOLF }),
    O12(ObjectiveType.RESOURCE, 2, new Symbol[] { Symbol.BUTTERFLY }),
    O13(ObjectiveType.RESOURCE, 3, new Symbol[] { Symbol.POTION, Symbol.SCROLL, Symbol.FEATHER }),
    O14(ObjectiveType.RESOURCE, 2, new Symbol[] { Symbol.SCROLL, Symbol.SCROLL }),
    O15(ObjectiveType.RESOURCE, 2, new Symbol[] { Symbol.POTION, Symbol.POTION }),
    O16(ObjectiveType.RESOURCE, 2, new Symbol[] { Symbol.FEATHER, Symbol.FEATHER });

    private ObjectiveType type;
    private int points;
    private Symbol[] resources;
    private Disposition disp;

    /**
     * Retrieves the type of the objective
     *
     * @return the type of the objective
     */
    public ObjectiveType getType() {
        return type;
    }

    /**
     * Retrieves the points associated with the objective.
     *
     * @return The points associated with the objective.
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Retrieves the symbols representing requirements (if there's any) for the
     * objective.
     *
     * @return An array containing the symbols representing requirements for the
     *         objective.
     */
    public Symbol[] getResources() {
        return this.resources.clone();
    }
    /**
     * Returns the correct disposition for the objective.
     *
     * @return The disposition associated with the objective.
     */
    public Disposition getDisp() {
        return this.disp;
    }

    /**
     * Constructs an `ObjectiveData` object with the specified points.
     *
     * @param points The points associated with this objective.
     */
    ObjectiveData(int points) {
        this.points = points;
    }
    /**
     * Constructs an `ObjectiveData` object with the specified points, type, and resources.
     *
     * @param points    The points associated with this objective.
     * @param type      The type of objective.
     * @param resources An array of symbols representing the resources required for this objective.
     */
    ObjectiveData(ObjectiveType type, int points, Symbol[] resources) {
        this(points);
        this.type = type;
        this.resources = resources;
        this.disp = null;
    }
    /**
     * Constructs an `ObjectiveData` object with the specified points, type, and disposition.
     *
     * @param points    The points associated with this objective.
     * @param type      The type of objective.
     * @param disp      The disposition associated with this objective.
     */
    ObjectiveData(ObjectiveType type, int points, Disposition disp) {
        this(points);
        this.type = type;
        this.resources = null;
        this.disp = disp;
    }
}
