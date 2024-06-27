package it.polimi.ingsw.util.exception;


/**
 * Exception to be raised when using invalid selection indexes.
 */
public class InvalidSelectionException extends Exception{
    /**
     * Exception to be raised when using invalid selection indexes.
     *
     * @param message Printable message.
     */
    public InvalidSelectionException(String message) {
        super(message);
    }
}
