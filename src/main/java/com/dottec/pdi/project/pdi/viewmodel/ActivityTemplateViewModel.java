package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.ActivityTemplateController;
import com.dottec.pdi.project.pdi.dao.ActivityTemplateDAO;
import com.dottec.pdi.project.pdi.model.ActivityTemplate;
import com.dottec.pdi.project.pdi.utils.FXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;


public class ActivityTemplateViewModel {
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private ImageView editButton;
    @FXML private ImageView deleteButton;
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;

    private ActivityTemplate activityTemplate;
    private boolean creatingGoalTemplateMode = false;
    public void setCreatingGoalTemplateMode(boolean creatingGoalTemplateMode){this.creatingGoalTemplateMode = creatingGoalTemplateMode;}
    private TemplateGoalViewModel templateGoalViewModel;
    public void setTemplateGoalViewModel(TemplateGoalViewModel templateGoalViewModel){
        this.templateGoalViewModel=templateGoalViewModel;
    }

    public void setActivityTemplate(ActivityTemplate activityTemplate) {
        this.activityTemplate = activityTemplate;
    }

    @FXML void initialize(){
        buttonVisible(false, confirmButton, cancelButton);
    }

    public void updateFields(){
        nameField.setText(activityTemplate.getName());
        descriptionField.setText(activityTemplate.getDescription());
    }

    private void buttonVisible(Boolean visible, Node... buttons){
        for(Node button : buttons){
            button.setVisible(visible);
            button.setManaged(visible);
        }
    }

    @FXML
    private void handleEnableEditing(){
        buttonVisible(false, editButton, deleteButton);
        buttonVisible(true, confirmButton, cancelButton);

        confirmButton.setOnMouseClicked(mouseEvent -> {handleConfirmEdit();});
        cancelButton.setOnMouseClicked(mouseEvent -> {handleCancelEditing();});

        nameField.getStyleClass().remove("label-not-editable");
        nameField.getStyleClass().add("label-editable");
        nameField.setMouseTransparent(false);
        nameField.setEditable(true);

        descriptionField.getStyleClass().remove("label-not-editable");
        descriptionField.getStyleClass().add("label-editable");
        descriptionField.setEditable(true);
    }

    @FXML
    private void handleCancelEditing() {
        updateFields();
        disableEditingState();
    }

    @FXML
    private void handleConfirmEdit() {
        if (nameField.getText().trim().isEmpty()) {
            FXUtils.showErrorMessage("O nome não pode estar vazio.");
            return;
        }

        activityTemplate.setName(nameField.getText());
        activityTemplate.setDescription(descriptionField.getText());

        if(!creatingGoalTemplateMode){
            ActivityTemplateDAO.update(activityTemplate);
            FXUtils.showSuccessMessage("Atividade Modelo atualizada com sucesso!");
        }

        disableEditingState();
    }

    private void disableEditingState() {
        nameField.setEditable(false);
        nameField.getStyleClass().add("label-not-editable");
        nameField.setMouseTransparent(true);
        nameField.getStyleClass().remove("label-editable");

        descriptionField.setEditable(false);
        descriptionField.getStyleClass().add("label-not-editable");
        descriptionField.getStyleClass().remove("label-editable");

        buttonVisible(true, deleteButton, editButton);
        buttonVisible(false, cancelButton, confirmButton);
    }

    @FXML
    private void handleEnableDelete(){
        buttonVisible(false, editButton, deleteButton);
        buttonVisible(true, cancelButton, confirmButton);

        confirmButton.setOnMouseClicked(mouseEvent -> {handleConfirmDelete();});
        cancelButton.setOnMouseClicked(mouseEvent -> {handleCancelEditing();});
    }

    @FXML
    private void handleConfirmDelete(){
        if(creatingGoalTemplateMode){
            templateGoalViewModel.removeActivityTemplate(activityTemplate);
        } else {
            templateGoalViewModel.removeActivityTemplate(activityTemplate);
            ActivityTemplateController.deleteActivityTemplate(activityTemplate);
        }
        FXUtils.showSuccessMessage("Atividade excluída com sucesso!");
    }
}
