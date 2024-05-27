package it.polimi.ingsw.view.coreview;

import it.polimi.ingsw.model.item.card.*;
import it.polimi.ingsw.util.Symbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class representing a view of the card
 * ---------------------
 * | GOLD          3 P |
 * |-------------------|
 * | F       R       W |
 * |         R         |
 * | M       R       B |
 * |-------------------|
 * |       W W F       |
 * ---------------------
 */
public class CardView implements ItemView{

    /**
     * A record representing coordinates for ASCII art representation.
     */
    private record Coord(int x, int y, boolean side) {}

    private final String frontId, backId;
    private final Symbol color;
    private boolean face;
    private final Map<Coord, Character> asciiArt;
    private static final int asciiArtXDim = 9;
    private static final int asciiArtYDim = 21;

    /**
     * Constructor of the class.
     * 
     * @param card The card to be represented.
     */
    public CardView(Card card) {
        if (card instanceof StartingCard) {
            this.frontId = card.getId() + "_front";
            this.backId = card.getId() + "_back";
        } else if (card instanceof ResourceCard) {
            this.frontId = card.getId();
            this.backId = card.getColor().name().toLowerCase() + "_back";
        } else {
            this.frontId = card.getId();
            this.backId = "gold_" + card.getColor().name().toLowerCase() + "_back";
        }
        this.color = card.getColor();
        this.face = card.getShowingFace();
        this.asciiArt = new HashMap<>();
        this.constructHeader(card);
        card.setShowingFace(this.face);
    }

    /**
     * Sets the showing face of the card.
     *
     * @param face The showing face of the card.
     */
    public void setFace(boolean face) {
        this.face = face;
    }

    /**
     * Retrieves the showing face of the card.
     *
     * @return The showing face of the card.
     */
    public boolean getFace() {
        return this.face;

    }

    /**
     * Retrieves the color of the card
     * 
     * @return the card color as a symbol
     */
    public Symbol getColor() {
        return this.color;
    }

    /**
     * Constructs the header of the card's ASCII art based on its type.
     *
     * @param card The card for which the header is constructed.
     */
    private void constructHeader(Card card) {
        switch (card.getClass().getSimpleName()) {
            case "StartingCard" -> this.push("START", 1, 2);
            case "ResourceCard" -> {
                this.push("RES", 1, 2);
                ResourceCard resCard = (ResourceCard) card;
                this.push("" + resCard.getCardPoints(), 1, (asciiArtYDim - 1) / 2, true);
            }
            case "GoldCard" -> {
                this.push("GOLD", 1, 2);
                GoldCard goldCard = (GoldCard) card;
                this.push("" + goldCard.getCardPoints(), 1, (asciiArtYDim - 1) / 2, true);
                this.constructFooter(goldCard);
            }
            case "GoldCardCorner" -> {
                this.push("GOLD", 1, 2);
                GoldCardCorner goldCard = (GoldCardCorner) card;
                this.push("" + goldCard.getCardPoints(), 1, (asciiArtYDim - 1) / 2, true);
                this.asciiArt.put(new Coord(1, asciiArtYDim - 3, true), '■');
                this.constructFooter(goldCard);
            }
            case "GoldCardSymbol" -> {
                this.push("GOLD", 1, 2);
                GoldCardSymbol goldCard = (GoldCardSymbol) card;
                this.push("" + goldCard.getCardPoints(), 1, (asciiArtYDim - 1) / 2, true);
                this.asciiArt.put(new Coord(1, asciiArtYDim - 3, true), goldCard.getSym().INITIAL);
                this.constructFooter(goldCard);
            }
        }
        constructBody(card);
    }

    /**
     * Constructs the body of the card's ASCII art which include its corners and
     * center symbols.
     *
     * @param card The card for which the body is constructed.
     */
    private void constructBody(Card card) {
        card.setShowingFace(true);
        this.setCorners(card.getShowingCorners(), true);

        card.setShowingFace(false);
        this.setCorners(card.getShowingCorners(), false);

        Symbol[] center = card.getCenter();
        for (int i = 0; i < center.length; i++) {
            this.asciiArt.put(new Coord(4 - (center.length / 2) + i, (asciiArtYDim - 1) / 2, false), center[i].INITIAL);
        }
    }

