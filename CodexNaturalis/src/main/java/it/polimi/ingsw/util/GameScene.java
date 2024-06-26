package it.polimi.ingsw.util;

/**
 * Represents different scenes in the game.
 */
public enum GameScene {
    START("start"), MENU("menu"), CHOOSEITEM("chooseItem"), COLOR("color"), CREATE("create"), IDSCENE("idScene"),
    NAME("name"), GAME("game"), WAITPLAYERS("waitPlayers"), ENDGAME("endGame"), CRASHED("crashed");
    public final String path;
    GameScene(String path){
        this.path = "/fxml/" + path + ".fxml";
    }
}
