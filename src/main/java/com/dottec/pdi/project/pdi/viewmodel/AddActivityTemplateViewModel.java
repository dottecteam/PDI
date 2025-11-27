package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.ActivityController;
import com.dottec.pdi.project.pdi.controllers.ActivityTemplateController;
import com.dottec.pdi.project.pdi.model.ActivityTemplate;
import com.dottec.pdi.project.pdi.model.Tag;
import com.dottec.pdi.project.pdi.utils.FXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class AddActivityTemplateViewModel {
    @FXML private TextField formAddActivityName;
    @FXML private TextField formAddActivityDescription;

    @FXML private VBox formPane;

    private ActivityTemplate activityTemplate;
    public void setActivityTemplate(ActivityTemplate activityTemplate) {
        this.activityTemplate = activityTemplate;
    }

    private TemplateGoalViewModel templateGoalViewModel;
    public void setTemplateGoalViewModel(TemplateGoalViewModel templateGoalViewModel) {
        this.templateGoalViewModel = templateGoalViewModel;
    }

    private boolean creatingGoalTemplateMode = false;
    public void setCreatingGoalTemplateMode(boolean creatingGoalTemplateMode){this.creatingGoalTemplateMode = creatingGoalTemplateMode;}

    @FXML private void initialize(){
        formAddActivityName.clear();
        formPane.getChildren().stream()     //Input fields labels dynamics
                .filter(node -> node instanceof StackPane)
                .forEach(stack -> {
                    StackPane stackPane = (StackPane) stack;
                    Node input = stackPane.getChildren().get(1);
                    Label label = (Label) stackPane.getChildren().get(0);

                    input.focusedProperty().addListener((obs, oldVal, newVal) -> updateLabel(input, label));
                });
    }

    private void updateLabel(Node node, Label label) {
        if (node.isFocused() || FXUtils.isFilled(node)) {
            label.getStyleClass().add("formInput-label-focused");
        } else {
            label.getStyleClass().remove("formInput-label-focused");
        }
    }

    private void updateActivityTemplate(){
        activityTemplate.setName(formAddActivityName.getText());
        activityTemplate.setDescription(formAddActivityDescription.getText());
    }

    @FXML
    void handleConfirm(ActionEvent actionEvent){
        if (creatingGoalTemplateMode){
            updateActivityTemplate();
            activityTemplate.getGoalTemplate().addActivityTemplate(activityTemplate);  //Add the activity to the goal's list
            templateGoalViewModel.addActivityTemplate(activityTemplate);
            TemplateViewModel.showSuccessMessage("Atividade adicionada com sucesso!");
            TemplateViewModel.goBack();
        } else {
            updateActivityTemplate();
            if(templateGoalViewModel !=null) templateGoalViewModel.addActivityTemplate(activityTemplate);
            ActivityTemplateController.saveActivityTemplate(activityTemplate);
            TemplateViewModel.showSuccessMessage("Atividade adicionada com sucesso!");
            TemplateViewModel.goBack();
        }
    }

    @FXML
    void handleCancel(ActionEvent actionEvent){
        TemplateViewModel.goBack();
    }
}
