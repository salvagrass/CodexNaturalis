package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.model.item.objective.DiagonalType;
import it.polimi.ingsw.model.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DiagonalTypeTest {
    DiagonalType d1, d2;
    ResourceCard c;

    @Before
    public void setUp(){
        d1 = new DiagonalType(Symbol.GRASS, 1, 3);
        d2 = new DiagonalType(Symbol.GRASS, -1, 3);
        c = new ResourceCard(new Symbol[] {Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.GRASS},
                1);
    }

    @Test
    public void checkDisposition_noOverlappedDispositions_correctOutput(){
        ResourceCard c2 = new ResourceCard(new Symbol[] {Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.BUTTERFLY},
                1);
        assertEquals(d1.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null}
                }), 0);
        assertEquals(d2.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null}
                }), 0);
        assertEquals(d1.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null},
                        {null, c2  , null, null, null},
                        {null, null, c2  , null, null},
                        {null, null, null, c2  , null},
                        {null, null, null, null, null}
                }), 0);
        assertEquals(d2.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null},
                        {null, null, null, c2  , null},
                        {null, null, c2  , null, null},
                        {null, c2  , null, null, null},
                        {null, null, null, null, null}
                }), 0);
        assertEquals(d1.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null},
                        {null, c   , null, null, null},
                        {null, null, c   , null, null},
                        {null, null, null, c   , null},
                        {null, null, null, null, null}
                }), 1);
        assertEquals(d2.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null},
                        {null, null, null, c   , null},
                        {null, null, c   , null, null},
                        {null, c   , null, null, null},
                        {null, null, null, null, null}
                }), 1);
        assertEquals(d1.checkDisposition(new Card[][]
                {
                        {null, null, c   , null, null},
                        {null, c   , null, c   , null},
                        {c   , null, c   , null, c   },
                        {null, c   , null, c   , null},
                        {null, null, c   , null, null}
                }), 3);
        assertEquals(d2.checkDisposition(new Card[][]
                {
                        {null, null, c   , null, null},
                        {null, c   , null, c   , null},
                        {c   , null, c   , null, c   },
                        {null, c   , null, c   , null},
                        {null, null, c   , null, null}
                }), 3);
    }

    @Test
    public void checkDisposition_overlappedDispositions_correctOutput(){
        assertEquals(d1.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null, null, null},
                        {null, c   , null, null, null, null, null},
                        {null, null, c   , null, null, null, null},
                        {null, null, null, c   , null, null, null},
                        {null, null, null, null, c   , null, null},
                        {null, null, null, null, null, c   , null},
                        {null, null, null, null, null, null, null}
                }), 1);
        assertEquals(d1.checkDisposition(new Card[][]
                {
                        {c   , null, null, null, null, null, null},
                        {null, c   , null, null, null, null, null},
                        {null, null, c   , null, null, null, null},
                        {null, null, null, c   , null, null, null},
                        {null, null, null, null, c   , null, null},
                        {null, null, null, null, null, c   , null},
                        {null, null, null, null, null, null, null}
                }), 2);
        assertEquals(d1.checkDisposition(new Card[][]
                {
                        {c   , null, null, null, null, null, null},
                        {null, c   , null, null, null, null, null},
                        {null, null, c   , null, null, null, null},
                        {null, null, null, c   , null, null, null},
                        {null, null, null, null, c   , null, null},
                        {null, null, null, null, null, c   , null},
                        {null, null, null, null, null, null, c   }
                }), 2);
        assertEquals(d2.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, c   , null},
                        {null, null, null, null, c   , null, null},
                        {null, null, null, c   , null, null, null},
                        {null, null, c   , null, null, null, null},
                        {null, c   , null, null, null, null, null},
                        {null, null, null, null, null, null, null}
                }), 1);
        assertEquals(d2.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, c   , null},
                        {null, null, null, null, c   , null, null},
                        {null, null, null, c   , null, null, null},
                        {null, null, c   , null, null, null, null},
                        {null, c   , null, null, null, null, null},
                        {c   , null, null, null, null, null, null}
                }), 2);
        assertEquals(d2.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null, null, c   },
                        {null, null, null, null, null, c   , null},
                        {null, null, null, null, c   , null, null},
                        {null, null, null, c   , null, null, null},
                        {null, null, c   , null, null, null, null},
                        {null, c   , null, null, null, null, null},
                        {c   , null, null, null, null, null, null}
                }), 2);
        assertEquals(d1.checkDisposition(new Card[][]
                {
                        {c   , null, null, null, null, null, null},
                        {null, c   , null, c   , null, null, null},
                        {c   , null, c   , null, c   , null, null},
                        {null, c   , null, c   , null, c   , null},
                        {null, null, c   , null, c   , null, null},
                        {null, null, null, c   , null, c   , null},
                        {null, null, null, null, c   , null, c   }
                }), 4);
    }

    @Test
    public void checkDisposition_unvalidCells_correctOutput(){
        assertEquals(d1.checkDisposition(new Card[][]
                {
                        {null, c   , null, null, null},
                        {null, null, c   , null, null},
                        {null, null, null, c   , null},
                        {null, null, null, null, null},
                        {null, null, null, null, null}
                }), 0);
        assertEquals(d2.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null},
                        {null, null, c   , null, null},
                        {null, c   , null, null, null},
                        {c   , null, null, null, null},
                        {null, null, null, null, null}
                }), 0);
    }
}
