package it.polimi.ingsw.util.exception;


/**
 * Exception to be raised when using invalid selection indexes
 */
public class InvalidSelectionException extends Exception{
    public InvalidSelectionException() {
        super();
    }

    public InvalidSelectionException(String message) {
        super(message);
    }
}
