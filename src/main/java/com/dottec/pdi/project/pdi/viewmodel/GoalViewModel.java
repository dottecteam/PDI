package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.ActivityController;
import com.dottec.pdi.project.pdi.controllers.GoalController;
import com.dottec.pdi.project.pdi.enums.GoalStatus;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Goal;
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
import java.time.LocalDate;

public class GoalViewModel {
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;

    @FXML private ImageView editButton;
    @FXML private Button confirmEditButton;
    @FXML private Button cancelEditButton;

    @FXML private VBox activitiesField;

    private GoalViewModel goalViewModel;
    public void setGoalViewModel(GoalViewModel goalViewModel){
        this.goalViewModel = goalViewModel;
    }

    private Goal goal;
    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    private Collaborator collaborator;
    public void setCollaborator(Collaborator collaborator){this.collaborator = collaborator;}

    private boolean creatingGoalMode = false;

    @FXML
    private void initialize(){
        Platform.runLater(() -> {
            if(goal == null){   //Create a new goal if the create goal button was pressed
                enableCreationMode();
                updateFields();
            } else {    //Update the fields for the selected goal
                updateFields();
                createAddActivityButton();
                goal.setActivities(ActivityController.findActivitiesByGoalId(goal.getId()));
                populateActivities();
            }
        });
    }

    private void updateFields(){
        if(goal != null) {  //Update goal's name and description
            nameField.setText(goal.getName());
            descriptionField.setText(goal.getDescription());
        }
    }

    private void createGoal(){  //Create a new goal
        Goal goal = new Goal();
        goal.setName("");
        goal.setDescription("");
        goal.setStatus(GoalStatus.in_progress);
        this.goal = goal;
    }

    private void createAddActivityButton(){
        Button addActivity = new Button("Adicionar Atividade");
        addActivity.getStyleClass().add("basic-button");
        addActivity.setOnMouseClicked(mouseEvent -> {
            Activity activity = new Activity(); //Create a new activity
            activity.setGoal(this.goal);    //Set this as the activity's goal
            TemplateViewModel.switchScreen("AddActivity.fxml", controller -> {
                if(controller instanceof AddActivityViewModel addActivityViewModel){
                    addActivityViewModel.setActivity(activity);
                    addActivityViewModel.setGoalViewModel(goalViewModel);
                    addActivityViewModel.setCreatingGoalMode(creatingGoalMode);
                }
            });
        });
        HeaderViewModel.addButton(addActivity);
    }

    private void configHeader(){
        HeaderViewModel.clear();
        HeaderViewModel.setLabel("Adicionar Meta");
        HeaderViewModel.setReturnButtonVisible(true);

        createAddActivityButton();

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
        creatingGoalMode = true;
        createGoal(); //Create a new goal
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
        goal.setName(nameField.getText());
        setDeadline();
        goal.setDescription(descriptionField.getText());
        GoalController.assignGoalToCollaborator(goal, collaborator);
        goal.setCollaborator(collaborator);

        if(nameField.getText().isBlank()){
            TemplateViewModel.showErrorMessage("Por favor, preencha o nome da meta.");
            return;
        }

        boolean saved = GoalController.saveGoal(goal);
        if(!saved){
            TemplateViewModel.showErrorMessage("Não foi possível adicionar a meta.");
            return;
        }

        goal.getActivities().forEach(ActivityController::saveActivity);

        disableCreationMode();
        creatingGoalMode = false;
        TemplateViewModel.showSuccessMessage("Meta criada com sucesso!");
        HeaderViewModel.updateHeader("Goal.fxml");
    }

    private void setDeadline(){
        goal.setDeadline(LocalDate.now().plusDays(1));
        goal.getActivities().forEach(activity -> {
            if(activity.getDeadline().isAfter(goal.getDeadline())){
                goal.setDeadline(activity.getDeadline());
            }
        });
    }

    private void disableCreationMode(){
        disableEditingState();
        HeaderViewModel.clearButtons();
        HeaderViewModel.clearButtons();
        createAddActivityButton();
    }

    public void addActivity(Activity activity){
        try {   //Load the activity template for each activity
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dottec/pdi/project/pdi/views/Activity.fxml"));
            Parent activityPane = loader.load();
            activityPane.maxWidth(activitiesField.getWidth());

            ActivityViewModel controller = loader.getController();
            controller.setActivity(activity);
            controller.updateFields();
            controller.setCreatingGoalMode(creatingGoalMode);
            controller.setGoalViewModel(goalViewModel);

            activitiesField.getChildren().add(activityPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeActivity(Activity activity){
        goal.getActivities().remove(activity);
        populateActivities();
    }

    public void populateActivities() {
        activitiesField.getChildren().clear();
        if(goal != null && goal.numberActivities() != 0) {
            goal.getActivities().forEach(this::addActivity);
        } else if(goal == null) {
            System.out.println("Goal is null");
        } else {
            System.out.println("Goal has no activities");
            Label label = new Label("Este colaborador ainda não possui metas.");
            label.getStyleClass().add("mid-label");
            activitiesField.getChildren().add(label);
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

        goal.setName(nameField.getText());
        goal.setDescription(descriptionField.getText());
        GoalController.updateGoal(goal);    //Update database

        TemplateViewModel.showSuccessMessage("Meta atualizada com sucesso!");

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
