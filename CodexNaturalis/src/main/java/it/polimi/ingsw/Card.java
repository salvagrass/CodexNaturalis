package it.polimi.ingsw;
import it.polimi.ingsw.Symbol;

/**
 * Abstract class representing cards in the game. This class is intended to be extended by specific card types.
 */
public abstract class Card {
    Symbol[] frontCorners;
    Symbol[] center;
    boolean showingFace;

    /**
     * Getter of the showingFace attrib
     * @return true if the showing face of the card is the front one, false otherwise
     */
    public boolean getShowingFace() {
        return this.showingFace;
    }

    /**
     * Setter of the showingFace param
     * @param newVal is true if I want to set as showing face the front one, false otherwise
     */
    public void setShowingFace(boolean newVal) {
        this.showingFace = newVal;
    }

    /**
     * Getter method for the showingCorners attribute.
     * Returns a copy of the frontCorners attribute if the showing face is the front one,
     * or an array of it.polimi.ingsw.Symbol.EMPTY otherwise,
     * because only the starting cards can have symbols on the back.
     * @return a copy of the attribute if the showing face is the front one,
     *         or an array of it.polimi.ingsw.Symbol.EMPTY otherwise.
     */
    public Symbol[] getShowingCorners() {
        if (getShowingFace()) {
            return this.frontCorners.clone();
        }
        return new Symbol[] { Symbol.EMPTY, Symbol.EMPTY, Symbol.EMPTY, Symbol.EMPTY };
    }

    /**
     * Getter method for the Center attribute.
     * Returns an empty array (length = 0) if the showing face is the front one because there's no card with a symbol in the front face's center.
     * Returns a copy of the Center attribute otherwise because that tuple of symbols represents useful resources for card requirements.
     *
     * @return An empty array (length = 0) if the showing face is the front one.
     *         A copy of the Center attribute otherwise.
     */
    public Symbol[] getCenter() {
        if (getShowingFace()) {
            return new Symbol[] {};
        }
        return this.center.clone();
    }
}