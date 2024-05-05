package it.polimi.ingsw.util;

/**
 * Enumeration of every possible symbol present on a card.
 * EMPTY is an available corner without a symbol.
 * NOTAVAILABLE is a corner of a card on which you can't place another card
 */
public enum Symbol {
    WOLF("\u001B[34m"),
    GRASS("\u001B[32m"),
    BUTTERFLY("\u001B[35m"),
    FUNGUS("\u001B[31m"),
    POTION, SCROLL, FEATHER, EMPTY, NOTAVAILABLE;
    public static final String ANSI_RESET = "\u001B[0m";
    public final String ANSI_COLOR;


    Symbol(){
        this.ANSI_COLOR = ANSI_RESET;
    }

    Symbol(String color){
        this.ANSI_COLOR = color;
    }

}
