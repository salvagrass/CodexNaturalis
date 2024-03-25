package it.polimi.ingsw;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * class that represents the common area where the decks and the ground cards are
 */

public class CommonGround {
    Deck resDeck, goldDeck;
    ResourceCard[] groundResCards;
    GoldCard[] groundGoldCards;
    Objective[] commonObjectives;
    List<Objective> objectives;

    /**
     * Retrieves the common objectives
     * @return the common objectives
     */
    public Objective[] getCommonObjectives() {
        return commonObjectives;
    }

    /**
     * Draws a card from the specified collection, if the card was drawn from the ground, replaces it
     * @param sel the index for choosing a collection
     * @return the drawn card
     */
    public DrawableCard draw(int sel){
        DrawableCard tmp;
        switch(sel){
            case 0 -> {return resDeck.draw();}
            case 1 -> {
                tmp = groundResCards[0];
                groundResCards[0] = (ResourceCard) resDeck.draw();
                return tmp;
            }
            case 2 -> {
                tmp = groundResCards[1];
                groundResCards[1] = (ResourceCard) resDeck.draw();
                return tmp;
            }
            case 3 -> { return goldDeck.draw();}
            case 4 -> {
                tmp = groundGoldCards[0];
                groundGoldCards[0] = (GoldCard) goldDeck.draw();
                return tmp;
            }
            case 5 -> {
                tmp = groundGoldCards[1];
                groundGoldCards[1] = (GoldCard) goldDeck.draw();
                return tmp;
            }
            default -> {return null;}
        }
    }

    /**
     * Sets up the decks and the initial cards on the ground
     */
    public void setDecks(){
        deckGenerator();
        setGroundCards();
    }

    private void deckGenerator(){
        Map<CardType,Integer> counters = CardData.getCounters();
        this.resDeck = new Deck(counters.get(CardType.RESOURCE));
        this.goldDeck = new Deck(counters.get(CardType.GOLD)+counters.get(CardType.GOLDCORNER)+counters.get(CardType.GOLDSYMBOL));

        for(CardData data : CardData.values()){
            switch(data.getType()){
                case CardType.RESOURCE -> resDeck.add(new ResourceCard(data));
                case CardType.GOLD -> goldDeck.add(new GoldCard(data));
                case CardType.GOLDCORNER -> goldDeck.add(new GoldCardCorner(data));
                case CardType.GOLDSYMBOL -> goldDeck.add(new GoldCardSymbol(data));
            }
        }
    }


    private void setGroundCards(){
        this.groundResCards = new ResourceCard[2];
        this.groundGoldCards = new GoldCard[2];
        groundResCards[0] = (ResourceCard) resDeck.draw();
        groundResCards[1] = (ResourceCard) resDeck.draw();
        groundGoldCards[0] = (GoldCard) goldDeck.draw();
        groundGoldCards[1] = (GoldCard) goldDeck.draw();
    }

    /**
     * checks if both decks are empty
     * @return true if both decks are empty, false otherwise
     */
    public boolean areEmpty(){
        return (resDeck.getSize()==0 && goldDeck.getSize()==0);
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
     * Construct the list of objectives and sets the common one on the ground
     */
    public void setObjectives(){
        constructObjective();
        commonObjectives = new Objective[]{getRandomObjective(),getRandomObjective()};
    }

    private void constructObjective(){
        objectives = new ArrayList<>(ObjectiveData.values().length);

        for(ObjectiveData data: ObjectiveData.values()){
            switch(data.getType()){
                case ObjectiveType.RESOURCE -> objectives.add(new ResourceObjective(data));
                case ObjectiveType.DISPOSITION -> objectives.add(new DispObjective(data));
            }
        }
    }
}
