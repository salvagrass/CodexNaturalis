package it.polimi.ingsw.model.gameplane;

import it.polimi.ingsw.model.item.card.DrawableCard;

import java.util.List;

/**
 * Class representing ground decks in the game.
 * A deck is described as a collection of DrawableCard objects.
 */
public class Deck <T extends DrawableCard>{
    List<T> deck;

    /**
     * Construct a deck using the provided list of cards
     * @param deck the list of cards to be used for creating the deck
     */
    public Deck(List<T> deck){
        this.deck = deck;
    }

    /**
     * Shuffles the deck using the modern approach of the Fisher-Yates algorithm,
     * ensuring a uniform distribution of permutations.
     */
    public void shuffle() {
        int i, j;
        T tmp;
        for (i = deck.size() - 1; i > 0; i--) {
            j = (int) Math.floor(Math.random() * (i + 1));
            tmp = deck.get(j);
            deck.set(j, deck.get(i));
            deck.set(i, tmp);
        }
    }

    /**
     * Retrieves and removes the last card from the deck.
     *
     * @return The last card of the deck.
     */
    public T draw(){
        if(deck.isEmpty())
            return null;
        return deck.removeLast();
    }

    /**
     * Retrieves the size of the deck
     * @return the deck size as an int
     */
    public int getSize(){
        return deck.size();
    }
}

