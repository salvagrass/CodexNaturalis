package it.polimi.ingsw.view.controller;

public class StartController extends Controller {
    public void connect(){
        try {
            this.gui.tryConnection("127.0.0.1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
