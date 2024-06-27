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

    /**
     * Submit the create choice.
     */
    public void create() {
        this.gui.scanInput("1");
    }

    /**
     * Submit the join choice.
     */
    public void join() {
        this.gui.scanInput("2");
    }

    /**
     * Submit the load choice.
     */
    public void load() {
        this.gui.scanInput("3");
    }

    /**
     * Opens the quit popup.
     */
    public void exit() {
        Platform.runLater(this::openPopup);
    }

    /**
     * Opens a modal popup window for quitting the application.
     *
     * @throws RuntimeException if there's an issue loading the FXML or creating the popup.
     */
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

    /**
     * Displays a modal popup window showing the game rules.
     *
     * @throws IOException if there's an issue loading the FXML or creating the popup.
     */
    public void rules() throws IOException {
        Stage stage = createPopUpStage("Rules");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/rulebook.fxml")));
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
    }

    /**
     * Creates and configures a new JavaFX Stage for displaying a popup window.
     *
     * @param title The title to set for the popup window.
     * @return A new Stage configured with the specified title and icon.
     */
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
