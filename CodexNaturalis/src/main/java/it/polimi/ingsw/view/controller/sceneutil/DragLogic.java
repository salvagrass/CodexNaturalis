package it.polimi.ingsw.view.controller.sceneutil;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class DragLogic {
    private final List<VBox> boxes;
    private final ScrollPane scrollPane;
    private double mouseX, mouseY;
    private boolean draggable;

    /**
     * Constructs a new DragLogic instance.
     *
     * @param scrollPane The ScrollPane to which draggable boxes are attached.
     */

    public DragLogic(ScrollPane scrollPane) {
        this.boxes = new ArrayList<>();
        this.scrollPane = scrollPane;
    }

    /**
     * Retrieves the list of VBox instances representing draggable boxes.
     *
     * @return The list of VBox instances.
     */
    public List<VBox> getBoxes(){
        return this.boxes;
    }

    /**
     * Sets the draggable property for this instance.
     *
     * @param value The boolean value to set (true for draggable, false otherwise).
     */
    public void setDraggable(boolean value){
        this.draggable = value;
    }

    /**
     * Makes the specified ImageView draggable within the given constraints.
     *
     * @param view The ImageView to make draggable.
     * @param addImage A BiConsumer that accepts an ImageView and a VBox to associate the image with a box.
     */
    public void makeDraggable(ImageView view, BiConsumer<ImageView, VBox> addImage) {
        view.setOnMousePressed(event -> {
            if(this.draggable){
                view.setUserData(new double[]{view.getTranslateX(), view.getTranslateY()});
                this.mouseX = event.getSceneX() - view.getTranslateX();
                this.mouseY = event.getSceneY() - view.getTranslateY();
                event.consume();
            }
        });

        view.setOnMouseDragged(event -> {
            if(this.draggable){
                view.setTranslateX(event.getSceneX() - mouseX);
                view.setTranslateY(event.getSceneY() - mouseY);
            }
        });

        view.setOnMouseReleased(event -> {
            if(this.draggable){
                Bounds bounds = view.localToScene(view.getBoundsInLocal());
                Point2D eventPoint = new Point2D(bounds.getCenterX(),bounds.getCenterY());
                Bounds scrollBounds = scrollPane.localToScene(scrollPane.getBoundsInLocal());
                for (VBox box : this.boxes) {
                    Bounds boxBounds = box.localToScene(box.getBoundsInLocal());
                    if (boxBounds.contains(eventPoint) && scrollBounds.contains(boxBounds)) {
                        addImage.accept(view,box);
                        view.setVisible(false);
                        break;
                    }
                }
                double[] initialPosition = (double[]) view.getUserData();
                view.setTranslateX(initialPosition[0]);
                view.setTranslateY(initialPosition[1]);
            }
        });
    }
}
