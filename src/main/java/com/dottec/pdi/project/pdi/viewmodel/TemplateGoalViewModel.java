package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.GoalTemplatesController;
import com.dottec.pdi.project.pdi.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class TemplateGoalViewModel {
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;

    @FXML private ImageView editButton;
    @FXML private Button confirmEditButton;
    @FXML private Button cancelEditButton;

    @FXML private VBox activityTemplatesField;

    private TemplateGoalViewModel templateGoalViewModel;
    public void setTemplateGoalViewModel(TemplateGoalViewModel templateGoalViewModel){
        this.templateGoalViewModel = templateGoalViewModel;
    }

    private GoalTemplate goalTemplate;
    public void setGoalTemplate(GoalTemplate goalTemplate) {
        this.goalTemplate = goalTemplate;
    }

    private boolean creatingGoalTemplateMode = false;

    @FXML
    private void initialize(){
        Platform.runLater(() -> {
            if(goalTemplate == null){
                enableCreationMode();
                updateFields();
            } else {    //Update the fields for the selected goal
                updateFields();
                createAddActivityTemplateButton();
                populateActivityTemplates();
            }
        });
    }

    private void updateFields(){
        if(goalTemplate != null) {
            nameField.setText(goalTemplate.getGoa_tmp_name());
            descriptionField.setText(goalTemplate.getGoa_tmp_description());
        }
    }

    private void createGoalTemplate(){
        GoalTemplate goalTemplate = new GoalTemplate();
        goalTemplate.setGoa_tmp_name("");
        goalTemplate.setGoa_tmp_description("");
        goalTemplate.setActivityTemplates(new ArrayList<>());
        this.goalTemplate = goalTemplate;
    }

    private void createAddActivityTemplateButton(){
        Button addActivityTemplate = new Button("Adicionar Atividade");
        addActivityTemplate.getStyleClass().add("basic-button");
        addActivityTemplate.setOnMouseClicked(mouseEvent -> {
            ActivityTemplate activityTemplate = new ActivityTemplate();
            activityTemplate.setGoalTemplate(this.goalTemplate);
            TemplateViewModel.switchScreen("AddActivityTemplate.fxml", controller -> {
                if(controller instanceof AddActivityTemplateViewModel addActivityTemplateViewModel){
                    addActivityTemplateViewModel.setActivityTemplate(activityTemplate);
                    addActivityTemplateViewModel.setTemplateGoalViewModel(templateGoalViewModel);
                    addActivityTemplateViewModel.setCreatingGoalTemplateMode(creatingGoalTemplateMode);
                }
            });
        });
        HeaderViewModel.addButton(addActivityTemplate);
    }

    private void configHeader(){
        HeaderViewModel.clear();
        HeaderViewModel.setLabel("Adicionar Modelo ");
        HeaderViewModel.setReturnButtonVisible(true);

        createAddActivityTemplateButton();

        Button conclude = new Button("Concluir");
        conclude.getStyleClass().add("basic-button");
        conclude.setOnMouseClicked(e -> concludeGoalCreation());

        Button cancel = new Button("Cancelar");
        cancel.getStyleClass().add("cancel-button");
        cancel.setOnMouseClicked(e -> TemplateViewModel.goBack());

        HeaderViewModel.addButton(conclude);
        HeaderViewModel.addButton(cancel);
    }

    private void enableCreationMode(){  //Set the name, descripton and add actvities, then update the database
        creatingGoalTemplateMode = true;
        createGoalTemplate(); //Create a new goal
        Platform.runLater(this::configHeader);

        //Enter editable mode
        nameField.setEditable(true);
        nameField.getStyleClass().remove("label-not-editable");
        nameField.getStyleClass().add("label-editable");

        descriptionField.setEditable(true);
        descriptionField.getStyleClass().remove("label-not-editable");
        descriptionField.getStyleClass().add("label-editable");

        editButton.setVisible(false);
    }

    private void concludeGoalCreation(){
        goalTemplate.setGoa_tmp_name(nameField.getText());
        goalTemplate.setGoa_tmp_description(descriptionField.getText());

        if(nameField.getText().isBlank()){
            TemplateViewModel.showErrorMessage("Por favor, preencha o nome do modelo.");
            return;
        }

        boolean saved = GoalTemplatesController.addGoalTemplate(goalTemplate);
        if(!saved){
            TemplateViewModel.showErrorMessage("Não foi possível adicionar o modelo.");
            return;
        }

        disableCreationMode();
        creatingGoalTemplateMode = false;
        TemplateViewModel.showSuccessMessage("Modelo Meta criada com sucesso!");
        HeaderViewModel.updateHeader("TemplateGoal.fxml");
    }

    private void disableCreationMode(){
        disableEditingState();
        HeaderViewModel.clearButtons();
        HeaderViewModel.clearButtons();
        createAddActivityTemplateButton();
    }

        public void addActivityTemplate(ActivityTemplate activityTemplate){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dottec/pdi/project/pdi/views/ActivityTemplate.fxml"));
            Parent activityPane = loader.load();
            activityPane.maxWidth(activityTemplatesField.getWidth());

            ActivityTemplateViewModel controller = loader.getController();
            controller.setActivityTemplate(activityTemplate);
            controller.updateFields();
            controller.setCreatingGoalTemplateMode(creatingGoalTemplateMode);
            controller.setTemplateGoalViewModel(templateGoalViewModel);

            activityTemplatesField.getChildren().add(activityPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeActivityTemplate(ActivityTemplate activityTemplate){
        goalTemplate.getActivityTemplates().remove(activityTemplate);
        populateActivityTemplates();
    }

    public void populateActivityTemplates() {
        activityTemplatesField.getChildren().clear();
        if(goalTemplate != null && !goalTemplate.getActivityTemplates().isEmpty()) {
            goalTemplate.getActivityTemplates().forEach(this::addActivityTemplate);
        } else if(goalTemplate == null) {
            System.out.println("GoalTemplate is null");
        } else {
            System.out.println("GoalTemplate has no activityTemplates");
            Label label = new Label("Adicione Atividades.");
            label.getStyleClass().add("mid-label");
            activityTemplatesField.getChildren().add(label);
        }
    }

    @FXML
    private void handleEnableEditing() {    //Enable editing mode for name and description
        nameField.setEditable(true);
        nameField.getStyleClass().remove("label-not-editable");
        nameField.getStyleClass().add("label-editable");

        descriptionField.setEditable(true);
        descriptionField.getStyleClass().remove("label-not-editable");
        descriptionField.getStyleClass().add("label-editable");

        editButton.setVisible(false);
        confirmEditButton.setVisible(true);
        cancelEditButton.setVisible(true);
    }

    @FXML
    private void handleCancelEditing() {
        updateFields();
        disableEditingState();
    }

    @FXML
    private void handleConfirmEdit() {
        if (nameField.getText().trim().isEmpty()) {
            TemplateViewModel.showErrorMessage("O nome não pode estar vazio.");
            return;
        }

        goalTemplate.setGoa_tmp_name(nameField.getText());
        goalTemplate.setGoa_tmp_description(descriptionField.getText());
        GoalTemplatesController.updateGoalTemplate(goalTemplate);

        TemplateViewModel.showSuccessMessage("Meta Modelo atualizada com sucesso!");

        disableEditingState();
    }

    private void disableEditingState() {
        nameField.setEditable(false);
        nameField.getStyleClass().add("label-not-editable");
        nameField.getStyleClass().remove("label-editable");

        descriptionField.setEditable(false);
        descriptionField.getStyleClass().add("label-not-editable");
        descriptionField.getStyleClass().remove("label-editable");

        editButton.setVisible(true);
        confirmEditButton.setVisible(false);
        cancelEditButton.setVisible(false);
    }
}
