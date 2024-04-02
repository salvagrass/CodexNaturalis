package it.polimi.ingsw.model.item;

import it.polimi.ingsw.Symbol;

/**
 * Class representing starting cards in the game.
 */
public class StartingCard extends Card {
    private final Symbol[] backCorners;

    /**
     * Creates a card with the specified corners and center.
     *
     * @param corners The symbols representing the front corners of the card.
     * @param center  The symbols representing the center of the card.
     */
    public StartingCard(Symbol[] corners, Symbol[] center, Symbol[] back) {
        super(corners, center);
        this.backCorners = back;
    }

    /**
     * Getter method for the showingCorners attribute.
     * Returns a copy of the frontCorners attribute if the showing face is the front one,
     * or a copy of the backCorners attribute otherwise.
     *
     * @return A copy of the frontCorners attribute if the showing face is the front one.
     *         A copy of the backCorners attribute otherwise.
     */
    public Symbol[] getShowingCorners() {
        if (getShowingFace()) {
            return super.getShowingCorners();
        }
        return this.backCorners.clone();
    }

    /**
     * Retrieves the color of the card.
     *
     * @return Symbol.NOTAVAILABLE because startingCards don't have a color.
     */
    public Symbol getColor(){
        return Symbol.NOTAVAILABLE;
    }
}