package it.polimi.ingsw.controller.serverfunction;

import it.polimi.ingsw.controller.connection.Comm;

public interface CrashHandler {
    void handleCrash(Comm comm);
}
