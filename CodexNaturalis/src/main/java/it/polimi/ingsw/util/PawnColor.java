package it.polimi.ingsw.util;

/**
 * Enumeration of every color available for a player's pawn.
 */
public enum PawnColor {
    RED("rgb(128, 0, 0)"), YELLOW("196, 161, 2"), GREEN("rgb(0, 128, 0)"), BLUE("rgb(0, 0, 128)");
    final String color;
    PawnColor(String color){
        this.color = color;
    }
}
