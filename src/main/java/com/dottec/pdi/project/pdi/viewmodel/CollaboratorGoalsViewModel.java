package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.controllers.DepartmentController;
import com.dottec.pdi.project.pdi.controllers.GoalController;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.Goal;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CollaboratorGoalsViewModel {
    private Collaborator collaborator;

    @FXML private VBox collaboratorGoalsMainVBox;

    // --- FXML Campos de Informação do Colaborador ---
    @FXML private TextField nameField;
    @FXML private TextField cpfField;
    @FXML private TextField emailField;
    @FXML private ChoiceBox<Department> departmentField;

    // --- FXML Controles de Edição ---
    @FXML private ImageView editButton;
    @FXML private Button confirmEditButton;
    @FXML private Button cancelEditButton;

    // --- FXML Container para Metas ---
    @FXML private VBox goalsVBox;

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
        if(collaborator.getStatus() == CollaboratorStatus.active){
            updateCollaboratorFields();
            loadAndDisplayGoals();
            populateDepartments();
        } else {
            updateCollaboratorFields();
            collaboratorGoalsMainVBox.setDisable(true);
        }
    }

    // Atualiza os campos de texto com as informações do colaborador
    private void updateCollaboratorFields() {
        if (collaborator == null) return;

        nameField.setText(collaborator.getName());
        emailField.setText(collaborator.getEmail());
        cpfField.setText(collaborator.getCpf());

        // Exibe o nome do departamento, com verificação de nulo
        if (collaborator.getDepartment() != null && collaborator.getDepartment().getName() != null) {
            departmentField.setValue(collaborator.getDepartment());
        } else {
            departmentField.setValue(null);
        }
    }

    // Busca e exibe as metas do colaborador
    private void loadAndDisplayGoals() {
        if (collaborator == null || goalsVBox == null) return;

        goalsVBox.getChildren().clear(); // Limpa a lista antes de adicionar
        List<Goal> goals = GoalController.findGoalsByCollaborator(collaborator.getId());

        if (goals.isEmpty()) {
            Label label = new Label("Este colaborador ainda não possui metas.");
            label.setStyle("-fx-font-size: 16;");
            goalsVBox.getChildren().add(label);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Goal goal : goals) {
            StackPane goalCard = new StackPane();
            goalCard.getStyleClass().add("goal-card");

            Label goalName = new Label(goal.getName());
            goalName.getStyleClass().add("goal-name");

            Label goalDeadline = new Label("Prazo: " + goal.getDeadline().format(formatter));
            goalDeadline.getStyleClass().add("goal-deadline");

            Label goalStatus = new Label(goal.getStatus().name());
            goalStatus.getStyleClass().add("goal-status");
            goalStatus.getStyleClass().add("status-" + goal.getStatus().name().toLowerCase());

            goalCard.getChildren().addAll(goalName, goalDeadline, goalStatus);
            StackPane.setAlignment(goalName, Pos.CENTER_LEFT);
            StackPane.setAlignment(goalDeadline, Pos.TOP_RIGHT);
            StackPane.setAlignment(goalStatus, Pos.BOTTOM_RIGHT);

            goalsVBox.getChildren().add(goalCard);
        }
    }

    private void populateDepartments() {
        // Busca a lista de departamentos usando o controller
        List<Department> departments = DepartmentController.findAllDepartments();
        departmentField.getItems().addAll(departments);

        // Configura como o objeto Department deve ser exibido no ChoiceBox (mostrando seu nome)
        departmentField.setConverter(new StringConverter<Department>() {
            @Override
            public String toString(Department department) {
                // Se o objeto for nulo, retorna uma string vazia, senão, retorna o nome.
                return department == null ? "" : department.getName();
            }

            @Override
            public Department fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void handleEnableEditing() {
        nameField.setEditable(true);
        nameField.getStyleClass().remove("label-not-editable");
        nameField.getStyleClass().add("label-editable");

        departmentField.setMouseTransparent(false);
        departmentField.getStyleClass().remove("label-not-editable");
        departmentField.getStyleClass().add("label-editable");

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
        collaborator.setDepartment(departmentField.getValue());
        CollaboratorController.updateCollaborator(collaborator);

        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Colaborador atualizado com sucesso!");

        // Retorna ao estado de visualização
        disableEditingState();
    }

    private void disableEditingState() {
        nameField.setEditable(false);
        nameField.getStyleClass().add("label-not-editable");
        nameField.getStyleClass().remove("label-editable");

        departmentField.setMouseTransparent(true);
        departmentField.getStyleClass().add("label-not-editable");
        departmentField.getStyleClass().remove("label-editable");

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