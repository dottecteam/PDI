package com.dottec.pdi.project.pdi.utils;

import com.dottec.pdi.project.pdi.viewmodel.TemplateViewModel;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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
        buildMessageBox(false, mainStackPane, message, headerMessage);
    };
    public static void buildMessageBox(Boolean error, StackPane mainStackPane, String message, String headerMessage){
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

        vbox.getChildren().addAll(header, label);
        stackPane.getChildren().add(vbox);

        System.out.println(message);

        mainStackPane.getChildren().addLast(stackPane);

        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        delay.setOnFinished(e -> mainStackPane.getChildren().remove(stackPane));
        delay.play();

        mainStackPane.setOnMouseClicked(mouseEvent -> {
            mainStackPane.getChildren().remove(stackPane);
        });
    }
}
