package it.polimi.ingsw.model.item.card;

import it.polimi.ingsw.util.Symbol;

/**
 * Abstract class representing cards in the game. This class is intended to be extended by specific card types.
 */
public abstract class Card {
    private final Symbol[] frontCorners;
    private final Symbol[] center;
    private boolean showingFace;
    private final String id;


    /**
     * Creates a card with the specified corners and center.
     *
     * @param corners The symbols representing the front corners of the card.
     * @param center The symbols representing the center of the card.
     * @param id A unique identifier for a card.
     */
    public Card(Symbol[] corners, Symbol[] center, String id){
        this.center = center;
        this.frontCorners = corners;
        this.showingFace = false;
        this.id = id;
    }

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
     * or an array of Symbol.EMPTY otherwise,
     * because only the starting cards can have symbols on the back.
     * @return a copy of the attribute if the showing face is the front one,
     *         or an array of Symbol.EMPTY otherwise.
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

    /**
     * Retrieves the color of the card.
     *
     * @return The color of the card.
     */
    public Symbol getColor(){
        return this.center[0];
    }

    /**
     * Retrieves the id of the card.
     *
     * @return The id of the card.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns a string representation of the object.
     * Is intended to be overridden by specific card types.
     *
     * @return A string representation of the object.
     */
    public String toString(){
        StringBuilder s = new StringBuilder("[" + this.center[0]);
        for(int i = 1; i < this.center.length; i++){
            s.append(",").append(this.center[i]);
        }
        s.append("]");
        return "ID: " + this.getId() +
                "\nShowing_Face: " + (this.getShowingFace() ? "Front" : "Back") +
                "\nFront_Corners: [" +
                this.frontCorners[0].toString() + "," +
                this.frontCorners[1].toString() + "," +
                this.frontCorners[2].toString() + "," +
                this.frontCorners[3].toString() + "]" +
                "\nCenter: " + s;
    }
}