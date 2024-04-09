package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.objective.ResourceObjective;
import it.polimi.ingsw.model.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ResourceObjectiveTest {
    ResourceObjective r;

    @Before
    public void setUp(){
        Map<Symbol, Integer> res = new HashMap<>();
        res.put(Symbol.FUNGUS, 3);
        r = new ResourceObjective(2, res);
    }

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

        r = new ResourceObjective(2, res);

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
