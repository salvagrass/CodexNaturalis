package it.polimi.ingsw.util.json;

import com.google.gson.*;
import it.polimi.ingsw.model.item.objective.*;
import it.polimi.ingsw.util.json.DispositionSerializeLogic;

import java.lang.reflect.Type;

public class ObjectiveSerializeLogic implements JsonSerializer<Objective>, JsonDeserializer<Objective>{

    /**
     * Deserialize a JSON representation of an Objective into an Objective object.
     *
     * @param json The JSON data to deserialize.
     * @param typeOfT The type of the target object (e.g., ResourceObjective, DispObjective).
     * @param context The context for deserialization.
     * @return An Objective object representing the deserialized data.
     * @throws JsonParseException If the type is not defined.
     */
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

    /**
     * Serialize an Objective object into its JSON representation.
     *
     * @param src The Objective object to serialize.
     * @param typeOfSrc The type of the target object (e.g., ResourceObjective, DispObjective).
     * @param context The context for serialization.
     * @return A JsonElement representing the serialized data.
     */
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
