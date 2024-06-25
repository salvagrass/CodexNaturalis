package it.polimi.ingsw;

import it.polimi.ingsw.model.gameplane.GameBoard;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.util.ResCardFactory;
import it.polimi.ingsw.util.StartingCardsFactory;
import it.polimi.ingsw.util.Symbol;
import it.polimi.ingsw.util.exception.InvalidPlacementException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
/**
 * Unit test for GameBoardTest.
 */
public class GameBoardTest {
    GameBoard board;
    List<ResourceCard> resourceCards;
    List<StartingCard> startingCards;

    /**
     * Sets up the test environment by initializing objects for testing.
     */
    @Before
    public void setUp(){
        board = new GameBoard();
        try {
            resourceCards = new ResCardFactory().createItemsList();
            startingCards = new StartingCardsFactory().createItemsList();
            addStartingCard_startingCard_updatedBehaviour();
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests the behavior of the `addStartingCard` method when adding a starting card to the board.
     * Verifies that the resource counts are updated and position checks return the expected results.
     */
    public void addStartingCard_startingCard_updatedBehaviour(){
        Map <Symbol,Integer> before = board.getCountResources();
        board.addStartingCard(startingCards.getLast());

        assertNotEquals(board.getCountResources(), before);

        assertTrue(board.checkPosition(-1, -1));
        assertTrue(board.checkPosition(-1, 1));
        assertFalse(board.checkPosition(1, -1));
        assertFalse(board.checkPosition(1, 1));
    }

    /**
     * Checks if the given position is within the valid board boundaries.
     */
    @Test
    public void checkPosition_outOfRangeIndexes_correctOutput(){
        assertFalse(board.checkPosition(25, 25));   //Down-Right
        assertFalse(board.checkPosition(25, -16));  //Down-Left
        assertFalse(board.checkPosition(-16, 25));  //Upper-Right
        assertFalse(board.checkPosition(-16, -16)); // Upper-Left
    }

    /**
     * Adds a drawable card to the board at the specified position.
     */
    @Test (expected = InvalidPlacementException.class)
    public void addCard_invalidPosition_exceptionThrown() throws InvalidPlacementException{
        DrawableCard card = resourceCards.getFirst();
        board.addCard(card,1,-1);
    }

    /**
     * Adds a drawable card to the board at the specified position.
     */
    @Test
    public void addCard_surroundedCard_correctOutput(){
        DrawableCard card = new ResourceCard(new Symbol[]{Symbol.FUNGUS,Symbol.WOLF,Symbol.GRASS,Symbol.BUTTERFLY}, new Symbol[]{Symbol.FUNGUS},0,"Test");
        card.setShowingFace(true);
        try {

            //getting the board ready
            board.addCard(card,-1,1);

            //creating the upper triangle
            board.addCard(card,-2,2);
            board.addCard(card,-3,3);
            board.addCard(card,-2,4);

            //creating the lower triangle
            board.addCard(card,0,2);
            board.addCard(card,1,3);
            board.addCard(card,0,4);

            //Card surrounded by other cards
            /*
                 0, 0, C, 0,
                 0, C, 0, C,
                 C, 0, X, 0,
                 0, C, 0, C,
                 0, 0, C, 0,
             */
            board.addCard(card,-1,3);
        } catch (InvalidPlacementException e) {
            fail();
        }
    }

    /**
     * Updates the resource counts based on the starting card placement.
     * This method is used to verify that the correct resources are initialized on the board.
     */
    @Test
    public void updResources_startingCard_correctOutput(){
        assertEquals((long)board.getCountResources().get(Symbol.BUTTERFLY), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FEATHER), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FUNGUS), 1);
        assertEquals((long)board.getCountResources().get(Symbol.GRASS), 1);
        assertEquals((long)board.getCountResources().get(Symbol.POTION), 0);
        assertEquals((long)board.getCountResources().get(Symbol.SCROLL), 0);
        assertEquals((long)board.getCountResources().get(Symbol.WOLF), 1);
    }

    /**
     * Updates the resource counts based on the starting card placement.
     * This method is used to verify that the correct resources are initialized on the board.
     */
    @Test
    public void updResources_drawableCardFromStartingCard_correctOutput(){
        DrawableCard card = resourceCards.getFirst();
        card.setShowingFace(true);
        try {
            board.addCard(card,-1,-1);
        } catch (InvalidPlacementException e) {
            fail();
        }
        assertEquals((long)board.getCountResources().get(Symbol.BUTTERFLY), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FEATHER), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FUNGUS), 3);
        assertEquals((long)board.getCountResources().get(Symbol.GRASS), 1);
        assertEquals((long)board.getCountResources().get(Symbol.POTION), 0);
        assertEquals((long)board.getCountResources().get(Symbol.SCROLL), 0);
        assertEquals((long)board.getCountResources().get(Symbol.WOLF), 1);
    }

    /**
     * Updates resources using a DrawableCard and verifies the correct output.
     */
    @Test
    public void updResources_DrawableCardFromDrawableCard_correctOutput(){
        try {
            board.addCard(resourceCards.getFirst(),-1,-1);
            DrawableCard card = resourceCards.get(1);
            card.setShowingFace(true);
            board.addCard(card,-2,-2);
        } catch (InvalidPlacementException e) {
            fail();
        }
        assertEquals((long)board.getCountResources().get(Symbol.BUTTERFLY), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FEATHER), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FUNGUS), 4);
        assertEquals((long)board.getCountResources().get(Symbol.GRASS), 1);
        assertEquals((long)board.getCountResources().get(Symbol.POTION), 0);
        assertEquals((long)board.getCountResources().get(Symbol.SCROLL), 0);
        assertEquals((long)board.getCountResources().get(Symbol.WOLF), 1);
    }

    /**
     * Updates resources after adding a surrounded card and verifies the correct output.
     */
    @Test
    public void updResource_surroundedCard_CorrectOutput(){
        addCard_surroundedCard_correctOutput();
        assertEquals((long)board.getCountResources().get(Symbol.BUTTERFLY), 7);
        assertEquals((long)board.getCountResources().get(Symbol.FEATHER), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FUNGUS), 8);
        assertEquals((long)board.getCountResources().get(Symbol.GRASS), 5);
        assertEquals((long)board.getCountResources().get(Symbol.POTION), 0);
        assertEquals((long)board.getCountResources().get(Symbol.SCROLL), 0);
        assertEquals((long)board.getCountResources().get(Symbol.WOLF), 5);
    }

    /**
     * Expands the game board when the edge is reached and verifies that the board is correctly expanded.
     */
    @Test
    public void expandBoard_edgeReached_boardExpanded(){
        DrawableCard card = new ResourceCard(new Symbol[]{Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY,Symbol.EMPTY}, new Symbol[]{Symbol.FUNGUS},0,"Test");
        int i,j;
        assertFalse(board.checkPosition(-16,16));
        for(i=-1,j=1;i>=-15&&j<=15;i--,j++)
            try {
                board.addCard(card,i,j);
            }
            catch (InvalidPlacementException e) {
                fail();
            }
        assertTrue(board.checkPosition(-16,16));
        for(i=0,j=0;i>=-15 && j<=15;i--,j++)
            assertFalse(board.checkPosition(i,j));
    }
}