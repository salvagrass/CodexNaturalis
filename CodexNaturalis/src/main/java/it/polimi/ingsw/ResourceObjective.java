package it.polimi.ingsw;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing objectives that require a specified number of symbols in the game.
 */
public class ResourceObjective extends Objective {
    final private Map<Symbol, Integer> resources;

    /**
     * Constructs an objective using information provided by the specified enum.
     *
     * @param o An enum containing all the necessary information to create the resource objective.
     */
    public ResourceObjective(ObjectiveData o) {
        super(o.getPoints());
        this.resources = new HashMap<>();
        setResources(o.getResources());
    }

    /**
     * Takes an array of symbols and creates a map associating each symbol with its
     * number of appearances.
     * The resulting map is stored in the class attribute.
     *
     * @param res The array of symbols.
     */
    private void setResources(Symbol[] res) {
        for (Symbol s : res) {
            if (this.resources.containsKey(s)) {
                this.resources.put(s, this.resources.get(s) + 1);
            } else {
                this.resources.put(s, 1);
            }
        }
    }

    /**
     * Counts the amount of times each symbol of the objective appears in the game board resources,
     * normalises it based on the correspondent number in the objective map, and selects the minimum of these values.
     * This minimum value is then multiplied by the card points to determine the points gained by the objective.
     *
     * @param g The board status.
     * @return The points gained by the objective.
     */
    public int countObjectivePoints(GameBoard g) {
        Map<Symbol, Integer> tmp = g.getCountResources();
        Symbol s = (Symbol) resources.keySet().toArray()[0];
        int min = tmp.get(s)/resources.get(s);
        for (Map.Entry<Symbol, Integer> e : resources.entrySet()) {
            if (tmp.get(e.getKey()) / resources.get(e.getKey()) < min) {
                min = tmp.get(e.getKey()) / resources.get(e.getKey());
            }
        }
        return min * getPoints();
    }
}