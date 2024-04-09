package it.polimi.ingsw;

import it.polimi.ingsw.model.gameplane.CommonGround;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.model.util.ObjFactory;
import it.polimi.ingsw.model.util.exception.InvalidDrawException;
import it.polimi.ingsw.model.util.exception.InvalidSelectionException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class CommonGroundTest {
    CommonGround ground;

    @Before
    public void SetUp(){
        try {
            ground = new CommonGround();
        } catch (IOException e) {
            fail();
        }
    }

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

    @Test
    public void draw_drawFromEmpty_invalidDraw(){
        boolean isEmpty=false;
        while(!isEmpty){
            try{ground.draw(0);}
            catch (Exception e){isEmpty = true;}
        }
        isEmpty = false;
        while(!isEmpty){
            try{ground.draw(3);}
            catch (Exception e){isEmpty = true;}
        }

        try{ground.draw(0);}
        catch (Exception e){
            try{ground.draw(3);}
            catch (Exception d){return;}
        }
        fail();
    }
    @Test
    public void setObjectives_noInput_objSet(){
        Objective[] objectives = ground.getCommonObjectives();
        assertNotNull(objectives);
        for(Objective obj: objectives)
            assertNotNull(obj);
    }

    @Test
    public void getRandomObjective_noInput_objective(){
        Objective obj1 = ground.getRandomObjective();
        Objective obj2 = ground.getRandomObjective();
        assertNotEquals(obj1,obj2);
    }
}
