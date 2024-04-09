package it.polimi.ingsw.model.util;

import com.google.gson.*;
import it.polimi.ingsw.model.item.objective.LType;
import it.polimi.ingsw.model.item.objective.DiagonalType;
import it.polimi.ingsw.model.item.objective.Disposition;

import java.lang.reflect.Type;

public class DispositionDeserializer implements JsonDeserializer<Disposition> {
    @Override
    public Disposition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String dispType = jsonObject.get("dispType").getAsString();
        Type rtr = switch (dispType) {
            case "DiagonalType" -> DiagonalType.class;
            case "LType" -> LType.class;
            default -> throw new JsonParseException("Type Not Defined");
        };
        return jsonDeserializationContext.deserialize(jsonElement,rtr);
    }
}
