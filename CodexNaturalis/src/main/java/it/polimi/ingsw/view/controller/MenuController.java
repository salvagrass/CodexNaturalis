package it.polimi.ingsw.view.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

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

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Quit");

            ConfirmQuitController controller = loader.getController();
            controller.setStage(popupStage);
            controller.setGUI(this.gui);

            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rules() throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/rulebook.fxml")));
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
    }
}
