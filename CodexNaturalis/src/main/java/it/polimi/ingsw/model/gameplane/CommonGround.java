package it.polimi.ingsw.model.gameplane;


import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.card.GoldCard;
import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.model.util.GoldCardsFactory;
import it.polimi.ingsw.model.util.ObjFactory;
import it.polimi.ingsw.model.util.ResCardFactory;
import it.polimi.ingsw.model.util.exception.InvalidDrawException;
import it.polimi.ingsw.model.util.exception.InvalidSelectionException;

import java.io.IOException;
import java.util.List;

/**
 * Represents the common area for the game.
 */

public class CommonGround {
    private Deck<GoldCard> goldDeck;
    private Deck<ResourceCard> resDeck;
    private ResourceCard[] groundResCards;
    private GoldCard[] groundGoldCards;
    private Objective[] commonObjectives;
    private List<Objective> objectives;

    /**
     * Construct a new instance of CommonGround initializing decks and objectives.
     * @throws IOException if an input error occurs in the reading of the JSON files.
     */
    public CommonGround() throws IOException {
        this.setDecks();
        this.setObjectives();
    }

    /**
     * Retrieves the common objectives.
     * @return the common objectives.
     */
    public Objective[] getCommonObjectives() {
        return commonObjectives.clone();
    }

    /**
     *  Draws a card from the specified collection and replaces it if drawn from the ground.
     * @param sel the index for choosing the collection.
     * @return the drawn card.
     * @throws InvalidDrawException if the selected deck is empty.
     * @throws InvalidSelectionException if the sel parameter isn't from the ones expected.
     */
    public DrawableCard draw(int sel) throws InvalidDrawException,InvalidSelectionException{
        DrawableCard tmp;
        switch (sel) {
            case 0 -> {
                tmp = resDeck.draw();
            }
            case 1 -> {
                tmp = groundResCards[0];
                groundResCards[0] = resDeck.draw();
            }
            case 2 -> {
                tmp = groundResCards[1];
                groundResCards[1] = resDeck.draw();
            }
            case 3 -> {
                tmp = goldDeck.draw();
            }
            case 4 -> {
                tmp = groundGoldCards[0];
                groundGoldCards[0] = goldDeck.draw();
            }
            case 5 -> {
                tmp = groundGoldCards[1];
                groundGoldCards[1] = goldDeck.draw();
            }

            default -> throw new InvalidSelectionException("Invalid Selection");
        }
        if(tmp==null)
            throw new InvalidDrawException("Deck Empty");
        return tmp;
    }

    /**
     * Sets up the deck and initial cards on the ground.
     * @throws IOException if there's a problem while reading the JSON files
     */
    private void setDecks() throws IOException,NullPointerException{
        deckGenerator();
        setGroundCards();
    }

    private void deckGenerator() throws IOException,NullPointerException {
        resDeck = new Deck<>(new ResCardFactory().createItemsList());
        goldDeck = new Deck<>(new GoldCardsFactory().createItemsList());
        resDeck.shuffle();
        goldDeck.shuffle();
    }


    private void setGroundCards(){
        this.groundResCards = new ResourceCard[2];
        this.groundGoldCards = new GoldCard[2];
        groundResCards[0] = resDeck.draw();
        groundResCards[1] = resDeck.draw();
        groundGoldCards[0] = goldDeck.draw();
        groundGoldCards[1] = goldDeck.draw();
    }

    /**
     * Checks if the correspondent deck is empty.
     * @param sel the integer used for selecting a deck.
     * @return true if the deck is empty, false otherwise.
     * @throws InvalidSelectionException if sel doesn't correspond to any deck.
     */
    public boolean isEmpty(int sel) throws InvalidSelectionException{
        switch (sel){
            case 0 -> {return resDeck.getSize()==0;}
            case 1 -> {return goldDeck.getSize()==0;}
            default -> throw new InvalidSelectionException("Invalid Selection");
        }
    }

    /**
     * Gets a random objective from the objectives list and removes it from the list
     * @return the random objective
     */
    public Objective getRandomObjective(){
        int idx = (int)Math.floor(Math.random()*(objectives.size()));
        return objectives.remove(idx);
    }


    /**
     * Construct the list of objectives and sets the common ones on the ground.
     */
    private void setObjectives() throws IOException,NullPointerException {
        constructObjective();
        commonObjectives = new Objective[]{getRandomObjective(),getRandomObjective()};
    }

    private void constructObjective() throws IOException,NullPointerException {
        this.objectives = new ObjFactory().createItemsList();
    }
}
