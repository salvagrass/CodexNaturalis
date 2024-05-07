package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.util.StartingCardsFactory;
import it.polimi.ingsw.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

/**
 * Unit test for ResCardFactoryTest.
 */
public class StartingCardFactoryTest {
    List<StartingCard> startingCards;

    /**
     * Sets up the test environment by initializing objects for testing.
     */
    @Before
    public void setUp(){
        try {
            startingCards = new StartingCardsFactory().createItemsList();
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Test case for the createItemList method with no input, focusing on the startingCardList.
     */
    @Test
    public void createItemList_noInput_startingCardList(){
        StartingCard card = startingCards.get(4);
        assertArrayEquals(card.getShowingCorners(),new Symbol[]{Symbol.EMPTY,Symbol.EMPTY,Symbol.NOTAVAILABLE,Symbol.NOTAVAILABLE});
        assertArrayEquals(card.getCenter(),new Symbol[]{Symbol.WOLF,Symbol.BUTTERFLY,Symbol.GRASS});
        card.setShowingFace(true);
        assertArrayEquals(card.getShowingCorners(),new Symbol[]{Symbol.BUTTERFLY,Symbol.FUNGUS,Symbol.WOLF,Symbol.GRASS});
        assertArrayEquals(card.getCenter(), new Symbol[]{});
    }
}
