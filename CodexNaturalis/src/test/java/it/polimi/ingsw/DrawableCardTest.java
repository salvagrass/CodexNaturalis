package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
/**
 * Unit test for DrawableCardTest.
 */
public class DrawableCardTest {
    ResourceCard c;

    /**
     * Sets up the test environment by initializing objects for testing.
     */
    @Before
    public void setUp(){
        c = new ResourceCard(new Symbol[] {Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.BUTTERFLY},
                1, null);
    }

    /**
     * Tests the `givePoints` method when the card's front face is up.
     */
    @Test
    public void givePoints_frontFaceUp_correctOutput(){
        c.setShowingFace(true);
        assertEquals(c.givePoints(new Card[][]{{null, null},{null, null}}, new HashMap<>(0), 0,0), 1);
    }

    /**
     * Tests the `givePoints` method when the card's back face is up.
     */
    @Test
    public void givePoints_backFaceUp_zero(){
        c.setShowingFace(false);
        assertEquals(c.givePoints(new Card[][]{{null, null},{null, null}}, new HashMap<>(0), 0,0), 0);
    }
}
