package it.polimi.ingsw.model.item;

import it.polimi.ingsw.model.utils.Symbol;

import java.util.Map;

/**
 * Class representing gold cards that aim to cover corners during the game.
 */
public class GoldCardCorner extends GoldCard {

    /**
     * Constructs a GoldCard with the specified front corners, center, requirements, and points.
     *
     * @param frontCorners The symbols representing the front corners of the card.
     * @param center       The symbols representing the center of the card.
     * @param requirements A map containing how many of each symbol are required from the card.
     * @param points       The number of points awarded by the card.
     */
    public GoldCardCorner(Symbol[] frontCorners, Symbol[] center, Map<Symbol, Integer> requirements, int points) {
        super(frontCorners, center, requirements, points);
    }

    public String getPoints() {
        return getCardPoints() + " EACH CORNER";
    }

    /**
     * Returns the points awarded when placing the card face-up;
     * returns 0 otherwise since cards do not have points on their back.
     * Counts the number of adjacent positions occupied by a card,
     * which represents the number of corners covered by the last card placed,
     * and returns the product of this number multiplied by the points the card
     * gives for each corner.
     *
     * @param board The state of the game board after placing the card.
     * @param resources The resources of the board after placing the card.
     * @param x The x-coordinate of the card placement.
     * @param y The y-coordinate of the card placement.
     * @return The points obtained by placing the card.
     */
    public int givePoints(Card[][] board, Map<Symbol, Integer> resources, int x, int y) {
        int points = 0;
        int i, j;
        int l1, l2;
        if (!getShowingFace()) {
            return 0;
        }
        l1 = board.length;
        l2 = board[0].length;
        for (i = x - 1 >= 0 ? x - 1 : x + 1; i <= x + 1 && i < l1; i += 2) {
            for (j = y - 1 >= 0 ? y - 1 : y + 1; j <= y + 1 && j < l2; j += 2) {
                points += board[i][j] != null ? 1 : 0;
            }
        }
        return points * getCardPoints();
    }
}
