package it.polimi.ingsw.util.json;

import com.google.gson.*;
import it.polimi.ingsw.model.item.card.*;

import java.lang.reflect.Type;

public class CardSerializeLogic implements JsonDeserializer<Card>,JsonSerializer<Card>{

    /**
     * Deserialize a JSON representation of a card into a Card object.
     *
     * @param jsonElement The JSON data to deserialize.
     * @param type The type of the target object (e.g., StartingCard, ResourceCard).
     * @param jsonDeserializationContext The context for deserialization.
     * @return A Card object representing the deserialized data.
     * @throws JsonParseException If the type is not defined.
     */
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

    /**
     * Serialize a Card object into its JSON representation.
     *
     * @param card The Card object to serialize.
     * @param type The type of the target object (e.g., StartingCard, ResourceCard).
     * @param jsonSerializationContext The context for serialization.
     * @return A JsonElement representing the serialized data.
     */
    @Override
    public JsonElement serialize(Card card, Type type, JsonSerializationContext jsonSerializationContext) {
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(card);
        JsonObject object = element.getAsJsonObject();
        object.addProperty("type",card.getClass().getSimpleName());
        return object;
    }
}
