package it.polimi.ingsw;

import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.json.ObjFactory;
import it.polimi.ingsw.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Unit test for LTypeTest.
 */
public class ObjFactoryTest {
    List<Objective> objectiveList;

    /**
     * Sets up the test environment by initializing objects for testing.
     */
    @Before
    public void setUp(){
        try {
            objectiveList = new ObjFactory().createItemsList();
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Test case for creating an item list with no input, using a DiagonalType object.
     *
     * @throws AssertionError if the test fails (e.g., if assertions are not met)
     */
    @Test
    public void createItemList_noInput_DiagonalTypeObj(){
        Objective obj = objectiveList.getFirst();
        assertEquals(obj.getId(),"ObjDisp01");
        assertEquals(obj.toString(),"Disposition: DiagonalType:\nColor: FUNGUS\nDirection: -1\nLength: 3\nPoints: 2");
    }

    /**
     * Test case for creating an item list with no input, using an LType object.
     *
     * @throws AssertionError if the test fails (e.g., if assertions are not met)
     */

    @Test
    public void createItemList_noInput_LTypeObj(){
        Objective obj = objectiveList.get(4);
        assertEquals(obj.getId(),"ObjDisp05");
        assertEquals(obj.toString(),"Disposition: LType\nMain Color: FUNGUS\nSecondary Color: GRASS\nFirst Card Position: DL\nPoints: 3");
    }

    /**
     * Test case for creating an item list with no input, using a ResourceObjective.
     *
     * @throws AssertionError if the test fails (e.g., if assertions are not met)
     */
    @Test
    public void createItemList_noInput_ResourceObjective(){
        Objective obj = objectiveList.get(8);
        Map<Symbol,Integer> actual = new EnumMap<>(Symbol.class);
        actual.put(Symbol.FUNGUS,3);
        assertEquals(obj.getId(),"ObjRes01");
        assertEquals(obj.toString(),"Resources: " + actual + "\nPoints: 2");
    }

}
