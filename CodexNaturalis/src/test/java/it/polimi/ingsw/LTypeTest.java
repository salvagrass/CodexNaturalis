package it.polimi.ingsw;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.model.item.objective.LType;
import it.polimi.ingsw.util.Symbol;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LTypeTest {
    LType l1, l2, l3, l4;
    ResourceCard c, d;

    @Before
    public void setUp(){
        l1 = new LType(Symbol.GRASS, Symbol.BUTTERFLY, 0);
        l2 = new LType(Symbol.GRASS, Symbol.BUTTERFLY, 1);
        l3 = new LType(Symbol.GRASS, Symbol.BUTTERFLY, 2);
        l4 = new LType(Symbol.GRASS, Symbol.BUTTERFLY, 3);
        c = new ResourceCard(new Symbol[] {Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.GRASS},
                1, null);
        d = new ResourceCard(new Symbol[] {Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY},
                new Symbol[] {Symbol.BUTTERFLY},
                1, null);
    }

    @Test
    public void checkDisposition_noOverlappedDispositionsEdgeCases_correctOutput(){
        assertEquals(l1.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null}
                }), 0);
        assertEquals(l2.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null}
                }), 0);
        assertEquals(l3.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null}
                }), 0);
        assertEquals(l4.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null}
                }), 0);
        assertEquals(l1.checkDisposition(new Card[][]
                {
                        {null, null, c   , null, null},
                        {null, d   , null, null, null},
                        {null, null, null, null, null},
                        {null, d   , null, null, null},
                        {null, null, null, null, null}
                }), 0);
        assertEquals(l1.checkDisposition(new Card[][]
                {
                        {d   , null, null, null, null},
                        {null, c   , null, d   , null},
                        {null, null, null, null, c   },
                        {null, c   , null, null, null},
                        {null, null, null, null, c   }
                }), 2);
        assertEquals(l2.checkDisposition(new Card[][]
                {
                        {null, null, null, null, d   },
                        {null, d   , null, c   , null},
                        {c   , null, null, null, null},
                        {null, null, null, c   , null},
                        {c   , null, null, null, null}
                }), 2);
        assertEquals(l3.checkDisposition(new Card[][]
                {
                        {c   , null, null, null, null},
                        {null, null, null, c   , null},
                        {c   , null, null, null, null},
                        {null, d   , null, c   , null},
                        {null, null, null, null, d   }
                }), 2);
        assertEquals(l4.checkDisposition(new Card[][]
                {
                        {null, null, null, null, c   },
                        {null, c   , null, null, null},
                        {null, null, null, null, c   },
                        {null, c   , null, d   , null},
                        {d   , null, null, null, null}
                }), 2);
    }

    @Test
    public void checkDisposition_overlappedDispositions_correctOutput(){
        assertEquals(l1.checkDisposition(new Card[][]
                {
                        {d   , null, d   , null, null, null, null},
                        {null, c   , null, c   , null, null, null},
                        {d   , null, null, null, null, null, null},
                        {null, c   , null, c   , null, null, null},
                        {d   , null, null, null, null, null, null},
                        {null, c   , null, null, null, null, null},
                        {null, null, null, null, null, null, null}
                }), 2);
        assertEquals(l2.checkDisposition(new Card[][]
                {
                        {null, null, null, null, null, null, d   },
                        {null, d   , null, null, null, c   , null},
                        {c   , null, null, null, null, null, null},
                        {null, d   , null, null, null, c   , null},
                        {c   , null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {c   , null, null, null, null, null, null}
                }), 2);
        assertEquals(l3.checkDisposition(new Card[][]
                {
                        {null, null, c   , null, null, null, null},
                        {null, null, null, null, null, c   , null},
                        {null, null, c   , null, null, null, null},
                        {null, null, null, d   , null, c   , null},
                        {null, null, null, null, null, null, d   },
                        {null, null, null, null, null, c   , null},
                        {null, null, null, null, null, null, d   }
                }), 2);
        assertEquals(l4.checkDisposition(new Card[][]
                {
                        {null, null, null, null, c   , null, null},
                        {null, c   , null, null, null, null, null},
                        {null, null, null, null, c   , null, null},
                        {null, c   , null, d   , null, null, null},
                        {d   , null, null, null, null, null, null},
                        {null, c   , null, null, null, null, null},
                        {d   , null, null, null, null, null, null}
                }), 2);
    }

    @Test
    public void checkDisposition_unvalidCells_correctOutput(){
        assertEquals(l1.checkDisposition(new Card[][]
                {
                        {null, d   , null, null, null, null, null},
                        {null, null, c   , null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, c   , null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null}
                }), 0);
    }
}
