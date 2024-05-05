package it.polimi.ingsw.util;

import com.google.gson.*;
import it.polimi.ingsw.model.item.objective.*;
import it.polimi.ingsw.util.json.DispositionSerializeLogic;

import java.lang.reflect.Type;

public class ObjectiveSerializeLogic implements JsonSerializer<Objective>, JsonDeserializer<Objective>{
    @Override
    public Objective deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String objType = object.get("type").getAsString();
        Type rtr = switch(objType){
            case "ResourceObjective" -> ResourceObjective.class;
            case "DispObjective" -> DispObjective.class;
            default -> throw new JsonParseException("Type not defined");
        };
        Gson gson = new GsonBuilder().
                registerTypeHierarchyAdapter(Disposition.class,new DispositionSerializeLogic()).
                create();
        return gson.fromJson(json,rtr);
    }

    @Override
    public JsonElement serialize(Objective src, Type typeOfSrc, JsonSerializationContext context) {
        Gson gson = new GsonBuilder().
                registerTypeHierarchyAdapter(Disposition.class,new DispositionSerializeLogic())
                .create();
        JsonElement element = gson.toJsonTree(src);
        JsonObject object = element.getAsJsonObject();
        object.addProperty("type",src.getClass().getSimpleName());
        return object;
    }
}
