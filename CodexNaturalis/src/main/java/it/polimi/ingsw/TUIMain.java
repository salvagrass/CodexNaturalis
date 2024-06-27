package it.polimi.ingsw;

import it.polimi.ingsw.util.ProjectLogger;
import it.polimi.ingsw.view.TUI;

/**
 * TUI main.
 */
public class TUIMain {
    public static void main(String[] args) throws Exception {
        ProjectLogger pj = new ProjectLogger("Main");
        pj.clearFile("logs/clientLog.txt");
        pj.clearFile("logs/commLog.txt");
        TUI view = new TUI();
        view.startGame();
    }
}
