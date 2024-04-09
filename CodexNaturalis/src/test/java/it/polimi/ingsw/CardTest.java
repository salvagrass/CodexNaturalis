package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.model.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CardTest {
    ResourceCard c;

    @Before
    public void setUp(){
        c = new ResourceCard(new Symbol[] {Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.BUTTERFLY},
                0);
    }

    @Test
    public void getShowingCorners_frontFaceUpCard_correctOutput(){
        c.setShowingFace(true);
        assertArrayEquals(c.getShowingCorners(), new Symbol[]{Symbol.FEATHER,Symbol.BUTTERFLY,Symbol.NOTAVAILABLE,Symbol.EMPTY});
    }

    @Test
    public void getShowingCorners_backFaceUpCard_arrayOfEmptySymbols(){
        c.setShowingFace(false);
        assertArrayEquals(c.getShowingCorners(), new Symbol[]{Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY});
    }

    @Test
    public void getCenter_frontFaceUpCard_emptyArray(){
        c.setShowingFace(true);
        assertEquals(c.getCenter().length, 0);
    }

    @Test
    public void getCenter_backFaceUpCard_correctOutput(){
        c.setShowingFace(false);
        assertArrayEquals(c.getCenter(), new Symbol[]{Symbol.BUTTERFLY});
    }

    @Test
    public void getColor_ResourceCard_correctOutput(){
        c.setShowingFace(true);
        assertEquals(c.getColor(), Symbol.BUTTERFLY);
        c.setShowingFace(false);
        assertEquals(c.getColor(), Symbol.BUTTERFLY);
    }
}
