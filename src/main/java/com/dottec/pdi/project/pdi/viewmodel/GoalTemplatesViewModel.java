package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.GoalTemplatesController;
import com.dottec.pdi.project.pdi.model.GoalTemplate;
import com.dottec.pdi.project.pdi.utils.FXUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import javax.annotation.processing.Generated;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GoalTemplatesViewModel implements Initializable {
    @FXML private VBox mainPane;
    @FXML private GridPane gridGoalTemplates;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        populateGoalTemplates();
        Button addGoalTemplate = new Button("Adicionar Modelo");
        addGoalTemplate.getStyleClass().add("basic-button");
        addGoalTemplate.setOnMouseClicked(eve -> {
            TemplateViewModel.switchScreen("TemplateGoal.fxml", controller -> {
                if(controller instanceof TemplateGoalViewModel templateGoalViewModel){
                    templateGoalViewModel.setTemplateGoalViewModel(templateGoalViewModel);
                }
                HeaderViewModel.updateHeader("TemplateGoal.fxml");
            });
        });
        HeaderViewModel.addButton(addGoalTemplate);
    }

    private void populateGoalTemplates(){
        List<GoalTemplate> goalTemplates = GoalTemplatesController.findAllGoalTemplates();

        gridGoalTemplates.getChildren().clear();

        for(int i = 0; i < goalTemplates.size(); i++){
            HBox templateCard = buildGoalTemplateStructure(goalTemplates.get(i));
            int col = i % 3;
            int row = i / 3;
            gridGoalTemplates.add(templateCard, col, row);
        }
    }

    private HBox buildGoalTemplateStructure(GoalTemplate goalTemplate){
        Label name = new Label(goalTemplate.getGoa_tmp_name());
        name.setStyle("-fx-text-fill: #4B0081; -fx-font-size: 18;");
        Label description = new Label(goalTemplate.getGoa_tmp_description());
        description.setStyle("-fx-padding: 3 0 0 15; -fx-font-size: 14;");
        description.setWrapText(true);

        VBox vBox = new VBox(name, description);

        Label activityCount = new Label();
        int count = goalTemplate.getActivityTemplates().size();
        activityCount.setText(count + " atividades");
        activityCount.setStyle("-fx-font-size: 12; -fx-min-width: 70");

        ImageView iv = new ImageView(
                new Image(getClass().getResource("/com/dottec/pdi/project/pdi/static/img/trash.png").toExternalForm()));
        iv.setFitWidth(20);
        iv.setFitHeight(20);
        Button deleteButton = new Button();
        deleteButton.setGraphic(iv);
        deleteButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 3;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox details = new VBox(deleteButton, spacer, activityCount);
        details.setAlignment(Pos.TOP_RIGHT);

        HBox hBox = new HBox(vBox, details);
        hBox.getStyleClass().add("hbox-goal-template");

        HBox.setHgrow(vBox, Priority.ALWAYS);

        hBox.setOnMouseClicked(e -> {
            TemplateViewModel.switchScreen("TemplateGoal.fxml", controller -> {
                if(controller instanceof TemplateGoalViewModel templateGoalViewModel){
                    templateGoalViewModel.setGoalTemplate(goalTemplate);
                }
            });
        });

        deleteButton.setOnMouseClicked(e ->
                FXUtils.showConfirmationMessage("Deseja deletar o modelo?").setOnMouseClicked(e2 -> {
                    GoalTemplatesController.deleteGoalTemplate(goalTemplate.getGoa_tmp_id());
                    FXUtils.showSuccessMessage("Modelo deletado com sucesso!");
                    gridGoalTemplates.getChildren().remove(hBox);
                }));

        return hBox;
    }
}
