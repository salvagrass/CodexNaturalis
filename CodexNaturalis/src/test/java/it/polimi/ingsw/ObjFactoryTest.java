package it.polimi.ingsw;

import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.model.util.ObjFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ObjFactoryTest {
    List<Objective> objectiveList;

    @Before
    public void setUp(){
        try {
            objectiveList = new ObjFactory().createItemsList();
        } catch (Exception e) {
            fail();
        }
    }

    //TODO: Either add a getter for the objectives or find another way to test the factories
    @Test
    public void createItemList_NoInput_DiagonalTypeObj(){
        Objective obj = objectiveList.getFirst();
        assertEquals(obj.getPoints(),2);
    }
}
