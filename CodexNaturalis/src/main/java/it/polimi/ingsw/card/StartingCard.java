package it.polimi.ingsw.card;
import it.polimi.ingsw.Symbol;

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
    public StartingCard(StartingEnum e) {
        this.frontCorners = e.getFrontCorners();
        this.backCorners = e.getBackCorners();
        this.center = e.getCenter();
        this.showingFace = e.getShowingFace();
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
            return this.frontCorners.clone();
        }
        return this.backCorners.clone();
    }
}