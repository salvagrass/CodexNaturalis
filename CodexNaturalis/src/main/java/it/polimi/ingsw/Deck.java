package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Class representing ground decks in the game.
 * A deck is described as a collection of DrawableCard objects.
 */
public class Deck {
    ArrayList<DrawableCard> deck;

    /**
     * Constructs a deck with the specified dimension.
     *
     * @param dim The dimension of the deck.
     */
    public Deck(int dim) {
        deck = new ArrayList<DrawableCard>(dim);
    }

    /**
     * Adds a drawable card to the deck.
     *
     * @param c The drawable card to be added.
     */
    public void add(DrawableCard c) {
        deck.add(c);
    }

    /**
     * Shuffles the deck using the modern approach of the Fisher-Yates algorithm,
     * ensuring a uniform distribution of permutations.
     */
    public void shuffle() {
        int i, j;
        DrawableCard tmp;
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
    public DrawableCard draw() throws NoSuchElementException {
        return deck.removeLast();
    }
}

