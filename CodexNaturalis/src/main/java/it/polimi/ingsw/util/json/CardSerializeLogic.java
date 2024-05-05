package it.polimi.ingsw.util.json;

import com.google.gson.*;
import it.polimi.ingsw.model.item.card.*;

import java.lang.reflect.Type;

public class CardSerializeLogic implements JsonDeserializer<Card>,JsonSerializer<Card>{

    @Override
    public Card deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String cardType = object.get("type").getAsString();
        Type rtr = switch(cardType){
            case "StartingCard" -> StartingCard.class;
            case "ResourceCard" -> ResourceCard.class;
            case "GoldCard" -> GoldCard.class;
            case "GoldCardCorner" -> GoldCardCorner.class;
            case "GoldCardSymbol" -> GoldCardSymbol.class;
            default -> throw new JsonParseException("Type not defined");
        };
        return (new Gson()).fromJson(jsonElement,rtr);
    }

    @Override
    public JsonElement serialize(Card card, Type type, JsonSerializationContext jsonSerializationContext) {
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(card);
        JsonObject object = element.getAsJsonObject();
        object.addProperty("type",card.getClass().getSimpleName());
        return object;
    }
}
