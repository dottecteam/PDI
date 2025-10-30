package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.ActivityController;
import com.dottec.pdi.project.pdi.enums.ActivityStatus;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Tag;
import com.dottec.pdi.project.pdi.utils.FXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class AddActivityViewModel {
    @FXML private TextField formAddActivityName;
    @FXML private TextField formAddActivityDescription;
    @FXML private ChoiceBox<Tag> formAddActivityTags;
    @FXML private DatePicker formAddDeadline;
    @FXML private Button confirmButton;

    @FXML private VBox formPane;

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
        activity.setTags(formAddActivityTags.getItems());
        activity.setDeadline(formAddDeadline.getValue());
        activity.setStatus(ActivityStatus.in_progress);
    }

    @FXML
    void handleConfirm(ActionEvent actionEvent){
        if(formAddDeadline.getValue()==null || formAddActivityName.getText().isBlank()){
            TemplateViewModel.showErrorMessage("Por favor, preencha os campos obrigatórios.");
        } else if(formAddDeadline.getValue().isBefore(LocalDate.now())) {
            TemplateViewModel.showErrorMessage("O prazo deve ser uma data futura.");
        } else if (creatingGoalMode){
            updateActivity();
            activity.getGoal().addActivity(activity);  //Add the activity to the goal's list
            goalViewModel.addActivity(activity);
            TemplateViewModel.showSuccessMessage("Atividade adicionada com sucesso!");
            TemplateViewModel.goBack();
        } else {
            updateActivity();
            if(goalViewModel!=null) goalViewModel.addActivity(activity);
            ActivityController.saveActivity(activity);
            TemplateViewModel.showSuccessMessage("Atividade adicionada com sucesso!");
            TemplateViewModel.goBack();
        }
    }

    @FXML
    void handleCancel(ActionEvent actionEvent){
        TemplateViewModel.goBack();
    }
}
