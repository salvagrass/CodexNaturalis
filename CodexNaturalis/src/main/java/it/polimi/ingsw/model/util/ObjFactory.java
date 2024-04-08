package it.polimi.ingsw.model.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.item.DispObjective;
import it.polimi.ingsw.model.item.Objective;
import it.polimi.ingsw.model.item.ResourceObjective;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjFactory extends ItemFactory<Objective> {

    @Override
    public List<Objective> createItemsList() throws IOException, NullPointerException {
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(ObjFactory.class.getResourceAsStream("/data/ObjDisp.json")));
        Gson gson = new GsonBuilder().registerTypeAdapter(DispObjective.class, new DispositionDeserializer()).create();
        List<Objective> objectives = new ArrayList<>(gson.fromJson(reader,new TypeToken<List<DispObjective>>(){}.getType()));
        reader.close();
        reader = new InputStreamReader(Objects.requireNonNull(ObjFactory.class.getResourceAsStream("/data/ObjRes")));
        objectives.addAll(gson.fromJson(reader,new TypeToken<List<ResourceObjective>>(){}.getType()));
        reader.close();
        return objectives;
    }
}
