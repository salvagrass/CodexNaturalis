package it.polimi.ingsw.view.controller.sceneutil;

import it.polimi.ingsw.util.Message;
import it.polimi.ingsw.view.controller.GameController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

public class ChatLogic {
    private final ObservableList<Message> chats;
    private final GameController game;
    private final ComboBox<String> users;
    private final Label notification;
    private String name;

    public ChatLogic(GameController game, ObservableList<Message> chats, ObservableList<String> turnOrder){
        this.game = game;
        this.users = new ComboBox<>();
        this.users.getItems().add("All");
        this.users.getItems().addAll(turnOrder);
        turnOrder.addListener((ListChangeListener<String>) change -> {
            while(change.next()){
                if (change.wasAdded()) {
                    for(String s : change.getAddedSubList()){
                        this.users.getItems().add(s);
                    }
                }
            }
        });

        this.chats = FXCollections.observableArrayList(chats);
        chats.addListener((ListChangeListener<Message>) change -> {
            while(change.next()){
                if (change.wasAdded()) {
                    this.chats.addAll(change.getAddedSubList());
                }
            }
        });
        this.notification = new Label();
    }

    public void createChat(StackPane stack){
        VBox messages = new VBox();
        this.chats.addListener((ListChangeListener<Message>) change -> Platform.runLater(() -> updMsgs(change, messages)));
        ScrollPane scroll = new ScrollPane(messages);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(300);
        scroll.setPannable(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        TextArea chat = new TextArea();
        chat.setPrefWidth(300);
        chat.setPromptText("chat...");
        chat.setWrapText(true);
        chat.setMinHeight(5);
        chat.setPrefHeight(5);

        chat.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if(!chat.getText().isEmpty()){
                    sendChatMsg(chat, this.users.getValue());
                }
                event.consume();
            }
        });

        this.users.setValue("All");
        this.users.valueProperty().addListener((obs, oldVal, newVal) -> changeChat(messages, newVal));

        HBox input = new HBox(5, this.users, chat);
        HBox.setHgrow(chat, Priority.ALWAYS);
        VBox chatBox = new VBox(5, scroll, this.notification, input);
        chatBox.setMaxHeight(800);
        chatBox.setMaxWidth(400);
        chatBox.setPadding(new Insets(0,10,0,10));
        StackPane.setAlignment(chatBox, Pos.BOTTOM_LEFT);

        chat.textProperty().addListener((obs, oldVal, newVal) -> adjustTextAreaHeight(chat));

        scroll.prefHeightProperty().bind(Bindings.createDoubleBinding(() ->
                        chatBox.getHeight() - chat.getHeight(),
                chatBox.heightProperty(), chat.heightProperty()));

        stack.getChildren().add(chatBox);

        stack.getScene().focusOwnerProperty().addListener((obs, oldVal, newVal) -> {
            if(input.getChildren().contains(newVal)){
                scroll.setVisible(true);
                this.notification.setMaxHeight(0);
                this.notification.setPrefHeight(0);
                this.notification.setMinHeight(0);
            }else{
                scroll.setVisible(false);
                this.notification.setMaxHeight(20);
                this.notification.setPrefHeight(20);
                this.notification.setMinHeight(20);
            }
        });
    }

    private void adjustTextAreaHeight(TextArea textArea) {
        Text text = new Text(textArea.getText());
        text.setFont(textArea.getFont());
        text.setWrappingWidth(textArea.getWidth());
        double textHeight = text.getLayoutBounds().getHeight();
        double newHeight = textHeight + textArea.getPadding().getTop() + textArea.getPadding().getBottom();
        textArea.setPrefHeight(Math.max(newHeight, 24));
    }

    private void updMsgs(ListChangeListener.Change<? extends Message> change, VBox messages) {
        while(change.next()){
            if (change.wasAdded()) {
                for(Message m : change.getAddedSubList()){
                    if(m.sender.equals(this.users.getValue()) && !m.receiver.equals("All") || m.receiver.equals(this.users.getValue())){
                        addMsg(messages, m);
                    }
                }
            }
        }
    }

    private void addMsg(VBox messages, Message m){
        String msg = m.sender + ": " + m.msg;
        Label label = new Label(msg);
        label.setWrapText(true);
        messages.getChildren().add(label);
        showVolatileMessage(this.notification, msg);
    }

    private void sendChatMsg(TextArea chat, String value) {
        String text = chat.getText();
        if(text.isEmpty()){
            return;
        }
        this.game.sendChatMsg(chat.getText(), value);
        chat.clear();
    }

    private void changeChat(VBox messages, String user){
        List<Message> msgs;
        if(user.equals("All")){
            msgs = this.chats.stream().filter(x -> x.receiver.equals(user)).toList();
        }else{
            msgs = this.chats.stream().filter(x ->
                    (x.sender.equals(user) && x.receiver.equals(this.name)) ||
                            (x.sender.equals(this.name) && x.receiver.equals(user))).toList();
        }
        messages.getChildren().clear();
        for(Message m : msgs){
            addMsg(messages, m);
        }
    }

    public void showVolatileMessage(Label messageLabel, String msg) {
        messageLabel.setText(msg);
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(3),
                actionEvent -> messageLabel.setText("")
        ));
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void setName(String name){
        this.name = name;
        this.users.getItems().remove(name);
    }
}
