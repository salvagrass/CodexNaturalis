package it.polimi.ingsw;

import it.polimi.ingsw.model.gameplane.Deck;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.model.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DeckTest {
    Deck<ResourceCard> deck1;
    List<ResourceCard> ls1;
    @Before
    public void setUp(){
        ls1 = new ArrayList<>(4);
        ls1.add(new ResourceCard(new Symbol[] {Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.BUTTERFLY},
                0, null));
        ls1.add(new ResourceCard(new Symbol[] {Symbol.NOTAVAILABLE, Symbol.FUNGUS, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.FUNGUS},
                1, null));
        ls1.add(new ResourceCard(new Symbol[] {Symbol.EMPTY, Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.GRASS},
                new Symbol[] {Symbol.GRASS},
                2, null));
        ls1.add(new ResourceCard(new Symbol[] {Symbol.POTION, Symbol.EMPTY, Symbol.WOLF, Symbol.EMPTY},
                new Symbol[] {Symbol.WOLF},
                3, null));
        deck1 = new Deck<>(ls1);
    }

    @Test
    public void draw_normalDeck_correctOutput(){
        DrawableCard tmp;

        tmp = ls1.get(3);
        assertEquals(deck1.draw(), tmp);

        tmp = ls1.get(2);
        assertEquals(deck1.draw(), tmp);

        tmp = ls1.get(1);
        assertEquals(deck1.draw(), tmp);

        tmp = ls1.get(0);
        assertEquals(deck1.draw(), tmp);
    }

    @Test
    public void draw_emptyDeck_nullValue(){
        Deck<ResourceCard> d = new Deck<>(new ArrayList<>(0));
        assertNull(d.draw());
    }

    @Test
    public void shuffle_emptyDeck_noExceptions(){
        Deck<ResourceCard> d = new Deck<>(new ArrayList<>(0));
        d.shuffle();
        assert(true); //No exceptions threw
    }

    @Test
    public void shuffle_oneCardDeck_noChanges(){
        List<ResourceCard> ls = new ArrayList<>(1), ls2;
        ls.add(new ResourceCard(new Symbol[] {Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.BUTTERFLY},
                0, null));
        ls2 = new ArrayList<>(ls);
        Deck<ResourceCard> d = new Deck<>(ls), d2 = new Deck<>(ls2);
        d.shuffle();
        assertEquals(d.draw(), d2.draw());
    }

    @Test
    public void shuffle_normalDeck_differentPermutation(){
        boolean samePermutation = true;
        Deck<ResourceCard> deck2 = new Deck<>(new ArrayList<>(ls1));
        DrawableCard c1, c2;
        deck1.shuffle();
        for(int i = 0; i < 4 && samePermutation; i++){
            c1 = deck1.draw();
            c2 = deck2.draw();
            samePermutation = c1.equals(c2);
        }
        assert(!samePermutation);
    }

    @Test
    public void getSize_normalDeck_correctOutput(){
        assertEquals(deck1.getSize(), 4);
        deck1.draw();
        assertEquals(deck1.getSize(), 3);
        deck1.draw();
        deck1.draw();
        assertEquals(deck1.getSize(), 1);
        deck1.draw();
        assertEquals(deck1.getSize(), 0);
    }
}
