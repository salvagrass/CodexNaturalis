package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.Comm;

/**
 * Interface representing an object that handles client crashes.
 * Implementations of this interface provide the logic to handle
 * situations where a client unexpectedly disconnects or crashes.
*/
public interface CrashHandler {
    /**
     * Method called when a client crash is detected
     * @param comm the comm that was used to communicate with the crashed client
     *
     * @see Comm
     */
    void handleCrash(Comm comm);
}
