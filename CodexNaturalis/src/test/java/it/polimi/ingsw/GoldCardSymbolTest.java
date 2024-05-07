package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.GoldCardSymbol;
import it.polimi.ingsw.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for GoldCardSymbolTest.
 */
public class GoldCardSymbolTest {
    GoldCardSymbol c;

    /**
     * Sets up the test environment by initializing objects for testing.
     */
    @Before
    public void setUp(){
        Map<Symbol, Integer> req = new HashMap<>();
        req.put(Symbol.BUTTERFLY, 2);
        req.put(Symbol.FUNGUS, 1);
        c = new GoldCardSymbol(new Symbol[] {Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.POTION, Symbol.EMPTY},
                new Symbol[] {Symbol.BUTTERFLY},
                req,
                1,
                Symbol.POTION, null);
    }

    /**
     * Test case for calculating points when the card is front face up.
     */
    @Test
    public void givePoints_frontFaceUp_correctOutput(){
        Map<Symbol, Integer> res = new HashMap<>();
        res.put(Symbol.BUTTERFLY, 4);
        res.put(Symbol.FUNGUS, 3);
        res.put(Symbol.WOLF, 0);
        res.put(Symbol.GRASS, 0);
        res.put(Symbol.POTION, 0);
        res.put(Symbol.FEATHER, 0);
        res.put(Symbol.SCROLL, 0);
        c.setShowingFace(true);
        assertEquals(c.givePoints(new Card[][] {{null,null},{null,null}}, res, 0, 0), 0);
        res.put(Symbol.POTION, 5);
        assertEquals(c.givePoints(new Card[][] {{null,null},{null,null}}, res, 0, 0), 5);
    }

    /**
     * Test case for calculating points when the card is back face up.
     */
    @Test
    public void givePoints_backFaceUp_zero(){
        Map<Symbol, Integer> res = new HashMap<>();
        res.put(Symbol.BUTTERFLY, 4);
        res.put(Symbol.FUNGUS, 3);
        res.put(Symbol.WOLF, 0);
        res.put(Symbol.GRASS, 0);
        res.put(Symbol.POTION, 0);
        res.put(Symbol.FEATHER, 0);
        res.put(Symbol.SCROLL, 0);
        c.setShowingFace(false);
        assertEquals(c.givePoints(new Card[][] {{null,null},{null,null}}, res, 0, 0), 0);
        res.put(Symbol.POTION, 5);
        assertEquals(c.givePoints(new Card[][] {{null,null},{null,null}}, res, 0, 0), 0);
    }
}
