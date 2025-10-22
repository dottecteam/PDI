package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.controllers.CollaboratorStatusData;
import com.dottec.pdi.project.pdi.dao.CollaboratorDAO;
import com.dottec.pdi.project.pdi.controllers.DepartmentController;
import com.dottec.pdi.project.pdi.controllers.GoalController;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.Goal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CollaboratorGoalsViewModel implements Initializable {
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

    // --- FXML Para o gráfico individual ---
    @FXML private PieChart statusPieChart;
    @FXML private Label percentageLabel;
    private final String STATUS_CONCLUIDO = "completed";

    // Método chamado pelo JavaFX após o FXML ser carregado
    @FXML
    private void initialize() {
        // Inicia com os botões de ação escondidos
        confirmEditButton.setVisible(false);
        cancelEditButton.setVisible(false);
    }
    //Override do gráfico
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusPieChart.setAnimated(false);
    }

    // Este é o método principal para carregar os dados na tela
    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
        if(collaborator.getStatus() == CollaboratorStatus.active){
            updateCollaboratorFields();
            loadAndDisplayGoals();
            populateDepartments();

            loadData(collaborator.getId());
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

            String deadlineText;
            if (goal.getDeadline() != null) {
                deadlineText = "Prazo: " + goal.getDeadline().format(formatter);
            } else {
                deadlineText = "Prazo: (Não definido)";
            }
            Label goalDeadline = new Label(deadlineText);

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
            TemplateViewModel.showErrorMessage("O nome não pode estar vazio.");
            return;
        }

        // Atualiza o objeto e chama o controller estático
        collaborator.setName(nameField.getText());
        collaborator.setDepartment(departmentField.getValue());
        CollaboratorController.updateCollaborator(collaborator);

        TemplateViewModel.showSuccessMessage("Colaborador atualizado com sucesso!");

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

    //Carrega dados para o gráfico
    public void loadData(int collaboratorId) {
        percentageLabel.setText("Carregando dados...");

        Task<List<CollaboratorStatusData>> loadDataTask = new Task<>() {
            @Override
            protected List<CollaboratorStatusData> call() throws Exception {
                //A Task agora chama o NOVO método do DAO com o ID recebido
                return CollaboratorDAO.getTaskStatusCountsForCollaborator(collaboratorId);
            }
        };

        loadDataTask.setOnSucceeded(event -> {
            List<CollaboratorStatusData> dataFromDB = loadDataTask.getValue();

            ObservableList<PieChart.Data> pieChartData =
                    FXCollections.observableArrayList();

            int totalTasks = 0;
            int completedTasks = 0;

            for (CollaboratorStatusData data : dataFromDB) {

                //Traduz o status do banco
                String statusDoBanco = data.status();
                String traducao;

                switch (statusDoBanco.toLowerCase()) {
                    case "completed":
                        traducao = "Completo";
                        break;
                    case "pending":
                        traducao = "Pendente";
                        break;
                    case "canceled":
                        traducao = "Cancelado";
                        break;
                    case "in_progress":
                        traducao = "Em progresso";
                        break;
                    default:
                        traducao = statusDoBanco; //Se não for traduzido, mostra o original
                }

                PieChart.Data pieData = new PieChart.Data(traducao, data.cont());
                final String status = data.status();

                pieData.nodeProperty().addListener((ov, oldNode, newNode) -> {
                    if (newNode != null) {
                        //Define a cor com base no status
                        String color = "#9032BB"; // Cor padrão



                        switch (status.toLowerCase()) {
                            case "completed":
                                color = "#598649";
                                break;
                            case "pending":
                                color = "#FFCF5E";
                                break;
                            case "canceled":
                                color = "#E54B2E";
                                break;
                            case "in_progress":
                                color = "#F5883F";
                                break;
                        }
                        newNode.setStyle("-fx-background-color: " + color + ";");
                    }
                });

                pieChartData.add(pieData);
                totalTasks += data.cont();
                if (data.status().equalsIgnoreCase(STATUS_CONCLUIDO)) {
                    completedTasks = data.cont();
                }

            }

            statusPieChart.setData(pieChartData);

            if (totalTasks > 0) {
                double percentage = ((double) completedTasks / totalTasks) * 100.0;
                percentageLabel.setText(String.format("Progresso: %.1f%%", percentage));
            } else {
                percentageLabel.setText("Nenhuma tarefa encontrada para este colaborador.");
            }
        });

        loadDataTask.setOnFailed(event -> {
            percentageLabel.setText("Erro ao carregar dados.");
            loadDataTask.getException().printStackTrace();
        });

        new Thread(loadDataTask).start();
    }


    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}