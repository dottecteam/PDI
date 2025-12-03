package com.dottec.pdi.project.pdi.utils;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;

public class FXUtils {
    private FXUtils(){};

    public static boolean isFilled(Node node) {
        if (node instanceof TextField textField) {
            return textField.getText() != null && !textField.getText().trim().isEmpty();
        }
        else if (node instanceof TextArea textArea) {
            return textArea.getText() != null && !textArea.getText().trim().isEmpty();
        }
        else if (node instanceof ChoiceBox<?> choiceBox) {
            return choiceBox.getValue() != null;
        }
        else if (node instanceof ComboBox<?> comboBox) {
            return comboBox.getValue() != null;
        }
        else if (node instanceof DatePicker datePicker) {
            return datePicker.getValue() != null;
        }
        else if (node instanceof CheckBox checkBox) {
            return checkBox.isSelected();
        }
        else {
            return node != null;
        }
    }

    public static void buildMessageBox(StackPane mainStackPane, String message, String headerMessage){
        buildMessageBox(false, mainStackPane, message, headerMessage, true);
    }

    public static void buildMessageBox(Boolean error, StackPane mainStackPane, String message, String headerMessage, boolean autoClose, Node... nodes){
        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().addAll("message");

        VBox vbox = new VBox();

        HBox header = new HBox();
        header.getStyleClass().add("message-header");


        if(error) {
            header.getStyleClass().add("message-header-error");
        } else {
            header.getStyleClass().add("message-header-success");
        }

        Label headerLabel = new Label(headerMessage);
        headerLabel.getStyleClass().add("message-header-label");

        Button closeButton = new Button("X");
        closeButton.getStyleClass().addAll("close-button");
        closeButton.setOnAction(actionEvent -> {
            mainStackPane.getChildren().remove(stackPane);
        });
        header.getChildren().addAll(headerLabel, closeButton);

        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(500);
        label.setStyle("-fx-padding: 10");

        for(Node node : nodes){
            if(node instanceof Button){
                node.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> mainStackPane.getChildren().remove(stackPane));
            }
        }

        HBox nodesField = new HBox(nodes);
        nodesField.setSpacing(10);
        nodesField.setStyle("-fx-padding: 10;");
        nodesField.setAlignment(Pos.CENTER_RIGHT);

        vbox.getChildren().addAll(header, label, nodesField);
        stackPane.getChildren().add(vbox);

        mainStackPane.getChildren().addLast(stackPane);

        if(autoClose){
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> mainStackPane.getChildren().remove(stackPane));
            delay.play();
        }

        mainStackPane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if(!mainStackPane.getChildren().contains(stackPane)) return;
            if (!stackPane.contains(stackPane.screenToLocal(e.getScreenX(), e.getScreenY()))) {
                mainStackPane.getChildren().remove(stackPane);
            }
        });
    }

    public static Button buildConfirmationMessageBox(StackPane mainStackPane, String message, String headerMessage){
        Button confirm = new Button("Confirmar");
        confirm.getStyleClass().add("basic-button");

        buildMessageBox(false, mainStackPane, message, headerMessage, false, confirm);

        return confirm;
    }
}
