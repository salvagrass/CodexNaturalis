package it.polimi.ingsw.view.controller;

import it.polimi.ingsw.util.ProjectLogger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

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
        Platform.runLater(this::openPopup);
    }

    private void openPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/closeGame.fxml"));
            Parent root = loader.load();

            Stage popupStage = createPopUpStage("Quit");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            ConfirmQuitController controller = loader.getController();
            controller.setStage(popupStage);
            controller.setGUI(this.gui);

            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();
        } catch (Exception e) {
            new ProjectLogger(getClass().getSimpleName()).
                    log("logs/clientLog.txt",Level.SEVERE,"Can't open popup",e);
            throw new RuntimeException(e);
        }
    }

    public void rules() throws IOException {
        Stage stage = createPopUpStage("Rules");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/rulebook.fxml")));
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
    }

    private Stage createPopUpStage(String title){
        Stage popUpStage = new Stage();
        popUpStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/icons/OpenBook32.png"))));
        popUpStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/icons/OpenBook32.png"))));
        popUpStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/icons/OpenBook64.png"))));
        popUpStage.setResizable(false);
        popUpStage.setTitle(title);
        return popUpStage;
    }
}
