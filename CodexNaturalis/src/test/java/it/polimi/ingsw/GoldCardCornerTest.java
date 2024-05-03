package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.GoldCardCorner;
import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GoldCardCornerTest {
    GoldCardCorner c;
    ResourceCard r;

    @Before
    public void setUp(){
        Map<Symbol, Integer> req = new HashMap<>();
        req.put(Symbol.BUTTERFLY, 3);
        req.put(Symbol.WOLF, 1);
        c = new GoldCardCorner(new Symbol[] {Symbol.EMPTY, Symbol.EMPTY, Symbol.EMPTY, Symbol.NOTAVAILABLE},
                new Symbol[] {Symbol.BUTTERFLY},
                req,
                2, null);
        r = new ResourceCard(new Symbol[] {Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.BUTTERFLY},
                1, null);
    }

    @Test
    public void givePoints_backFaceUp_zero(){
        assertEquals(c.givePoints(new Card[][] {{null, null, null}, {null, null, null}, {null, null, null}},
                        new HashMap<>(), 1, 1),
                0);
    }

    @Test
    public void givePoints_noCoveredCorners_zero(){
        c.setShowingFace(true);
        assertEquals(c.givePoints(new Card[][] {{null, null, null}, {null, null, null}, {null, null, null}},
                        new HashMap<>(), 1, 1),
                  0);
    }

    @Test
    public void givePoints_edgesOfTheMatrix_noExceptions(){
        c.setShowingFace(true);
        assertEquals(c.givePoints(new Card[][] {{null, null, null}, {null, null, null}, {null, null, null}},
                        new HashMap<>(), 0, 0),
                0);
        assertEquals(c.givePoints(new Card[][] {{null, null, null}, {null, null, null}, {null, null, null}},
                        new HashMap<>(), 2, 2),
                0);
        assertEquals(c.givePoints(new Card[][] {{null, null, null}, {null, null, null}, {null, null, null}},
                        new HashMap<>(), 2, 0),
                0);
        assertEquals(c.givePoints(new Card[][] {{null, null, null}, {null, null, null}, {null, null, null}},
                        new HashMap<>(), 0, 2),
                0);
    }

    @Test
    public void givePoints_oneCornerCovered_two(){
        c.setShowingFace(true);
        assertEquals(c.givePoints(new Card[][] {{r, null, null}, {null, null, null}, {null, null, null}},
                        new HashMap<>(), 1, 1),
                2);
        assertEquals(c.givePoints(new Card[][] {{null, r, null}, {null, null, null}, {null, null, null}},
                        new HashMap<>(), 1, 0),
                2);
        assertEquals(c.givePoints(new Card[][] {{null, null, null}, {null, r, null}, {null, null, null}},
                        new HashMap<>(), 0, 0),
                2);
        assertEquals(c.givePoints(new Card[][] {{null, null, null}, {r, null, null}, {null, null, null}},
                        new HashMap<>(), 0, 1),
                2);
    }

    @Test
    public void givePoints_twoCornersCovered_four(){
        c.setShowingFace(true);
        assertEquals(c.givePoints(new Card[][] {{r, null, r}, {null, null, null}, {null, null, null}},
                        new HashMap<>(), 1, 1),
                4);
        assertEquals(c.givePoints(new Card[][] {{r, null, null}, {null, null, null}, {r, null, null}},
                        new HashMap<>(), 1, 1),
                4);
        assertEquals(c.givePoints(new Card[][] {{null, null, r}, {null, null, null}, {null, null, r}},
                        new HashMap<>(), 1, 1),
                4);
        assertEquals(c.givePoints(new Card[][] {{null, null, null}, {null, null, null}, {r, null, r}},
                        new HashMap<>(), 1, 1),
                4);
        assertEquals(c.givePoints(new Card[][] {{null, null, r}, {null, null, null}, {r, null, null}},
                        new HashMap<>(), 1, 1),
                4);
        assertEquals(c.givePoints(new Card[][] {{r, null, null}, {null, null, null}, {null, null, r}},
                        new HashMap<>(), 1, 1),
                4);
    }

    @Test
    public void givePoints_threeCornersCovered_six(){
        c.setShowingFace(true);
        assertEquals(c.givePoints(new Card[][] {{r, null, r}, {null, null, null}, {null, null, r}},
                        new HashMap<>(), 1, 1),
                6);
        assertEquals(c.givePoints(new Card[][] {{r, null, r}, {null, null, null}, {r, null, null}},
                        new HashMap<>(), 1, 1),
                6);
        assertEquals(c.givePoints(new Card[][] {{null, null, r}, {null, null, null}, {r, null, r}},
                        new HashMap<>(), 1, 1),
                6);
        assertEquals(c.givePoints(new Card[][] {{r, null, null}, {null, null, null}, {r, null, r}},
                        new HashMap<>(), 1, 1),
                6);
    }

    @Test
    public void givePoints_fourCornersCovered_eight(){
        c.setShowingFace(true);
        assertEquals(c.givePoints(new Card[][] {{r, null, r}, {null, null, null}, {r, null, r}},
                        new HashMap<>(), 1, 1),
                8);
    }
}