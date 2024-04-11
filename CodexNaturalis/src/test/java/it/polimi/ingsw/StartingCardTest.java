package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.model.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class StartingCardTest {
    StartingCard c;

    @Before
    public void setUp(){
        c = new StartingCard(new Symbol[] {Symbol.FUNGUS, Symbol.GRASS, Symbol.WOLF, Symbol.BUTTERFLY},
                new Symbol[] {Symbol.BUTTERFLY},
                new Symbol[] {Symbol.EMPTY, Symbol.GRASS, Symbol.EMPTY, Symbol.BUTTERFLY},
                null);
    }

    @Test
    public void getShowingCorners_frontFaceUp_correctOutput(){
        c.setShowingFace(true);
        assertArrayEquals(c.getShowingCorners(), new Symbol[] {Symbol.FUNGUS, Symbol.GRASS, Symbol.WOLF, Symbol.BUTTERFLY});
    }

    @Test
    public void getShowingCorners_backFaceUp_correctOutput(){
        c.setShowingFace(false);
        assertArrayEquals(c.getShowingCorners(), new Symbol[] {Symbol.EMPTY, Symbol.GRASS, Symbol.EMPTY, Symbol.BUTTERFLY});
    }
}
