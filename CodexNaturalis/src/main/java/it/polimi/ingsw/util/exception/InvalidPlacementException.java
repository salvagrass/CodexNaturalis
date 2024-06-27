package it.polimi.ingsw.util.exception;

/**
 * Exception to be raised when attempting to place a card in an invalid position.
 */
public class InvalidPlacementException extends Exception{
    /**
     * Exception to be raised when attempting to place a card in an invalid position.
     */
    public InvalidPlacementException() {
        super();
    }

    /**
     * Exception to be raised when attempting to place a card in an invalid position.
     *
     * @param message Printable message.
     */
    public InvalidPlacementException(String message) {
        super(message);
    }
}
