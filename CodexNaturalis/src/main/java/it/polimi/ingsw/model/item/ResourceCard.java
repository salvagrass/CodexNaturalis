package it.polimi.ingsw.model.item;

import it.polimi.ingsw.model.util.Symbol;

/**
 * Class representing resource cards in the game.
 */
public class ResourceCard extends DrawableCard {

    /**
     * Creates a card with the specified corners, center and points.
     *
     * @param corners The symbols representing the front corners of the card.
     * @param center  The symbols representing the center of the card.
     * @param p       The number of points of the card
     */
    public ResourceCard(Symbol[] corners, Symbol[] center, int p) {
        super(corners, center, p);
    }
}