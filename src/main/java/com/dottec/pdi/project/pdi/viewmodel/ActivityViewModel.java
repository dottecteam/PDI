package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.GoalController;
import com.dottec.pdi.project.pdi.dao.ActivityDAO;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Tag;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.time.LocalDate;


public class ActivityViewModel {
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private ImageView editButton;
    @FXML private ImageView deleteButton;
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private Label statusLabel;
    @FXML private DatePicker deadlineDatePicker;
    @FXML private HBox titledPaneHeader;
    @FXML private TitledPane activityTitledPane;
    @FXML private VBox activityBody;

    private Activity activity;
    private boolean creatingGoalMode = false;
    public void setCreatingGoalMode(boolean creatingGoalMode){this.creatingGoalMode=creatingGoalMode;}
    private GoalViewModel goalViewModel;
    public void setGoalViewModel(GoalViewModel goalViewModel){
        this.goalViewModel=goalViewModel;
    }

    private TagsMenuViewModel tagsMenuViewModel;
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @FXML void initialize(){
        deadlineDatePicker.setEditable(false);
        deadlineDatePicker.getEditor().setMouseTransparent(true);
        deadlineDatePicker.setMouseTransparent(true);
        buttonVisible(cancelButton, false);
        buttonVisible(confirmButton, false);
        loadTagsMenu();
        titledPaneHeader.setMinWidth(activityTitledPane.getWidth());
    }

    private void loadTagsMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dottec/pdi/project/pdi/views/TagsMenu.fxml"));
            Parent root = loader.load();
            tagsMenuViewModel = loader.getController();
            activityBody.getChildren().add(1, root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFields(){
        nameField.setText(activity.getName());
        descriptionField.setText(activity.getDescription());
        deadlineDatePicker.setValue(activity.getDeadline());

        tagsMenuViewModel.disableEditing();
        tagsMenuViewModel.setSelectedTags(activity.getTags());
        tagsMenuViewModel.refresh();

        switch (activity.getStatus()) {
            case completed -> {
                statusLabel.setText("Completo");
                statusLabel.setStyle("-fx-background-color: #6D00A1; -fx-text-fill: white");
            }
            case in_progress -> {
                statusLabel.setText("Em progresso");
                statusLabel.setStyle("-fx-background-color: #AF69CD; -fx-text-fill: white;");
            }
            case canceled -> {
                statusLabel.setText("Cancelado");
                statusLabel.setStyle("-fx-background-color: #E6CCEF; -fx-text-fill: #5c5c5c");
            }
            case pending -> {
                statusLabel.setText("Pendente");
                statusLabel.setStyle("-fx-background-color: #AF69CD; -fx-text-fill: #5c5c5c;");
            }
            default -> {
                statusLabel.setText("Desconhecido");
                statusLabel.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
            }
        }
    }

    private void buttonVisible(Node button, Boolean visible){
        button.setVisible(visible);
        button.setManaged(visible);
    }

    @FXML
    private void handleEnableEditing(){
        buttonVisible(deleteButton, false);
        buttonVisible(editButton, false);
        buttonVisible(confirmButton, true);
        buttonVisible(cancelButton, true);

        confirmButton.setOnMouseClicked(mouseEvent -> {handleConfirmEdit();});
        cancelButton.setOnMouseClicked(mouseEvent -> {handleCancelEditing();});

        nameField.getStyleClass().remove("label-not-editable");
        nameField.getStyleClass().add("label-editable");
        nameField.setMouseTransparent(false);
        nameField.setEditable(true);

        descriptionField.getStyleClass().remove("label-not-editable");
        descriptionField.getStyleClass().add("label-editable");
        descriptionField.setEditable(true);

        tagsMenuViewModel.enableEditing();

        deadlineDatePicker.getStyleClass().remove("label-not-editable");
        deadlineDatePicker.getStyleClass().add("label-editable");
        deadlineDatePicker.setMouseTransparent(false);
    }

    @FXML
    private void handleCancelEditing() {
        updateFields();
        disableEditingState();
        tagsMenuViewModel.cancelEdit();
    }

    @FXML
    private void handleConfirmEdit() {
        if (nameField.getText().trim().isEmpty()) {
            TemplateViewModel.showErrorMessage("O nome não pode estar vazio.");
            return;
        } else if(deadlineDatePicker.getValue().isBefore(LocalDate.now())) {
            TemplateViewModel.showErrorMessage("O prazo deve ser uma data futura.");
            return;
        }

        tagsMenuViewModel.confirmEdit();

        activity.setName(nameField.getText());
        activity.setDescription(descriptionField.getText());
        activity.setDeadline(deadlineDatePicker.getValue());

        if(!creatingGoalMode){
            ActivityDAO.update(activity);
        }

        TemplateViewModel.showSuccessMessage("Meta atualizada com sucesso!");

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

        deadlineDatePicker.getStyleClass().remove("label-editable");
        deadlineDatePicker.getStyleClass().add("label-not-editable");
        deadlineDatePicker.setMouseTransparent(true);

        buttonVisible(editButton, true);
        buttonVisible(deleteButton, true);
        buttonVisible(confirmButton, false);
        buttonVisible(cancelButton, false);
    }

    @FXML
    private void handleEnableDelete(){
        buttonVisible(deleteButton, false);
        buttonVisible(editButton, false);
        buttonVisible(confirmButton, true);
        buttonVisible(cancelButton, true);

        confirmButton.setOnMouseClicked(mouseEvent -> {handleConfirmDelete();});
        cancelButton.setOnMouseClicked(mouseEvent -> {handleCancelEditing();});
    }

    @FXML
    private void handleConfirmDelete(){
        if(creatingGoalMode){
            goalViewModel.removeActivity(activity);
        } else {
            goalViewModel.removeActivity(activity);
            ActivityDAO.delete(activity);
        }
        TemplateViewModel.showSuccessMessage("Atividade excluída com sucesso!");
    }
}
