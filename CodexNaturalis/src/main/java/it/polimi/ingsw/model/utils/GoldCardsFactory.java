package it.polimi.ingsw.model.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.item.GoldCard;
import it.polimi.ingsw.model.item.GoldCardCorner;
import it.polimi.ingsw.model.item.GoldCardSymbol;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

/**
 * Concrete implementation of the factory design pattern for creating a list of GoldCards.
 */
public class GoldCardsFactory extends ItemFactory<GoldCard> {

    /**
     * Creates a list of GoldCards by reading data from JSON files.
     *
     * @return the created list of GoldCards.
     * @throws IOException if an I/O error occurs while reading the JSON files.
     * @throws NullPointerException if any of the JSON files are not found or the data is invalid.
     */
    @Override
    public List<GoldCard> createItemsList() throws IOException,NullPointerException {
        Gson gson = new Gson();
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(GoldCardsFactory.class.getResourceAsStream("/data/GoldCard.json")));
        List<GoldCard> goldCardList = gson.fromJson(reader,new TypeToken<List<GoldCard>>(){}.getType());
        reader.close();
        reader = new InputStreamReader(Objects.requireNonNull(GoldCardsFactory.class.getResourceAsStream("/data/GoldCardCorner.json")));
        goldCardList.addAll(gson.fromJson(reader,new TypeToken<List<GoldCardCorner>>(){}.getType()));
        reader.close();
        reader = new InputStreamReader(Objects.requireNonNull(GoldCardsFactory.class.getResourceAsStream("/data/GoldCardSymbol.json")));
        goldCardList.addAll(gson.fromJson(reader,new TypeToken<List<GoldCardSymbol>>(){}.getType()));
        reader.close();
        return goldCardList;
    }
}
