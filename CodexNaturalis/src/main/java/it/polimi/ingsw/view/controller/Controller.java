package it.polimi.ingsw.view.controller;

import it.polimi.ingsw.view.GUI;

public class Controller {
    GUI gui;

    /**
     * Sets the GUI for the object.
     *
     * @param gui The GUI to set.
     */
    public void setGui(GUI gui){
        this.gui = gui;
    }

    /**
     * Confirms an action based on the provided parameters.
     *
     * @param confirmValue The confirmation value (true or false).
     * @param confirmMsg   The confirmation message associated with the action.
     */
    public void confirm(boolean confirmValue,String confirmMsg){}
}
