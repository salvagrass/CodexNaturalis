package it.polimi.ingsw;

import it.polimi.ingsw.model.gameplane.CommonGround;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.exception.InvalidDrawException;
import it.polimi.ingsw.util.exception.InvalidSelectionException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Unit test for CommonGround.
 */
public class CommonGroundTest {
    CommonGround ground;
    /**
     * Sets up the test environment by creating a new instance of the `CommonGround` class.
     * This method is executed before each test method.
     *
     */
    @Before
    public void SetUp(){
        try {
            ground = new CommonGround();
        } catch (IOException e) {
            fail();
        }
    }

    /**
     * Tests the behavior of the `setDecks` method when no input is provided.
     * It verifies that decks are correctly created.
     */
    @Test
    public void setDecks_noInput_createdDecks(){
        for(int i=0;i<6;i++) {
            DrawableCard card = null;
            try {
                card = ground.draw(i);
            } catch (Exception e) {
                fail();
            }
            assertNotNull(card);
        }
    }

    /**
     * Tests the behavior of drawing cards from an empty deck.
     * Verifies that attempting to draw from an empty deck results in an {@link InvalidDrawException}.
     */
    @Test
    public void draw_drawFromEmpty_invalidDraw(){
        boolean isEmpty=false;
        while(!isEmpty){
            try{ground.draw(0);}
            catch (InvalidDrawException e){isEmpty = true;}
            catch (InvalidSelectionException d){fail();}
        }
        isEmpty = false;
        while(!isEmpty){
            try{ground.draw(3);}
            catch (InvalidDrawException e){isEmpty = true;}
            catch (InvalidSelectionException d){fail();}
        }

        try{ground.draw(0);}
        catch (InvalidDrawException e){
            try{ground.draw(3);}
            catch (InvalidDrawException d){return;}
            catch (InvalidSelectionException ignored){}
        }
        catch (InvalidSelectionException ignored){}
        fail();
    }
    /**
     * Tests the behavior of drawing from an empty ground.
     * This method verifies that when drawing from an empty ground
     * @throws InvalidSelectionException if an invalid selection occurs during the test.
     * @throws InvalidDrawException if an invalid draw occurs during the test.
     */
    @Test (expected = InvalidDrawException.class)
    public void draw_drawFromEmptyGround_invalidDraw() throws InvalidSelectionException, InvalidDrawException {
        draw_drawFromEmpty_invalidDraw();
        try {
            ground.draw(1);
        } catch (InvalidDrawException | InvalidSelectionException e) {
            fail();
        }
        ground.draw(1);
    }

    /**
     * Tests the behavior of drawing with an invalid selection.
     * @throws InvalidSelectionException if an invalid selection occurs during the test.
     * @throws InvalidDrawException if an invalid draw occurs during the test.
     */
    @Test (expected = InvalidSelectionException.class)
    public void draw_invalidSelection_InvalidSelectionException() throws InvalidSelectionException, InvalidDrawException {
        try{
            ground.draw(-1);
        }
        catch (InvalidSelectionException e){
            ground.draw(9);
        }
        fail();
    }

    /**
     * Tests the behavior of setting objectives with no input.
     */
    @Test
    public void setObjectives_noInput_objSet(){
        Objective[] objectives = ground.getCommonObjectives();
        assertNotNull(objectives);
        for(Objective obj: objectives)
            assertNotNull(obj);
    }

    /**
     * Tests the behavior of retrieving a random objective with no input.
     */
    @Test
    public void getRandomObjective_noInput_objective(){
        Objective obj1 = ground.getRandomObjective();
        Objective obj2 = ground.getRandomObjective();
        assertNotEquals(obj1,obj2);
        assertNotNull(obj1);
        assertNotNull(obj2);
    }
}
