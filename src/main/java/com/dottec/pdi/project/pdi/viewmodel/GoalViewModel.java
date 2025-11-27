package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.ActivityController;
import com.dottec.pdi.project.pdi.controllers.GoalController;
import com.dottec.pdi.project.pdi.enums.ActivityStatus;
import com.dottec.pdi.project.pdi.dao.ActivityDAO;
import com.dottec.pdi.project.pdi.enums.GoalStatus;
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

    private GoalTemplate goalTemplate = null;
    public void setGoalTemplate(GoalTemplate goalTemplate){
        this.goalTemplate = goalTemplate;
    }

    private boolean creatingGoalMode = false;

    @FXML
    private void initialize(){
    }

    public void refresh(){
        if(goal == null){   //Create a new goal if the create goal button was pressed
            enableCreationMode();
            updateFields();
        } else {    //Update the fields for the selected goal
            updateFields();
            createAddActivityButton();
            goal.setActivities(ActivityController.findActivitiesByGoalId(goal.getId()));
            populateActivities();
        }
    }

    private void updateFields(){
        if(goal != null) {
            nameField.setText(goal.getName());
            descriptionField.setText(goal.getDescription());

            if(goalTemplate != null){
                nameField.setText(goalTemplate.getGoa_tmp_name());
                descriptionField.setText(goalTemplate.getGoa_tmp_description());

                for(ActivityTemplate activityTemplate : goalTemplate.getActivityTemplates()){
                    Activity activity = new Activity();
                    activity.setName(activityTemplate.getName());
                    activity.setDescription(activityTemplate.getDescription());
                    activity.setGoal(goal);
                    activity.setStatus(ActivityStatus.in_progress);

                    addActivity(activity);
                }
            }
        }
    }

    private void createGoal(){
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
            Activity activity = new Activity();
            activity.setGoal(this.goal);
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
        updateFields();
        creatingGoalMode = true;
        createGoal();
        Platform.runLater(this::configHeader);

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
        if (!goal.getActivities().contains(activity)) {
            goal.addActivity(activity);
        }
        populateActivities();
    }

    public void removeActivity(Activity activity){
        goal.getActivities().remove(activity);
        populateActivities();
    }

    public void populateActivities() {
        activitiesField.getChildren().clear();
        if(goal != null && goal.numberActivities() != 0) {
            goal.getActivities().forEach(activity -> {
                if (creatingGoalMode) {

                    loadActivityPane(activity);
                } else {

                    Activity fullActivity = ActivityDAO.findById(activity.getId());
                    if (fullActivity != null) {
                        loadActivityPane(fullActivity);
                    } else {
                        System.err.println("Aviso: Atividade com ID " + activity.getId() + " não encontrada no banco.");
                    }
                }
            });
        } else if(goal == null) {
            System.out.println("Goal is null");
        } else {
            System.out.println("Goal has no activities");
            Label label = new Label("Esta meta ainda não possui atividades.");
            label.getStyleClass().add("mid-label");
            activitiesField.getChildren().add(label);
        }
    }

    private void loadActivityPane(Activity activity){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dottec/pdi/project/pdi/views/Activity.fxml"));
            Parent activityPane = loader.load();
            activityPane.maxWidth(activitiesField.getWidth());

            ActivityViewModel controller = loader.getController();
            controller.setActivity(activity);
            controller.updateFields();
            controller.setCreatingGoalMode(creatingGoalMode);
            controller.setGoalViewModel(this.goalViewModel);

            activitiesField.getChildren().add(activityPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleEnableEditing() {
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
        GoalController.updateGoal(goal);

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