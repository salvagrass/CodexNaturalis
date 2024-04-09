package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.model.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class DrawableCardTest {
    ResourceCard c;

    @Before
    public void setUp(){
        c = new ResourceCard(new Symbol[] {Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.BUTTERFLY},
                1);
    }

    @Test
    public void givePoints_frontFaceUp_correctOutput(){
        c.setShowingFace(true);
        assertEquals(c.givePoints(new Card[][]{{null, null},{null, null}}, new HashMap<>(0), 0,0), 1);
    }

    @Test
    public void givePoints_backFaceUp_zero(){
        c.setShowingFace(false);
        assertEquals(c.givePoints(new Card[][]{{null, null},{null, null}}, new HashMap<>(0), 0,0), 0);
    }
}
