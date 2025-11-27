package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.GoalTemplatesController;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.GoalTemplate;
import com.mysql.cj.protocol.x.XProtocolRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class AddGoalFromTemplateViewModel {
    @FXML private VBox goalTemplatesVBox;

    @FXML public void initialize() {
        loadData();
    }

    private void loadData() {
        List<GoalTemplate> allGoalTeplates = GoalTemplatesController.findAllGoalTemplates();

        for(GoalTemplate goalTemplate : allGoalTeplates) {
            createListItem(goalTemplate);
        }
    }

    public void createListItem(GoalTemplate goalTemplate) {
        HBox hbox = new HBox(10);
        hbox.getStyleClass().add("goal-template-structure");
        Label nameLabel = new Label();
        Label descriptionLabel = new Label();
        Region spacer = new Region();
        Button addButton = new Button("Adicionar");

        HBox.setHgrow(spacer, Priority.ALWAYS);
        hbox.setAlignment(Pos.CENTER_LEFT);
        addButton.getStyleClass().add("basic-button");
        addButton.setScaleX(0.8);
        addButton.setScaleY(0.8);

        addButton.setOnAction(event -> {
            if (goalTemplate != null) {
                addButton.setDisable(true);
                addButton.setText("Adicionado");
            }
        });
        nameLabel.setText(goalTemplate.getGoa_tmp_name());
        nameLabel.setStyle("-fx-text-fill: #4B0081;");
        descriptionLabel.setText(goalTemplate.getGoa_tmp_description());
        descriptionLabel.setStyle("-fx-padding: 3 0 0 10;");
        VBox vBox = new VBox(nameLabel, descriptionLabel);
        vBox.setStyle("-fx-padding: 5; -fx-text-fill: black;");

        hbox.getChildren().clear();
        hbox.getChildren().addAll(vBox, spacer);

        addButton.setDisable(false);
        addButton.setText("Adicionar");
        hbox.getChildren().add(addButton);

        goalTemplatesVBox.getChildren().add(hbox);
    }
}