    /**
     * Constructs the footer of a Gold Card's ASCII art, representing its
     * requirements.
     *
     * @param card The Gold Card for which the footer is constructed.
     */
    private void constructFooter(GoldCard card) {
        int i = 2;
        for (Symbol sym : card.getRequirements().keySet()) {
            int length = card.getRequirements().get(sym);
            for (int k = 0; k < length; k++, i += 2) {
                this.asciiArt.put(new Coord(asciiArtXDim - 2, i, true), sym.INITIAL);
            }
        }
    }

    /**
     * Pushes a string into the ascii representation
     * 
     * @param s     the string to be pushed
     * @param row   the row where the string should be pushed
     * @param begin the column where the string should start
     * @param side  the side on which to push the string
     */
    private void push(String s, int row, int begin, boolean side) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            this.asciiArt.put(new Coord(row, begin + i, side), s.charAt(i));
        }
    }

    /**
     * Pushes a string on both sides of the ascii representation
     * 
     * @param s     the string to be pushed
     * @param row   the row where the string should be pushed
     * @param begin the column where the string should start
     */
    private void push(String s, int row, int begin) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            this.asciiArt.put(new Coord(row, begin + i, true), s.charAt(i));
            this.asciiArt.put(new Coord(row, begin + i, false), s.charAt(i));
        }
    }

    /**
     * Pushes the corner of a card into its ascii representation
     * 
     * @param symbols the corner of the card, indexed in a clockwise pattern
     * @param side    the side on which to push the corners
     */
    private void setCorners(Symbol[] symbols, boolean side) {
        int i, counter;
        int[] xPos = new int[] { 3, 5 };
        int[] yPos = new int[] { 2, asciiArtYDim - 3 };
        int[] values = new int[] { 0, 1, 3, 2 };
        for (i = 0, counter = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++, counter++) {
                this.asciiArt.put(new Coord(xPos[i], yPos[j], side), symbols[values[counter]].INITIAL);
            }
        }
    }

    /**
     * Generates the ASCII art representation of one or more cards. The cards will
     * be represented
     * all on the same line
     *
     * @param cards The cards for which the ASCII art representation will be
     *              generated.
     * @return The ASCII art representation of the cards.
     */
    public static String showAsciiArt(CardView... cards) {
        int i, j, column, pos;
        CardView cardView;
        StringBuilder asciiArt = new StringBuilder();
        for (i = 0; i < asciiArtXDim; i++) {
            for (j = 0, column = 0, cardView = cards[0], pos = 0; j < asciiArtYDim * cards.length
                    + cards.length; j++, column++) {
                if (column % asciiArtYDim == 0 && column > 0) {
                    pos++;
                    if (pos < cards.length)
                        cardView = cards[pos];
                    column = -1;
                    asciiArt.append('\t');
                } else if (i == 0 || i == asciiArtXDim - 1) {
                    asciiArt.append('-');
                } else if (column == 0) {
                    asciiArt.append('|');
                    if (3 <= i && i <= 5)
                        asciiArt.append(cardView.color.ANSI_COLOR);
                } else if (column == asciiArtYDim - 1) {
                    if (3 <= i && i <= 5)
                        asciiArt.append(Symbol.ANSI_RESET);
                    asciiArt.append('|');
                } else if (i == 2 || i == 6)
                    asciiArt.append('-');
                else {
                    char c = cardView.asciiArt.getOrDefault(new Coord(i, column, cardView.face), ' ');
                    asciiArt.append(c);
                }
            }
            asciiArt.append('\n');
        }
        return asciiArt.toString();
    }

    /**
     * Generates the ASCII art representation of a list of cards. The cards will be
     * represented
     * all on the same line
     *
     * @param cards The list of cards for which the ASCII art representation will be
     *              generated.
     * @return The ASCII art representation of the list of cards.
     */
    public static String showAsciiArt(List<CardView> cards) {
        CardView[] temp = new CardView[cards.size()];
        for (int i = 0; i < cards.size(); i++)
            temp[i] = cards.get(i);
        return showAsciiArt(temp);
    }

    @Override
    public String getId() {
        return this.face ? this.frontId : this.backId;
    }
}
