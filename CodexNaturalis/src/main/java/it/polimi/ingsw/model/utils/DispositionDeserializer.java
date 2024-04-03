package it.polimi.ingsw.model.utils;

import com.google.gson.*;
import it.polimi.ingsw.model.item.LType;
import it.polimi.ingsw.model.item.DiagonalType;
import it.polimi.ingsw.model.item.Disposition;

import java.lang.reflect.Type;

public class DispositionDeserializer implements JsonDeserializer<Disposition> {
    @Override
    public Disposition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String dispType = jsonObject.get("DispType").getAsString();
        Type rtr = switch (dispType) {
            case "DiagonalType" -> DiagonalType.class;
            case "LType" -> LType.class;
            default -> throw new JsonParseException("Type Not Defined");
        };
        return jsonDeserializationContext.deserialize(jsonElement,rtr);
    }
}
