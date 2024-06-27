package it.polimi.ingsw.model.item.card;

import it.polimi.ingsw.util.Symbol;

import java.util.Map;

/**
 * Class representing gold cards that give points based on the number of appearances of a specific symbol in the game.
 */
public class GoldCardSymbol extends GoldCard {
    final private Symbol sym;

    /**
     * Constructs a GoldCard with the specified front corners, center, requirements, points, and symbol.
     *
     * @param frontCorners The symbols representing the front corners of the card.
     * @param center       The symbols representing the center of the card.
     * @param requirements A map containing how many of each symbol are required from the card.
     * @param points       The number of points awarded by the card.
     * @param sym          The symbol for which the card gives points.
     * @param id           A unique identifier for a card.
     */
    public GoldCardSymbol(Symbol[] frontCorners, Symbol[] center, Map<Symbol, Integer> requirements, int points, Symbol sym, String id) {
        super(frontCorners, center, requirements, points, id);
        this.sym = sym;
    }

    /**
     * Returns the points associated with the card.
     *
     * @return The card points followed by the symbol.
     */
    public String getPoints() {
        return getCardPoints() + " " + sym;
    }

    /**
     * Returns the points awarded when placing the card face-up;
     * returns 0 otherwise since cards do not have points on their back.
     * Returns the number of points given for a symbol multiplied by the number of appearances of that symbol.
     *
     * @param board The state of the game board after placing the card.
     * @param resources The resources of the board after placing the card.
     * @param x The x-coordinate of the card placement.
     * @param y The y-coordinate of the card placement.
     * @return The points obtained by placing the card.
     */
    public int givePoints(Card[][] board, Map<Symbol, Integer> resources, int x, int y) {
        if (!getShowingFace()) {
            return 0;
        }
        return resources.get(sym) * getCardPoints();
    }

    /**
     * Returns the symbol used to compute the points.
     *
     * @return Returns the symbol used to compute the points.
     */
    public Symbol getSym() {
        return this.sym;
    }
}