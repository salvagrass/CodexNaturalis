package it.polimi.ingsw;

import it.polimi.ingsw.controller.userclient.ClientRMI;
import it.polimi.ingsw.view.CLI;

public class TestClient {

 public static void main(String[] args) throws Exception{
        ClientRMI client = new ClientRMI(new CLI());
        client.connect("localhost", 1099);
    }
}
