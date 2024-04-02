package it.polimi.ingsw;

import java.io.IOException;
import java.util.List;

/**
 * Class to implement the factory design pattern for items in the game.
 * This class serves as an abstract base for concrete item factories.
 *
 * @param <T> the type of item of the game to be constructed.
 */
public abstract class ItemFactory<T> {
    /**
     * Abstract method used to construct the item in the real implementation.
     *
     * @return the created item.
     * @throws IOException if an I/O error occurs while reading the item data.
     * @throws NullPointerException if the item data is null or invalid.
     */
    public abstract List<T> createDeck() throws IOException, NullPointerException;
}
