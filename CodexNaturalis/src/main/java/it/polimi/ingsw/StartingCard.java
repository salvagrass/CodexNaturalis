package it.polimi.ingsw;

/**
 * Class representing starting cards in the game.
 */
public class StartingCard extends Card {
    private final Symbol[] backCorners;

    /**
     * Constructs a card using information provided by the specified enum.
     *
     * @param e An enum containing all the necessary information to create the card.
     */
    public StartingCard(CardData e) {
        super(e.getFrontCorners(), e.getCenter());
        this.backCorners = e.getBackCorners();
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