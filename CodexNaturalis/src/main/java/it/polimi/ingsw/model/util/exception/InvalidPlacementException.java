package it.polimi.ingsw.model.util.exception;

/**
 * Exception to be raised when attempting to place a card in an invalid position
 */
public class InvalidPlacementException extends Exception{
    public InvalidPlacementException() {
        super();
    }

    public InvalidPlacementException(String message) {
        super(message);
    }
}
