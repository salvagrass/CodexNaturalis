package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.util.ResCardFactory;
import it.polimi.ingsw.util.Symbol;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit test for ResCardFactoryTest.
 */
public class ResCardFactoryTest {

    /**
     * Test case for creating an item list with no input, using resource cards.
     *
     * @throws AssertionError if the test fails (e.g., if assertions are not met)
     */
    @Test
    public void createItemList_noInput_resCardList(){
        List<ResourceCard> cards = null;
        try {
            cards = new ResCardFactory().createItemsList();
        } catch (Exception e) {
            fail();
        }
        ResourceCard card = cards.get(8);
        assertArrayEquals(card.getShowingCorners(), new Symbol[]{Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY});
        assertArrayEquals(card.getCenter(),new Symbol[]{Symbol.FUNGUS});
        card.setShowingFace(true);
        assertArrayEquals(card.getShowingCorners(),new Symbol[]{Symbol.FUNGUS,Symbol.NOTAVAILABLE,Symbol.EMPTY,Symbol.EMPTY});
        assertEquals(card.getCardPoints(),1);
    }
}
