package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.ActivityController;
import com.dottec.pdi.project.pdi.controllers.GoalController;
import com.dottec.pdi.project.pdi.enums.ActivityStatus;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.model.Tag;
import com.dottec.pdi.project.pdi.utils.FXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;

public class AddActivityViewModel extends VBox {
    @FXML private TextField formAddActivityName;
    @FXML private TextField formAddActivityDescription;
    @FXML private DatePicker formAddDeadline;
    @FXML private Button confirmButton;

    @FXML private VBox formPane;

    private TagsMenuViewModel tagsMenuViewModel;

    private Activity activity;
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private GoalViewModel goalViewModel;
    public void setGoalViewModel(GoalViewModel goalViewModel) {
        this.goalViewModel = goalViewModel;
    }

    private boolean creatingGoalMode = false;
    public void setCreatingGoalMode(boolean creatingGoalMode){this.creatingGoalMode=creatingGoalMode;}

    @FXML private void initialize(){
        loadTagsMenu();
        formAddActivityName.clear();
        formAddDeadline.setEditable(false);
        formAddDeadline.getEditor().setMouseTransparent(true);
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

    private void updateActivity(){
        activity.setName(formAddActivityName.getText());
        activity.setDescription(formAddActivityDescription.getText());
        activity.setDeadline(formAddDeadline.getValue());
        activity.setTags(tagsMenuViewModel.getSelectedTags());
        activity.setStatus(ActivityStatus.in_progress);
    }

    @FXML
    void handleConfirm(ActionEvent actionEvent){
        if(formAddDeadline.getValue()==null || formAddActivityName.getText().isBlank()){
            FXUtils.showErrorMessage("Por favor, preencha os campos obrigatórios.");
        } else if(formAddDeadline.getValue().isBefore(LocalDate.now())) {
            FXUtils.showErrorMessage("O prazo deve ser uma data futura.");
        } else if (creatingGoalMode){
            updateActivity();
            activity.getGoal().addActivity(activity);  //Add the activity to the goal's list
            goalViewModel.addActivity(activity);
            FXUtils.showSuccessMessage("Atividade adicionada com sucesso!");
            TemplateViewModel.goBack();
        } else {
            updateActivity();
            if(goalViewModel!=null) goalViewModel.addActivity(activity);
            ActivityController.saveActivity(activity);
            FXUtils.showSuccessMessage("Atividade adicionada com sucesso!");
            TemplateViewModel.goBack();
            Goal goal = goalViewModel.getGoal();
            if(goal.getDeadline()== null) {
                goal.setDeadline(activity.getDeadline());
                GoalController.updateGoal(goal);
            } else if(activity.getDeadline().isAfter(goal.getDeadline())){
                goal.setDeadline(activity.getDeadline());
                GoalController.updateGoal(goal);
            }
        }
    }

    @FXML
    void handleCancel(ActionEvent actionEvent){
        TemplateViewModel.goBack();
    }

    private void loadTagsMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dottec/pdi/project/pdi/views/TagsMenu.fxml"));
            Parent root = loader.load();
            tagsMenuViewModel = loader.getController();
            formPane.getChildren().add(3, root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
