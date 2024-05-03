package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.GoldCard;
import it.polimi.ingsw.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GoldCardTest {
    GoldCard c;

    @Before
    public void setUp(){
        Map<Symbol, Integer> req = new HashMap<>();
        req.put(Symbol.BUTTERFLY, 3);
        req.put(Symbol.FUNGUS, 1);
        c = new GoldCard(new Symbol[] {Symbol.POTION, Symbol.NOTAVAILABLE, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.BUTTERFLY},
                req,
                3, null);
    }

    @Test
    public void checkRequirements_enoughItems_true(){
        Map<Symbol, Integer> req = new HashMap<>();
        req.put(Symbol.BUTTERFLY, 4);
        req.put(Symbol.FUNGUS, 3);
        req.put(Symbol.WOLF, 0);
        req.put(Symbol.GRASS, 0);
        req.put(Symbol.POTION, 0);
        req.put(Symbol.FEATHER, 0);
        req.put(Symbol.SCROLL, 0);
        assertTrue(c.checkRequirements(req));
        req.put(Symbol.BUTTERFLY, 3);
        req.put(Symbol.FUNGUS, 1);
        assertTrue(c.checkRequirements(req));
    }

    @Test
    public void checkRequirements_notEnoughItems_false(){
        Map<Symbol, Integer> req = new HashMap<>();
        req.put(Symbol.BUTTERFLY, 1);
        req.put(Symbol.FUNGUS, 3);
        req.put(Symbol.WOLF, 0);
        req.put(Symbol.GRASS, 0);
        req.put(Symbol.POTION, 6);
        req.put(Symbol.FEATHER, 0);
        req.put(Symbol.SCROLL, 0);
        assertFalse(c.checkRequirements(req));
        req.put(Symbol.FUNGUS, 0);
        assertFalse(c.checkRequirements(req));
    }
}
