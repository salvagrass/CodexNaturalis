package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.GoldCard;
import it.polimi.ingsw.model.item.card.GoldCardCorner;
import it.polimi.ingsw.model.item.card.GoldCardSymbol;
import it.polimi.ingsw.util.GoldCardsFactory;
import it.polimi.ingsw.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit test for GoldCardFactoryTest.
 */
public class GoldCardFactoryTest {
    List<GoldCard> goldCards;
    /**
     * Sets up the test environment by initializing objects for testing.
     */
    @Before
    public void setUp(){
        try {
            goldCards = new GoldCardsFactory().createItemsList();
            assertEquals(goldCards.size(),40);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Test case for creating an item list with no input, specifically for a GoldCard.
     */
    @Test
    public void createItemList_noInput_GoldCard(){
        GoldCard card = goldCards.getFirst();
        assertArrayEquals(card.getShowingCorners(), new Symbol[]{Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY});
        assertArrayEquals(card.getCenter(),new Symbol[]{Symbol.FUNGUS});
        card.setShowingFace(true);
        assertArrayEquals(card.getShowingCorners(),new Symbol[]{Symbol.EMPTY,Symbol.NOTAVAILABLE,Symbol.NOTAVAILABLE,Symbol.POTION});
        assertEquals(card.getCardPoints(),3);
        EnumMap<Symbol,Integer> req = new EnumMap<>(Symbol.class);
        req.put(Symbol.FUNGUS,3);
        assertEquals(card.getRequirements(),req);
    }

    /**
     * Test case for creating an item list with no input, specifically for a GoldCardCorner.
     */
    @Test
    public void createItemList_noInput_GoldCardCorner(){
        GoldCard card = goldCards.get(16);
        assertArrayEquals(card.getShowingCorners(), new Symbol[]{Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY});
        assertArrayEquals(card.getCenter(),new Symbol[]{Symbol.FUNGUS});
        card.setShowingFace(true);
        assertArrayEquals(card.getShowingCorners(),new Symbol[]{Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY,Symbol.NOTAVAILABLE});
        assertEquals(card.getCardPoints(),2);
        EnumMap<Symbol,Integer> req = new EnumMap<>(Symbol.class);
        req.put(Symbol.FUNGUS,3);
        req.put(Symbol.WOLF,1);
        assertEquals(card.getRequirements(),req);
        assertTrue(card instanceof GoldCardCorner);
    }

    /**
     * Test case for creating an item list with no input, specifically for a GoldCardSymbol.
     */
    @Test
    public void createItemList_noInput_GoldCardSymbol(){
        GoldCard card = goldCards.get(28);
        assertArrayEquals(card.getShowingCorners(), new Symbol[]{Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY});
        assertArrayEquals(card.getCenter(),new Symbol[]{Symbol.FUNGUS});
        card.setShowingFace(true);
        System.out.println(card.getId());
        assertArrayEquals(card.getShowingCorners(),new Symbol[]{Symbol.NOTAVAILABLE,Symbol.EMPTY,Symbol.FEATHER,Symbol.EMPTY});
        assertEquals(card.getCardPoints(),1);
        EnumMap<Symbol,Integer> req = new EnumMap<>(Symbol.class);
        req.put(Symbol.FUNGUS,2);
        req.put(Symbol.WOLF,1);
        assertEquals(card.getRequirements(),req);
        assertTrue(card instanceof GoldCardSymbol);
        assertEquals(card.getPoints(),"1 FEATHER");
    }
}
