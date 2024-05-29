package it.polimi.ingsw.view.controller;

public class MenuController extends Controller{
    public void create() {
        this.gui.scanInput("1");
    }

    public void join() {
        this.gui.scanInput("2");
    }

    public void load() {
        this.gui.scanInput("3");
    }

    public void exit() {
        this.gui.scanInput("exit");
    }
}
