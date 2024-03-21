package it.polimi.ingsw.card;
import it.polimi.ingsw.Symbol;

/**
 * Enumeration containing data for every resource card to be instantiated during the game.
 */
public enum ResourceCardData {
    //istanze da aggiungere
    //formato:
    //ISTANZA1(frontCorners, center, cardPoints), ISTANZA2(frontCorners, center, cardPoints), ...;
    //es.
    ISTANZA1(new Symbol[] { Symbol.EMPTY, Symbol.EMPTY, Symbol.FUNGUS, Symbol.EMPTY},
            new Symbol[] {Symbol.BUTTERFLY},
            0);
    private Symbol[] frontCorners;
    private Symbol[] center;
    private boolean showingFace;
    private int cardPoints;

    /**
     * Enum constructor.
     *
     * @param frontCorners An array of Symbol representing the corners of the card.
     * @param center An array of Symbol representing the center of the card.
     * @param cardPoints The points given by the card upon placement.
     */
    private ResourceCardData(Symbol[] frontCorners, Symbol[] center, int cardPoints) {
        this.frontCorners = frontCorners;
        this.center = center;
        this.showingFace = false;
        this.cardPoints = cardPoints;
    }

    /**
     * Returns a copy of the frontCorners array.
     *
     * @return A copy of the frontCorners array.
     */
    public Symbol[] getFrontCorners() {
        return this.frontCorners.clone();
    }

    /**
     * Returns a copy of the center array.
     *
     * @return A copy of the center array.
     */
    public Symbol[] getCenter() {
        return this.center.clone();
    }

    /**
     * Returns true if the showing face is the front one; otherwise, returns false.
     *
     * @return true if the showing face is the front one; false otherwise.
     */
    public boolean getShowingFace() {
        return this.showingFace;
    }

    /**
     * Retrieves the points assigned to the card.
     *
     * @return The number of points assigned to the card.
     */
    public int getCardPoints() {
        return this.cardPoints;
    }
}
