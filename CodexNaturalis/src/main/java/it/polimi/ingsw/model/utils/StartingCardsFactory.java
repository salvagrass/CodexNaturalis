package it.polimi.ingsw.model.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.item.StartingCard;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

/**
 * Concrete implementation of the factory design pattern for creating a list of starting cards.
 */
public class StartingCardsFactory extends ItemFactory<StartingCard> {

    /**
     * Creates a list of StartingCards by reading data from a JSON file.
     *
     * @return the created list of StartingCards.
     * @throws IOException if an I/O error occurs while reading the JSON file.
     * @throws NullPointerException if the JSON file is not found or the data is invalid.
     */
    @Override
    public List<StartingCard> createItemsList() throws IOException, NullPointerException {
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(StartingCardsFactory.class.getResourceAsStream("/data/StartCard.json")));
        List<StartingCard> startingCards = new Gson().fromJson(reader, new TypeToken<List<StartingCard>>(){}.getType());
        reader.close();
        return startingCards;
    }
}
