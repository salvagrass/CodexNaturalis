package it.polimi.ingsw;

import it.polimi.ingsw.view.CLI;

public class TestClientSocket {
    public static void main(String[] args) throws Exception{
        CLI userView = new CLI(true);
        userView.tryConnection("localhost");
    }
}
