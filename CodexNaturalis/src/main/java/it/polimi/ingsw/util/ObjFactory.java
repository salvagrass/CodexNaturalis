package it.polimi.ingsw.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.item.objective.DispObjective;
import it.polimi.ingsw.model.item.objective.Disposition;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.model.item.objective.ResourceObjective;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that implements the factory method to create a list of objectives
 */

public class ObjFactory extends ItemFactory<Objective> {

    /**
     * Creates a list of objectives based on the ObjRes.json file
     * @return a list of objectives
     * @throws IOException if an IOException occurs reading the file
     * @throws NullPointerException if any of the json files are not found or are invalid
     */
    @Override
    public List<Objective> createItemsList() throws IOException, NullPointerException {
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(ObjFactory.class.getResourceAsStream("/data/ObjDisp.json")));
        Gson gson = new GsonBuilder().registerTypeAdapter(Disposition.class, new DispositionDeserializer()).create();
        List<Objective> objectives = new ArrayList<>(gson.fromJson(reader,new TypeToken<List<DispObjective>>(){}.getType()));
        reader.close();
        reader = new InputStreamReader(Objects.requireNonNull(ObjFactory.class.getResourceAsStream("/data/ObjRes.json")));
        objectives.addAll(gson.fromJson(reader,new TypeToken<List<ResourceObjective>>(){}.getType()));
        reader.close();
        return objectives;
    }
}
