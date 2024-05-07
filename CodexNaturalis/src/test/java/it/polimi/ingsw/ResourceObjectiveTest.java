package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.objective.ResourceObjective;
import it.polimi.ingsw.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for ResCardFactoryTest.
 */
public class ResourceObjectiveTest {
    ResourceObjective r;

    /**
     * Sets up the test environment by initializing objects for testing.
     */
    @Before
    public void setUp(){
        Map<Symbol, Integer> res = new HashMap<>();
        res.put(Symbol.FUNGUS, 3);
        r = new ResourceObjective(2, null, res);
    }

    /**
     * Test case for counting objective points when there are not enough symbols.
     *
     * @throws AssertionError if the test fails (e.g., if assertions are not met)
     */
    @Test
    public void countObjectivePoints_notEnoughSymbol_zero(){
        Map<Symbol, Integer> res = new HashMap<>();
        res.put(Symbol.BUTTERFLY, 4);
        res.put(Symbol.FUNGUS, 1);
        res.put(Symbol.WOLF, 0);
        res.put(Symbol.GRASS, 0);
        res.put(Symbol.POTION, 0);
        res.put(Symbol.FEATHER, 0);
        res.put(Symbol.SCROLL, 0);
        assertEquals(r.countObjectivePoints(new Card[][]{{null, null},{null, null}}, res), 0);
    }

    /**
     * Calculates the objective points based on the provided symbol counts.
     * The method takes a map of symbol counts and returns the total objective points.
     */
    @Test
    public void countObjectivePoints_enoughSymbol_correctOutput(){
        Map<Symbol, Integer> res = new HashMap<>();
        res.put(Symbol.BUTTERFLY, 4);
        res.put(Symbol.FUNGUS, 3);
        res.put(Symbol.WOLF, 0);
        res.put(Symbol.GRASS, 0);
        res.put(Symbol.POTION, 0);
        res.put(Symbol.FEATHER, 0);
        res.put(Symbol.SCROLL, 0);
        assertEquals(r.countObjectivePoints(new Card[][]{{null, null},{null, null}}, res), 2);
        res.put(Symbol.FUNGUS, 4);
        assertEquals(r.countObjectivePoints(new Card[][]{{null, null},{null, null}}, res), 2);
        res.put(Symbol.FUNGUS, 5);
        assertEquals(r.countObjectivePoints(new Card[][]{{null, null},{null, null}}, res), 2);
        res.put(Symbol.FUNGUS, 6);
        assertEquals(r.countObjectivePoints(new Card[][]{{null, null},{null, null}}, res), 4);
        res.put(Symbol.FUNGUS, 9);
        assertEquals(r.countObjectivePoints(new Card[][]{{null, null},{null, null}}, res), 6);
    }

    /**
     * Calculates the objective points based on the provided symbol counts.
     * The method takes a map of symbol counts and returns the total objective points.
     */
    @Test
    public void countObjectivePoints_multipleSymbolsRequired_correctOutput(){
        Map<Symbol, Integer> mapRes = new HashMap<>();
        Map<Symbol, Integer> res = new HashMap<>();

        mapRes.put(Symbol.BUTTERFLY, 4);
        mapRes.put(Symbol.FUNGUS, 3);
        mapRes.put(Symbol.WOLF, 0);
        mapRes.put(Symbol.GRASS, 0);
        mapRes.put(Symbol.POTION, 0);
        mapRes.put(Symbol.FEATHER, 0);
        mapRes.put(Symbol.SCROLL, 0);

        res.put(Symbol.FUNGUS, 3);
        res.put(Symbol.SCROLL, 2);

        r = new ResourceObjective(2, null, res);

        assertEquals(r.countObjectivePoints(new Card[][]{{null, null},{null, null}}, mapRes), 0);

        mapRes.put(Symbol.SCROLL, 2);
        assertEquals(r.countObjectivePoints(new Card[][]{{null, null},{null, null}}, mapRes), 2);

        mapRes.put(Symbol.FUNGUS, 5);
        assertEquals(r.countObjectivePoints(new Card[][]{{null, null},{null, null}}, mapRes), 2);

        mapRes.put(Symbol.FUNGUS, 7);
        mapRes.put(Symbol.SCROLL, 4);
        assertEquals(r.countObjectivePoints(new Card[][]{{null, null},{null, null}}, mapRes), 4);

        mapRes.put(Symbol.FUNGUS, 9);
        assertEquals(r.countObjectivePoints(new Card[][]{{null, null},{null, null}}, mapRes), 4);
    }
}
