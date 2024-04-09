package it.polimi.ingsw;

import it.polimi.ingsw.model.gameplane.GameBoard;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.card.GoldCard;
import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.model.util.GoldCardsFactory;
import it.polimi.ingsw.model.util.ResCardFactory;
import it.polimi.ingsw.model.util.StartingCardsFactory;
import it.polimi.ingsw.model.util.Symbol;
import it.polimi.ingsw.model.util.exception.InvalidPlacementException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GameBoardTest {
    GameBoard board;
    List<ResourceCard> resourceCards;
    List<StartingCard> startingCards;
    List<GoldCard> goldCards;

    @Before
    public void setUp() throws IOException {
        board = new GameBoard();
        resourceCards = new ResCardFactory().createItemsList();
        goldCards = new GoldCardsFactory().createItemsList();
        startingCards = new StartingCardsFactory().createItemsList();
    }

    @Test
    public void addStartingCard_startingCard_updatedBehaviour(){
        Map <Symbol,Integer> before = board.getCountResources();
        board.addStartingCard(startingCards.getLast());

        assertNotEquals(board.getCountResources(), before);

        assertTrue(board.checkPosition(14, 14));
        assertTrue(board.checkPosition(14, 16));
        assertFalse(board.checkPosition(16, 14));
        assertFalse(board.checkPosition(16, 16));
    }

    @Test
    public void checkPosition_outOfRangeIndexes_correctOutput(){
        assertFalse(board.checkPosition(40, 40));
        assertFalse(board.checkPosition(40, -1));
        assertFalse(board.checkPosition(-1, 40));
        assertFalse(board.checkPosition(-1, -1));
    }

    @Test
    public void updResources_startingCard_correctOutput(){
        addStartingCard_startingCard_updatedBehaviour();
        assertEquals((long)board.getCountResources().get(Symbol.BUTTERFLY), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FEATHER), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FUNGUS), 1);
        assertEquals((long)board.getCountResources().get(Symbol.GRASS), 1);
        assertEquals((long)board.getCountResources().get(Symbol.POTION), 0);
        assertEquals((long)board.getCountResources().get(Symbol.SCROLL), 0);
        assertEquals((long)board.getCountResources().get(Symbol.WOLF), 1);
    }

    @Test
    public void updResources_drawableCardFromStartingCard_correctOutput() throws InvalidPlacementException {
        addStartingCard_startingCard_updatedBehaviour();
        DrawableCard card = resourceCards.getFirst();
        card.setShowingFace(true);
        board.addCard(card,14,14);
        Map<Symbol,Integer> res = board.getCountResources();
        assertEquals((long)board.getCountResources().get(Symbol.BUTTERFLY), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FEATHER), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FUNGUS), 3);
        assertEquals((long)board.getCountResources().get(Symbol.GRASS), 1);
        assertEquals((long)board.getCountResources().get(Symbol.POTION), 0);
        assertEquals((long)board.getCountResources().get(Symbol.SCROLL), 0);
        assertEquals((long)board.getCountResources().get(Symbol.WOLF), 1);
    }

    @Test
    public void updResources_DrawableCardFromDrawableCard_correctOutput() throws InvalidPlacementException {
        updResources_drawableCardFromStartingCard_correctOutput();
        DrawableCard card = resourceCards.get(1);
        card.setShowingFace(true);
        board.addCard(card,13,13);
        Map<Symbol,Integer> res = board.getCountResources();
        assertEquals((long)board.getCountResources().get(Symbol.BUTTERFLY), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FEATHER), 0);
        assertEquals((long)board.getCountResources().get(Symbol.FUNGUS), 4);
        assertEquals((long)board.getCountResources().get(Symbol.GRASS), 1);
        assertEquals((long)board.getCountResources().get(Symbol.POTION), 0);
        assertEquals((long)board.getCountResources().get(Symbol.SCROLL), 0);
        assertEquals((long)board.getCountResources().get(Symbol.WOLF), 1);
    }

}