package it.polimi.ingsw;

import it.polimi.ingsw.view.UserView;
import it.polimi.ingsw.view.CLI;

public class TestClient {
    public static void main(String[] args) throws Exception{
        UserView view = new CLI(false);
        view.tryConnection("localhost");
    }
}
