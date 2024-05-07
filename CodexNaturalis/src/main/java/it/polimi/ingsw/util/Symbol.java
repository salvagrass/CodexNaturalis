package it.polimi.ingsw.util;

/**
 * Enumeration of every possible symbol present on a card.
 * EMPTY is an available corner without a symbol.
 * NOTAVAILABLE is a corner of a card on which you can't place another card
 */
public enum Symbol {
    WOLF("\u001B[34m", 'W'),
    GRASS("\u001B[32m", 'G'),
    BUTTERFLY("\u001B[35m", 'B'),
    FUNGUS("\u001B[31m", 'M'),
    POTION("\u001B[33m", 'P'),
    SCROLL("\u001B[33m", 'S'),
    FEATHER("\u001B[33m", 'F'),
    EMPTY('E'),
    NOTAVAILABLE('N');
    public static final String ANSI_RESET = "\u001B[0m";
    public final String ANSI_COLOR;
    public final char INITIAL;

    /**
     * Constructs a Symbol with the given initial character.
     *
     * @param initial The character representing the symbol.
     */
    Symbol(char initial) {
        this.ANSI_COLOR = ANSI_RESET;
        this.INITIAL = initial;
    }

    /**
     * Constructs a Symbol with the given color and initial character.
     *
     * @param color   The ANSI color code for the symbol.
     * @param initial The character representing the symbol.
     */
    Symbol(String color, char initial){
        this.ANSI_COLOR = color;
        this.INITIAL=initial;
    }

    /**
     * Retrieves the Symbol corresponding to the given initial character. If no match is found,
     * the NOTAVAILABLE instance is returned.
     *
     * @param c The initial character of the Symbol to retrieve.
     * @return The Symbol corresponding to the initial character, or NOTAVAILABLE if no match is found.
     */
    public static Symbol symbolFromInit(char c){
        return switch(c){
            case 'W' -> Symbol.WOLF;
            case 'G' -> Symbol.GRASS;
            case 'B' -> Symbol.BUTTERFLY;
            case 'M' -> Symbol.FUNGUS;
            case 'P' -> Symbol.POTION;
            case 'S' -> Symbol.SCROLL;
            case 'F' -> Symbol.FEATHER;
            case 'E' -> Symbol.EMPTY;
            default -> Symbol.NOTAVAILABLE;
        };
    }
}
