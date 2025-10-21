package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.GoalController;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Goal;
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

import java.util.List;

public class AddGoalViewModel {

    @FXML private ListView<Goal> goalsListView;

    private Collaborator currentCollaborator;
    private List<Goal> collaboratorGoals;

    @FXML public void initialize() {
        goalsListView.setCellFactory(param -> new GoalCell());
    }

    public void setCollaborator(Collaborator collaborator) {
        this.currentCollaborator = collaborator;
        loadData(); // Carrega os dados assim que o colaborador for definido
    }

    private void loadData() {
        if (currentCollaborator == null) return;

        List<Goal> allGoals = GoalController.findAllGoals();

        collaboratorGoals = GoalController.findGoalsByCollaborator(currentCollaborator.getId());

        ObservableList<Goal> observableGoals = FXCollections.observableArrayList(allGoals);
        goalsListView.setItems(observableGoals);
    }

    private class GoalCell extends ListCell<Goal> {
        private final HBox hbox = new HBox(10);
        private final Label nameLabel = new Label();
        private final Region spacer = new Region();
        private final Button addButton = new Button("Adicionar");
        private final Label statusLabel = new Label();

        public GoalCell() {
            super();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            hbox.setAlignment(Pos.CENTER_LEFT);
            addButton.getStyleClass().add("add-button");

            addButton.setOnAction(event -> {
                Goal goal = getItem();
                if (goal != null && currentCollaborator != null) {
                    GoalController.assignGoalToCollaborator(goal, currentCollaborator);
                    addButton.setDisable(true);
                    addButton.setText("Adicionado");
                }
            });
        }

        @Override
        protected void updateItem(Goal goal, boolean empty) {
            super.updateItem(goal, empty);
            if (empty || goal == null) {
                setGraphic(null);
            } else {
                nameLabel.setText(goal.getName());
                nameLabel.setStyle("-fx-font-size: 16px;");

                hbox.getChildren().clear();
                hbox.getChildren().addAll(nameLabel, spacer);

                Goal existingGoal = findGoalInCollaboratorList(goal.getId());

                if (existingGoal != null) {
                    switch (existingGoal.getStatus()) {
                        case in_progress:
                            statusLabel.setText("📖 Em progresso");
                            statusLabel.getStyleClass().setAll("status-label", "in-progress");
                            break;
                        case completed:
                            statusLabel.setText("✔ Concluído");
                            statusLabel.getStyleClass().setAll("status-label", "completed");
                            break;
                        case pending:
                            statusLabel.setText("⏳ Pendente");
                            statusLabel.getStyleClass().setAll("status-label", "pending");
                            break;
                        case canceled:
                            statusLabel.setText("❌ Cancelado");
                            statusLabel.getStyleClass().setAll("status-label", "canceled");
                            break;
                    }
                    hbox.getChildren().add(statusLabel);
                } else {
                    addButton.setDisable(false);
                    addButton.setText("Adicionar");
                    hbox.getChildren().add(addButton);
                }

                setGraphic(hbox);
            }
        }

        private Goal findGoalInCollaboratorList(int goalId) {
            if (collaboratorGoals == null) return null;
            for (Goal g : collaboratorGoals) {
                if (g.getId() == goalId) {
                    return g;
                }
            }
            return null;
        }
    }
}