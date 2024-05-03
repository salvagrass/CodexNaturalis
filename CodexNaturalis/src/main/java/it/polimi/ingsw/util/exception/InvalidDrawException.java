package it.polimi.ingsw.util.exception;

/**
 * Exception to be raised when attempting to draw a card from an empty deck
 */
public class InvalidDrawException extends Exception{
    public InvalidDrawException() {
        super();
    }

    public InvalidDrawException(String message) {
        super(message);
    }
}
