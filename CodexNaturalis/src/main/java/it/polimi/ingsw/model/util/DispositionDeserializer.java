package it.polimi.ingsw.model.util;

import com.google.gson.*;
import it.polimi.ingsw.model.item.objective.LType;
import it.polimi.ingsw.model.item.objective.DiagonalType;
import it.polimi.ingsw.model.item.objective.Disposition;

import java.lang.reflect.Type;

/**
 * Class to adapt gson deserializer to work with the Disposition interface
 */

public class DispositionDeserializer implements JsonDeserializer<Disposition> {
    /**
     * Deserializes a JSON element into a concrete implementation of the Disposition interface.
     *
     * @param jsonElement              the JSON element containing the data to deserialize
     * @param type                     the type of the object to deserialize to
     * @param jsonDeserializationContext   the context for deserialization
     * @return                         a concrete implementation of the Disposition interface
     * @throws JsonParseException      if the JSON is not well-formed or cannot be deserialized
     */
    @Override
    public Disposition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String dispType = jsonObject.get("dispType").getAsString();
        // if the dispType attribute in the JSON is equal to "Diagonal Type treat it like
        // a diagonal type disposition, if it is a LType treat it as a L disposition
        Type rtr = switch (dispType) {
            case "DiagonalType" -> DiagonalType.class;
            case "LType" -> LType.class;
            default -> throw new JsonParseException("Type Not Defined");
        };
        return jsonDeserializationContext.deserialize(jsonElement,rtr);
    }
}
