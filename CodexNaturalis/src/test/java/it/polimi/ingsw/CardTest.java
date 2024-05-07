package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
/**
 * Unit test for Card.
 */
public class CardTest {
    ResourceCard c;

    /**
     * Sets up the test environment before executing test cases.
     * Initializes a `ResourceCard` with specific symbols for testing.
     */
    @Before
    public void setUp(){
        c = new ResourceCard(new Symbol[] {Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.BUTTERFLY},
                0, null);
    }

    /**
     * Tests the behavior of the `getShowingCorners` method when the card's front face is up.
     * Verifies that the correct array of symbols is returned.
     */
    @Test
    public void getShowingCorners_frontFaceUpCard_correctOutput(){
        c.setShowingFace(true);
        assertArrayEquals(c.getShowingCorners(), new Symbol[]{Symbol.FEATHER,Symbol.BUTTERFLY,Symbol.NOTAVAILABLE,Symbol.EMPTY});
    }

    /**
     * Tests the behavior of the `getShowingCorners` method when the card's back face is up.
     * Verifies that an array of empty symbols is returned.
     */
    @Test
    public void getShowingCorners_backFaceUpCard_arrayOfEmptySymbols(){
        c.setShowingFace(false);
        assertArrayEquals(c.getShowingCorners(), new Symbol[]{Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY});
    }

    /**
     * Verifies the behavior of the `getCenter` method when the card's front face is up.
     */
    @Test
    public void getCenter_frontFaceUpCard_emptyArray(){
        c.setShowingFace(true);
        assertEquals(c.getCenter().length, 0);
    }

    /**
     * Verifies the behavior of the `getCenter` method when the card's back face is up.
     * Ensures that the center array contains only the symbol BUTTERFLY.
     */
    @Test
    public void getCenter_backFaceUpCard_correctOutput(){
        c.setShowingFace(false);
        assertArrayEquals(c.getCenter(), new Symbol[]{Symbol.BUTTERFLY});
    }

    /**
     * Tests the behavior of the getColor() method in the ResourceCard class.
     * The method is expected to return Symbol.BUTTERFLY when the card's showing face is set to true or false.
     */
    @Test
    public void getColor_ResourceCard_correctOutput(){
        c.setShowingFace(true);
        assertEquals(c.getColor(), Symbol.BUTTERFLY);
        c.setShowingFace(false);
        assertEquals(c.getColor(), Symbol.BUTTERFLY);
    }
}
