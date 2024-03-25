package it.polimi.ingsw;

/**
 * Class representing resource cards in the game.
 */
public class ResourceCard extends DrawableCard {

    /**
     * Constructs a card using information provided by the specified enum.
     *
     * @param e An enum containing all the necessary information to create the card.
     */
    public ResourceCard(CardData e) {
        super(e.getFrontCorners(), e.getCenter(), e.getCardPoints());
    }
}