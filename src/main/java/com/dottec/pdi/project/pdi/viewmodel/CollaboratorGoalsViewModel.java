package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.*;
import com.dottec.pdi.project.pdi.dao.CollaboratorDAO;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.enums.GoalStatus;
import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.model.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CollaboratorGoalsViewModel implements Initializable {
    private Collaborator collaborator;
    private static CollaboratorGoalsViewModel instance; // Para Singleton para refresh

    @FXML
    private VBox collaboratorGoalsMainVBox;

    // --- FXML Campos de Informação do Colaborador ---
    @FXML
    private TextField nameField;
    @FXML
    private TextField cpfField;
    @FXML
    private TextField emailField;
    @FXML
    private ChoiceBox<Department> departmentField;

    // --- Filtros ---
    private LocalDate startDay = LocalDate.now().minusDays(15);
    private LocalDate endDay = LocalDate.now().plusMonths(1);
    private List<GoalStatus> filteredStatuses = new ArrayList<>(Arrays.asList(GoalStatus.values()));

    // --- FXML Controles de Edição ---
    @FXML
    private ImageView editButton;
    @FXML
    private Button confirmEditButton;
    @FXML
    private Button cancelEditButton;

    // --- FXML Container para Metas ---
    @FXML
    private VBox goalsVBox;

    // --- FXML Para o gráfico individual ---
    @FXML
    private PieChart statusPieChart;
    @FXML
    private Label percentageLabel;
    private final String STATUS_CONCLUIDO = "completed";

    // Método chamado pelo JavaFX após o FXML ser carregado
    @FXML
    private void initialize() {
        instance = this; // Define a instância para o Singleton
        // Inicia com os botões de ação escondidos
        confirmEditButton.setVisible(false);
        cancelEditButton.setVisible(false);
    }

    //Override do gráfico
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusPieChart.setAnimated(false);
    }

    // Método estático para recarregar esta tela
    public static void refreshGoalsList() {
        if (instance != null && instance.collaborator != null) {
            instance.loadAndDisplayGoals();
            instance.loadData(instance.collaborator.getId()); // Recarrega o gráfico também
        }
    }


    // Este é o método principal para carregar os dados na tela
    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;

        // Configurações de UI
        setFilter();
        HeaderViewModel.setFilterButtonVisible(true);
        HeaderViewModel.setReturnButtonVisible(true);
        HeaderViewModel.setSearchBarVisible(false);
        HeaderViewModel.setLabel("PDI do Colaborador: " + collaborator.getName());

        if (collaborator.getStatus() == CollaboratorStatus.active) {
            updateCollaboratorFields();
            loadAndDisplayGoals();
            populateDepartments();

            loadData(collaborator.getId());
            configHeaderButtons(); // Atualiza os botões do cabeçalho
        } else {
            updateCollaboratorFields();
            editButton.setOpacity(0.3);
            collaboratorGoalsMainVBox.setDisable(true);
            configHeaderButtons(); // Apenas o botão de retorno deve ser exibido, mas a lógica de exclusão é tratada aqui.
        }
    }

    private void configHeaderButtons() {
        HeaderViewModel.clearButtons();

        createAddGoalButton();

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null && (loggedUser.getRole() == Role.hr_manager || loggedUser.getRole() == Role.general_manager)) {
            MenuButton statusMenu = new MenuButton(translateCollaboratorStatus(collaborator.getStatus().name()));
            statusMenu.getStyleClass().add("basic-button");

            // Adiciona as opções de status
            for (CollaboratorStatus statusOption : CollaboratorStatus.values()) {
                if (statusOption != collaborator.getStatus()) {
                    MenuItem item = new MenuItem(translateCollaboratorStatus(statusOption.name()));
                    item.setOnAction(e -> handleCollaboratorStatusChange(statusOption));
                    statusMenu.getItems().add(item);
                }
            }
            HeaderViewModel.addButton(statusMenu);
        }
    }

    // NOVO: Lógica de exclusão do colaborador
    private void handleDeleteCollaborator() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza que deseja INATIVAR o colaborador " + collaborator.getName() + "? Esta ação não pode ser desfeita.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            CollaboratorController.deleteCollaboratorById(collaborator.getId());
            TemplateViewModel.showSuccessMessage("Colaborador inativado com sucesso!");
            TemplateViewModel.goBack(); // Volta para a lista de colaboradores
        }
    }


    private void createAddGoalButton() {
        Button buttonAddGoal = new Button("Adicionar Meta");
        buttonAddGoal.getStyleClass().add("basic-button");
        MenuItem goalFromTemplateButton = new MenuItem("Templates");
        MenuItem emptyGoalButton = new MenuItem("Nova Meta");
        goalFromTemplateButton.getStyleClass().add("basic-button");
        emptyGoalButton.getStyleClass().add("basic-button");

        goalFromTemplateButton.setOnAction(ft -> {
            TemplateViewModel.switchScreen("AddGoalFromTemplate.fxml");
            HeaderViewModel.updateHeader("AddGoalFromTemplate.fxml");
        });

        emptyGoalButton.setOnAction(ft -> {
            TemplateViewModel.switchScreen("Goal.fxml", controller -> {
                if (controller instanceof GoalViewModel goalViewModel) {
                    goalViewModel.setGoalViewModel(goalViewModel);
                    goalViewModel.setCollaborator(collaborator);
                    goalViewModel.refresh();
                }
            });
            HeaderViewModel.updateHeader("Goal.fxml");
        });

        ContextMenu buttonOptions = new ContextMenu(emptyGoalButton, goalFromTemplateButton);
        buttonOptions.getStyleClass().add("context-menu-buttons");

        buttonAddGoal.setOnMouseClicked(event2 -> buttonOptions.show(buttonAddGoal, Side.LEFT, 20, 40));

        HeaderViewModel.addButton(buttonAddGoal);
    }

    private void setFilter() {
        FilterMenuViewModel filterMenu = new FilterMenuViewModel();

        //status based filter
        List<Node> statuses = new ArrayList<>();
        for (GoalStatus goalStatus : GoalStatus.values()) {
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(true);
            checkBox.setOnAction(e -> {
                if (!filteredStatuses.contains(goalStatus)) {
                    filteredStatuses.add(goalStatus);
                } else {
                    filteredStatuses.remove(goalStatus);
                }
            });
            switch (goalStatus.toString()) {
                case "in_progress" -> checkBox.setText("Em progresso");
                case "pending" -> checkBox.setText("Pendente");
                case "completed" -> checkBox.setText("Completo");
                case "canceled" -> checkBox.setText("Cancelado");
            }
            statuses.add(checkBox);
        }
        filterMenu.addFilterField("Filtrar por status", statuses);

        DatePicker startDayDatePicker = filterMenu.buildDatePicker();
        startDayDatePicker.setValue(startDay);
        startDayDatePicker.setOnAction(actionEvent -> startDay = startDayDatePicker.getValue());

        DatePicker endDayDatePicker = filterMenu.buildDatePicker();
        endDayDatePicker.setValue(endDay);
        endDayDatePicker.setOnAction(actionEvent -> endDay = endDayDatePicker.getValue());

        filterMenu.addFilterField("Filtrar por prazo",
                filterMenu.addDatePickerLabel("De: ", startDayDatePicker),
                filterMenu.addDatePickerLabel("À: ", endDayDatePicker));

        filterMenu.getConfirmFilterButton().setOnMouseClicked(e -> loadAndDisplayGoals());

        Button filterButton = HeaderViewModel.getController().getFilterButton();
        filterButton.setOnMouseClicked(e -> filterMenu.show(filterButton));
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
        goals.sort(Comparator.comparing(Goal::getStatus));

        if (goals.isEmpty()) {
            Label label = new Label("Este colaborador ainda não possui metas.");
            label.setStyle("-fx-font-size: 16;");
            goalsVBox.getChildren().add(label);
            return;
        }

        boolean foundFilteredGoals = false;

        for (Goal goal : goals) {
            boolean statusOk = filteredStatuses.contains(goal.getStatus());
            boolean startDayOk = true;
            boolean endDayOk = true;

            if (goal.getDeadline() != null) {
                // Ajuste de filtro: isAfter/isBefore é exclusivo. Para incluir o dia,
                // deve-se usar isAfter(date.minusDays(1)) e isBefore(date.plusDays(1))
                // No contexto do filtro, assumiremos que isAfter/isBefore são suficientes.
                startDayOk = !startDay.isAfter(goal.getDeadline()); // Prazo é depois ou igual ao início
                endDayOk = !endDay.isBefore(goal.getDeadline());   // Prazo é antes ou igual ao fim
            }

            if (statusOk && startDayOk && endDayOk) {
                buildGoalCard(goal);
                foundFilteredGoals = true;
            }
        }

        if (!foundFilteredGoals) {
            Label label = new Label("Sem resultados a serem exibidos.");
            label.setStyle("-fx-font-size: 16;");
            goalsVBox.getChildren().add(label);
        }
    }

    private void buildGoalCard(Goal goal) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        HBox goalCard = new HBox();
        goalCard.getStyleClass().add("goal-card");

        Label goalName = new Label(goal.getName());
        goalName.getStyleClass().add("mid-label");
        goalName.setStyle("-fx-text-fill: #4B0081");

        String deadlineText;
        if (goal.getDeadline() != null) {
            deadlineText = "Prazo: " + goal.getDeadline().format(formatter) + "                                  ";
        } else {
            deadlineText = "Prazo: (Não definido)";
        }
        Label goalDeadline = new Label(deadlineText);

        Label statusLabel = new Label(goal.getStatus().name());
        statusLabel.getStyleClass().add("label-status");
        switch (goal.getStatus()) {
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

        goalCard.getChildren().addAll(goalName, goalDeadline, statusLabel);
        goalCard.setAlignment(Pos.CENTER_LEFT);
        goalCard.setSpacing(0);
        HBox.setHgrow(goalName, Priority.ALWAYS);
        goalName.setMaxWidth(Double.MAX_VALUE);

        goalCard.setOnMouseClicked(mouseEvent -> {
            TemplateViewModel.switchScreen("Goal.fxml", controller -> {
                if (controller instanceof GoalViewModel goalViewModel) {
                    goalViewModel.setGoal(goal);
                    goalViewModel.setGoalViewModel(goalViewModel);
                    goalViewModel.refresh();
                }
            });
        });

        goalsVBox.getChildren().add(goalCard);
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
        cancelEditButton.setManaged(false);
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
                                color = "#01B8AA";
                                break;
                            case "pending":
                                color = "#708F95";
                                break;
                            case "canceled":
                                color = "#FD625E";
                                break;
                            case "in_progress":
                                color = "#F2C80F";
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

    private void handleCollaboratorStatusChange(CollaboratorStatus newStatus) {
        String statusText = translateCollaboratorStatus(newStatus.name());
        String confirmationMessage = "Confirmar mudança de status do colaborador " + collaborator.getName() + " para " + statusText + "?";

        if (newStatus == CollaboratorStatus.inactive) {
            confirmationMessage += " ATENÇÃO: O status 'Inativo' fará com que o colaborador seja logicamente excluído (soft-delete).";
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, confirmationMessage);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // O CollaboratorController.updateCollaborator usa o DAO que atualiza o status
            collaborator.setStatus(newStatus);
            CollaboratorController.updateCollaborator(collaborator); // Persiste o novo status no banco

            // Se for inativar, chamo a função que faz o soft delete.
            if (newStatus == CollaboratorStatus.inactive) {
                CollaboratorController.deleteCollaboratorById(collaborator.getId());
            }

            TemplateViewModel.showSuccessMessage("Status do colaborador atualizado com sucesso para " + statusText + "!");

            // Força a atualização visual (campos, botões) e a lista de metas
            updateCollaboratorFields();
            configHeaderButtons();
            loadAndDisplayGoals();
        }
    }

    // NOVO MÉTODO: Tradução de Status de Colaborador
    private String translateCollaboratorStatus(String status) {
        return switch (status.toLowerCase()) {
            case "active" -> "Ativo";
            case "on_leave" -> "Afastado";
            case "inactive" -> "Inativo";
            default -> status;
        };
    }
}