package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.controllers.GoalController;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Goal;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CollaboratorGoalsViewModel {
    private Collaborator collaborator;

    // --- FXML Campos de Informação do Colaborador ---
    @FXML private TextField nameField;
    @FXML private TextField cpfField;
    @FXML private TextField emailField;
    @FXML private TextField departmentField;

    // --- FXML Controles de Edição ---
    @FXML private ImageView editButton;
    @FXML private Button confirmEditButton;
    @FXML private Button cancelEditButton; // Botão de cancelar é uma boa prática

    // --- FXML Container para Metas ---
    @FXML private VBox goalsVBox; // Adicione este VBox no seu FXML para listar as metas

    // Método chamado pelo JavaFX após o FXML ser carregado
    @FXML
    private void initialize() {
        // Inicia com os botões de ação escondidos
        confirmEditButton.setVisible(false);
        cancelEditButton.setVisible(false);
    }

    // Este é o método principal para carregar os dados na tela
    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
        updateCollaboratorFields();
        loadAndDisplayGoals();
    }

    // Atualiza os campos de texto com as informações do colaborador
    private void updateCollaboratorFields() {
        if (collaborator == null) return;

        nameField.setText(collaborator.getName());
        emailField.setText(collaborator.getEmail());
        cpfField.setText(collaborator.getCpf());

        // Exibe o nome do departamento, com verificação de nulo
        if (collaborator.getDepartment() != null && collaborator.getDepartment().getName() != null) {
            departmentField.setText(collaborator.getDepartment().getName());
        } else {
            departmentField.setText("Não definido");
        }
    }

    // Busca e exibe as metas do colaborador
    private void loadAndDisplayGoals() {
        if (collaborator == null || goalsVBox == null) return;

        goalsVBox.getChildren().clear(); // Limpa a lista antes de adicionar
        List<Goal> goals = GoalController.findGoalsByCollaborator(collaborator.getId());

        if (goals.isEmpty()) {
            goalsVBox.getChildren().add(new Label("Este colaborador ainda não possui metas."));
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Goal goal : goals) {
            StackPane goalCard = new StackPane();
            goalCard.getStyleClass().add("goal-card"); // Estilize no seu CSS

            Label goalName = new Label(goal.getName());
            goalName.getStyleClass().add("goal-name");

            Label goalDeadline = new Label("Prazo: " + goal.getDeadline().format(formatter));
            goalDeadline.getStyleClass().add("goal-deadline");

            Label goalStatus = new Label(goal.getStatus().name()); // Simplesmente o nome do ENUM
            goalStatus.getStyleClass().add("goal-status");
            goalStatus.getStyleClass().add("status-" + goal.getStatus().name().toLowerCase()); // Ex: status-in_progress

            goalCard.getChildren().addAll(goalName, goalDeadline, goalStatus);
            StackPane.setAlignment(goalName, Pos.CENTER_LEFT);
            StackPane.setAlignment(goalDeadline, Pos.TOP_RIGHT);
            StackPane.setAlignment(goalStatus, Pos.BOTTOM_RIGHT);

            goalsVBox.getChildren().add(goalCard);
        }
    }

    @FXML
    private void handleEnableEditing() {
        // Apenas o nome deve ser editável
        nameField.setEditable(true);
        nameField.getStyleClass().remove("label-not-editable");
        nameField.getStyleClass().add("label-editable");

        // Troca a visibilidade dos botões
        editButton.setVisible(false);
        confirmEditButton.setVisible(true);
        cancelEditButton.setVisible(true);
    }

    @FXML
    private void handleCancelEditing() {
        // Restaura o valor original do campo
        updateCollaboratorFields();
        disableEditingState();
    }

    @FXML
    private void handleConfirmEdit() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro", "O nome não pode estar vazio.");
            return;
        }

        // Atualiza o objeto e chama o controller estático
        collaborator.setName(nameField.getText());
        CollaboratorController.updateCollaborator(collaborator);

        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Colaborador atualizado com sucesso!");

        // Retorna ao estado de visualização
        disableEditingState();
    }

    private void disableEditingState() {
        nameField.setEditable(false);
        nameField.getStyleClass().add("label-not-editable");
        nameField.getStyleClass().remove("label-editable");

        // Troca a visibilidade dos botões de volta
        editButton.setVisible(true);
        confirmEditButton.setVisible(false);
        cancelEditButton.setVisible(false);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}