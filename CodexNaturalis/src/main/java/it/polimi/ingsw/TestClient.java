package it.polimi.ingsw;

import it.polimi.ingsw.view.UserView;
import it.polimi.ingsw.view.CLI;

import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) throws Exception {
        CLI view = new CLI();
        view.startGame();
    }
}
