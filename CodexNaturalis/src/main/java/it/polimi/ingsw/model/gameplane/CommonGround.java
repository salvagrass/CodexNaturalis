package it.polimi.ingsw.model.gameplane;


import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.card.GoldCard;
import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.GoldCardsFactory;
import it.polimi.ingsw.util.ObjFactory;
import it.polimi.ingsw.util.ResCardFactory;
import it.polimi.ingsw.util.StartingCardsFactory;
import it.polimi.ingsw.util.Symbol;
import it.polimi.ingsw.util.exception.InvalidDrawException;
import it.polimi.ingsw.util.exception.InvalidSelectionException;

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
    private List<StartingCard> startingCardList;

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
        return this.commonObjectives;
    }

    /**
     * Creates an array of strings with the information of the resource cards on the ground
     * @return the array with the cards info
     */
    public DrawableCard[] getGroundResourceCards(){
        return this.groundResCards;
    }

    /**
     * Creates an array of strings with the information of the gold cards on the ground
     * @return the array with the cards info
     */
    public DrawableCard[] getGroundGoldCards(){
        return this.groundGoldCards;
    }

    /**
     * Return symbol of the top element from the resource deck without removing it.
     *
     * @return The symbol of top element from the resource deck.
     */
    public Symbol peekResDeck(){
        return peek(resDeck);
    }

    /**
     * Return symbol of the top element from the gold deck without removing it.
     *
     * @return The symbol of top element from the gold deck.
     */
    public Symbol peekGoldDeck(){
        return peek(goldDeck);
    }

    /**
     * Retrieves the top card's symbol from the specified deck.
     *
     * @param deck The deck to peek into.
     * @return The symbol of the top card.
     */
    private <T extends DrawableCard> Symbol peek(Deck<T> deck){
        return deck.peek();  
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
            case 0 -> tmp = resDeck.draw();
            case 1 -> {
                tmp = groundResCards[0];
                groundResCards[0] = resDeck.draw();
            }
            case 2 -> {
                tmp = groundResCards[1];
                groundResCards[1] = resDeck.draw();
            }
            case 3 -> tmp = goldDeck.draw();
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

    /**
     * Generates and initializes the resource and gold decks, as well as the starting card list.
     *
     * @throws IOException If an I/O error occurs during deck creation.
     * @throws NullPointerException If any of the factories return a null list.
     */
    private void deckGenerator() throws IOException,NullPointerException {
        resDeck = new Deck<>(new ResCardFactory().createItemsList());
        goldDeck = new Deck<>(new GoldCardsFactory().createItemsList());
        startingCardList = new StartingCardsFactory().createItemsList();
        resDeck.shuffle();
        goldDeck.shuffle();
    }

    /**
     * Initializes the ground resource and gold cards by drawing from the decks.
     * Sets two resource cards and two gold cards for the ground.
     *
     */
    private void setGroundCards(){
        this.groundResCards = new ResourceCard[2];
        this.groundGoldCards = new GoldCard[2];
        try {
            groundResCards[0] = (ResourceCard) draw(0);
            groundResCards[1] =  (ResourceCard) draw(0);
            groundGoldCards[0] = (GoldCard) draw(3);
            groundGoldCards[1] = (GoldCard) draw(3);
        }catch(InvalidSelectionException | InvalidDrawException ignored){}
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
     * Gets a random objective from the objectives list and removes it from said list
     * @return the random objective
     */
    public Objective getRandomObjective(){
        return this.getRandomItem(objectives);
    }

    /**
     * Gets a random starting card from the starting cards list and removes it from said list
     * @return the random starting card
     */
    public StartingCard getRandomStartingCard(){
        return this.getRandomItem(startingCardList);
    }


    /**
     * Gets a random object from the list given as a parameter and deletes it from said list
     * @param list the list to retrieve the random object
     * @param <T> the generic parameter
     * @return the random item in the list
     */
    private <T> T getRandomItem(List<T> list){
        int idx = (int)Math.floor(Math.random()*(list.size()));
        return list.remove(idx);
    }


    /**
     * Construct the list of objectives and sets the common ones on the ground.
     */
    private void setObjectives() throws IOException,NullPointerException {
        this.objectives = new ObjFactory().createItemsList();
        commonObjectives = new Objective[]{getRandomObjective(),getRandomObjective()};
    }
}
