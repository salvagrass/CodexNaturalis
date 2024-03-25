package it.polimi.ingsw;

/**
 * Class representing gold cards that aim to cover corners during the game.
 */
public class GoldCardCorner extends GoldCard {

    /**
     * Constructs a card using information provided by the specified enum.
     *
     * @param e An enum containing all the necessary information to create the card.
     */
    public GoldCardCorner(CardData e){
        super(e);
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
     * @param g The state of the game board after placing the card.
     * @param x The x-coordinate of the card placement.
     * @param y The y-coordinate of the card placement.
     * @return The points obtained by placing the card.
     */
    public int givePoints(GameBoard g, int x, int y) {
        int points = 0;
        int i, j;
        int l1, l2;
        Card[][] board = g.getBoard();
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
