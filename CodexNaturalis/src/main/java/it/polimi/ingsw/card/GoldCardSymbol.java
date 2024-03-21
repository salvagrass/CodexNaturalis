package it.polimi.ingsw.card;
import it.polimi.ingsw.Symbol;

/**
 * Class representing gold cards that give points based on the number of appearances of a specific symbol in the game.
 */
public class GoldCardSymbol extends GoldCard {
    final Symbol sym;

    /**
     * Constructs a card using information provided by the specified enum.
     *
     * @param e An enum containing all the necessary information to create the card.
     */
    public GoldCardSymbol(GoldCardSymbolData e) {
        this.frontCorners = e.getFrontCorners();
        this.center = e.getCenter();
        this.showingFace = e.getShowingFace();
        this.cardPoints = e.getCardPoints();
        this.requirements = e.getRequirements();
        this.sym = e.getSymbol();
    }

    public String getPoints() {
        return this.cardPoints + " " + sym;
    }

    /**
     * Returns the points awarded when placing the card face-up;
     * returns 0 otherwise since cards do not have points on their back.
     * Returns the number of points given for a symbol multiplied by the number of appearances of that symbol.
     *
     * @param g The state of the game board after placing the card.
     * @param x The x-coordinate of the card placement.
     * @param y The y-coordinate of the card placement.
     * @return The points obtained by placing the card.
     */
    public int givePoints(GameBoard g, int x, int y) {
        if (!getShowingFace()) {
            return 0;
        }
        return g.getCountResources().get(sym) * this.cardPoints;
    }
}