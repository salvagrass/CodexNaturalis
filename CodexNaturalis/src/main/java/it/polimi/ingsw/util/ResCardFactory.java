package it.polimi.ingsw.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.item.card.ResourceCard;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

/**
 * Concrete implementation of the factory design pattern for creating the list of resource cards
 */
public class ResCardFactory extends ItemFactory<ResourceCard> {

    /**
     * Creates a list of ResourceCards by reading data from a JSON file.
     *
     * @return the created list of ResourceCards.
     * @throws IOException if an I/O error occurs while reading the JSON file.
     * @throws NullPointerException if the JSON file is not found or the data is invalid.
     */
    @Override
    public List<ResourceCard> createItemsList() throws IOException,NullPointerException {
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(ResCardFactory.class.getResourceAsStream("/data/ResCard.json")));
        List<ResourceCard> resourceCards = new Gson().fromJson(reader,new TypeToken<List<ResourceCard>>(){}.getType());
        reader.close();
        return resourceCards;
    }
}
