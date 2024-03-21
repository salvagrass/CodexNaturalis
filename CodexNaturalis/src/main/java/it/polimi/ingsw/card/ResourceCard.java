package it.polimi.ingsw.card;

/**
 * Class representing resource cards in the game.
 */
public class ResourceCard extends DrawableCard {

    /**
     * Constructs a card using information provided by the specified enum.
     *
     * @param e An enum containing all the necessary information to create the card.
     */
    public ResourceCard(ResourceCardData e) {
        this.frontCorners = e.getFrontCorners();
        this.center = e.getCenter();
        this.showingFace = e.getShowingFace();
        this.cardPoints = e.getCardPoints();
    }
}