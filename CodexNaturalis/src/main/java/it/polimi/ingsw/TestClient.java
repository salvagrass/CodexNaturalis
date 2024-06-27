package it.polimi.ingsw;

import it.polimi.ingsw.util.ProjectLogger;
import it.polimi.ingsw.view.CLI;

public class TestClient {
    public static void main(String[] args) throws Exception {
        ProjectLogger pj = new ProjectLogger("Main");
        pj.clearFile("logs/clientLog.txt");
        pj.clearFile("logs/commLog.txt");
        CLI view = new CLI();
        view.startGame();
    }
}
