package it.polimi.ingsw.util;

public enum GameScene {
    START("start"), MENU("menu"), CHOOSEITEM("chooseItem"), COLOR("color"), CREATE("create"), IDSCENE("idScene"),
    NAME("name"), GAME("game"), WAITPLAYERS("waitPlayers");
    public final String path;
    GameScene(String path){
        this.path = "/fxml/" + path + ".fxml";
    }
}
