package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.model.Collaborator;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.Arrays;
import java.util.List;


import javafx.application.Platform;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;


import com.dottec.pdi.project.pdi.controllers.AuthController;
import com.dottec.pdi.project.pdi.model.User;
import com.dottec.pdi.project.pdi.model.Notification;
import com.dottec.pdi.project.pdi.controllers.NotificationController;
import javafx.scene.control.ButtonBase; // Importado para uso no novo método de construção

public class HeaderViewModel {
    @FXML
    private HBox header;
    @FXML
    private HBox headerHBox;
    @FXML
    private HBox headerButtonsField;

    //Header items
    @FXML
    private Label headerLabel;
    @FXML
    private Button returnButton;
    @FXML
    private Button filterButton;
    @FXML
    private Button notificationButton;
    @FXML
    private Label notificationBadge;
    @FXML
    private TextField searchBar;
    @FXML
    private StackPane searchBarStackPane;


    public HeaderViewModel() {
    }

    public Button getFilterButton() {
        return filterButton;
    }

    static HeaderViewModel instance;

    public static void setInstance(HeaderViewModel headerViewModel) {
        instance = headerViewModel;
    }

    public static HeaderViewModel getController() {
        return instance;
    }

    public static void updateHeader(String page) {
        instance.buildHeader(page);
    }

    @FXML
    private void initialize() {
        instance = this;
        returnButton.setOnMouseClicked(e -> TemplateViewModel.goBack());
        Platform.runLater(this::updateNotificationBadge);
    }

    @FXML
    private void goToNotifications(MouseEvent event) {
        TemplateViewModel.switchScreen("Notifications.fxml");
        updateHeader("Notifications.fxml");
    }

    public void updateNotificationBadge() {
        User user = AuthController.getInstance().getLoggedUser();
        if (user == null) {
            notificationBadge.setVisible(false);
            return;
        }

        List<Notification> allNotifications = NotificationController.findAllNotifications();

        long unreadCount = allNotifications.stream()
                .filter(n -> n.getUserId() == user.getId())
                .filter(n -> !n.isNotIsRead())
                .count();

        if (unreadCount > 0) {
            String text = (unreadCount > 9) ? "9+" : String.valueOf(unreadCount);
            notificationBadge.setText(text);
            notificationBadge.setVisible(true);
        } else {
            notificationBadge.setVisible(false);
        }
    }

    public static void refreshNotificationBadge() {
        if (instance != null) {
            instance.updateNotificationBadge();
        }
    }

    private void buildHeader(String page) {
        boolean showNotificationButton = true;

        switch (page) {
            case "Dashboard.fxml" -> {
                AuthController auth = AuthController.getInstance();
                User currentUser = auth.getLoggedUser();

                // --- NOVO: Botão de Exportar Dados ---
                Button exportButton = new Button("Exportar Dados");
                ImageView exportIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dottec/pdi/project/pdi/static/img/download.png")));
                exportIcon.setFitHeight(25.0);
                exportIcon.setFitWidth(25.0);
                exportButton.setGraphic(exportIcon);
                exportButton.setContentDisplay(ContentDisplay.RIGHT);

                // Adiciona a ação ao botão
                exportButton.setOnAction(e -> {
                    DashboardViewModel controller = DashboardViewModel.getInstance();
                    if (controller != null) {
                        controller.handleExportData(e);
                    } else {
                        // Mensagem de fallback, caso o controller não tenha sido inicializado corretamente
                        TemplateViewModel.showErrorMessage("Erro de Acesso", "A tela do Dashboard não está pronta para exportação. Tente novamente.");
                    }
                });

                if (currentUser != null) {
                    if (currentUser.getRole() == Role.department_manager) {
                        buildHeaderStructure("Dashboard - Setor " + currentUser.getDepartment().getName(), false, false, true, showNotificationButton, exportButton);
                    } else {
                        buildHeaderStructure("Dashboard", false, false, true, showNotificationButton, exportButton);
                    }
                } else {
                    buildHeaderStructure("Dashboard", false, false, true, showNotificationButton, exportButton);
                }
            }
            case "Collaborators.fxml" -> {
                AuthController auth = AuthController.getInstance();
                User currentUser = auth.getLoggedUser();

                // Corrigido: Botão "Adicionar Colaborador" visível apenas para HR e General Manager
                if (currentUser != null && (currentUser.getRole() == Role.hr_manager || currentUser.getRole() == Role.general_manager)) {
                    Button buttonAddCollaborator = new Button("Adicionar Colaborador");
                    buttonAddCollaborator.setOnMouseClicked(event2 -> {
                        TemplateViewModel.switchScreen("RegisterCollaborator.fxml");
                        updateHeader("RegisterCollaborator.fxml");
                    });

                    // O botão é passado apenas se a verificação de função passar
                    buildHeaderStructure("Colaboradores", false, true, true, showNotificationButton, buttonAddCollaborator);
                } else {
                    // Se o usuário é Gerente de Área ou Colaborador, o botão não é exibido
                    buildHeaderStructure("Colaboradores", false, true, true, showNotificationButton);
                }
            }
            case "RegisterCollaborator.fxml", "CollaboratorGoals.fxml", "AddGoalFromTemplate.fxml", "Goal.fxml",
                 "AddActivity.fxml" -> {
                buildHeaderStructure(
                        switch (page) {
                            case "RegisterCollaborator.fxml" -> "Adicionar Colaborador";
                            case "CollaboratorGoals.fxml" -> "PDI do Colaborador";
                            case "AddGoalFromTemplate.fxml", "Goal.fxml" -> "Adicionar Meta";
                            case "AddActivity.fxml" -> "Adicionar Atividade";
                            default -> "Plano de Desenvolvimento Individual";
                        },
                        true, false, false, showNotificationButton
                );
            }

            case "Notifications.fxml" -> {
                buildHeaderStructure("Notificações", true, false, false, false);
            }
            case "ManagementHub.fxml" -> { // NOVO HUB
                buildHeaderStructure("Gerenciamento", true, false, false, showNotificationButton);
            }
            case "Settings.fxml" -> { // Gerenciamento de Setores (sub-página)
                User currentUser = AuthController.getInstance().getLoggedUser();

                Button buttonAddSector = null;
                // Restringe o botão de adicionar setor apenas para RH (mantido para a tela de setores)
                if (currentUser != null && currentUser.getRole() == Role.hr_manager) {
                    buttonAddSector = new Button("Adicionar Setor");
                    buttonAddSector.setOnMouseClicked(event2 -> {
                        TemplateViewModel.switchScreen("AddSector.fxml");
                        updateHeader("AddSector.fxml");
                    });
                }

                buildHeaderStructure("Setores", true, false, false, showNotificationButton, buttonAddSector);
            }
            case "AddSector.fxml" -> {
                buildHeaderStructure("Adicionar Setor", true, false, false, showNotificationButton);
            }
            case "UserManagement.fxml" -> { // NOVO: Lista de Usuários
                Button buttonAddUser = new Button("Adicionar Usuário");
                buttonAddUser.setOnMouseClicked(event2 -> {
                    TemplateViewModel.switchScreen("UserForm.fxml");
                    updateHeader("UserForm.fxml");
                });
                buildHeaderStructure("Gerenciar Usuários", true, false, false, showNotificationButton, buttonAddUser);
            }
            case "UserForm.fxml" -> { // NOVO: Formulário de Usuário
                buildHeaderStructure("Adicionar Usuário", true, false, false, showNotificationButton);
            }
            case "Profile.fxml" -> {
                buildHeaderStructure("Perfil do usuário", true, false, false, showNotificationButton);
            }
            case "AddActivityTemplate.fxml" -> {
                buildHeaderStructure("Adicionar Atividade", true, false, false, false);
            }
            case "GoalTemplates.fxml" -> {
                buildHeaderStructure("Modelos", false, false, false, false);
            }
            case "TemplateGoal.fxml" -> {
                buildHeaderStructure("Modelo", true, false, false, false);
            }
            default -> {
                buildHeaderStructure("Plano de Desenvolvimento Individual", false, false, false, showNotificationButton);
            }
        }
    }

    public void buildHeaderStructure(String label, boolean returnBtn, boolean searchBar, boolean filterButton, boolean notificationButton, Node... headerItems) {
        headerLabel.setText(label);
        headerButtonsField.getChildren().clear();

        if (headerItems == null) headerItems = new Node[0];

        setReturnButtonVisible(returnBtn);
        setFilterButtonVisible(filterButton);
        setSearchBarVisible(searchBar);
        setNotificationButtonVisible(notificationButton);

        Arrays.stream(headerItems).forEach(item -> {
            if (item instanceof Button btn) {
                btn.getStyleClass().add("basic-button");
                headerButtonsField.getChildren().add(btn);
            } else {
                headerButtonsField.getChildren().add(item);
            }
        });
    }



        public static void setNotificationButtonVisible(Boolean visible) {
        instance.notificationButton.setVisible(visible);
        instance.notificationButton.setManaged(visible);
    }

    public static void setReturnButtonVisible(Boolean visible) {
        instance.returnButton.setVisible(visible);
        instance.returnButton.setManaged(visible);
    }

    public static void setSearchBarVisible(Boolean visible) {
        instance.searchBarStackPane.setVisible(visible);
        instance.searchBarStackPane.setManaged(visible);
    }

    public static void setFilterButtonVisible(Boolean visible) {
        instance.filterButton.setVisible(visible);
        instance.filterButton.setManaged(visible);
    }


    public StringProperty getSearchText() {
        return searchBar.textProperty();
    }

    public static void setLabel(String label) {
        instance.headerLabel.setText(label);
    }

    public static void removeButton(int index) {
        instance.headerButtonsField.getChildren().remove(index);
    }

    public static void removeLastButton() {
        instance.headerButtonsField.getChildren().removeLast();
    }

    public static void removeFirstButton() {
        instance.headerButtonsField.getChildren().removeFirst();
    }

    // CORREÇÃO: Aceita Node (para incluir MenuButton e outros)
    public static void addButton(Node node) {
        // Aplica estilo básico se for um botão (ou MenuButton/ButtonBase)
        if (node instanceof ButtonBase btn) {
            if (!btn.getStyleClass().contains("basic-button") && !btn.getStyleClass().contains("cancel-button")) {
                btn.getStyleClass().add("basic-button");
            }
        }
        instance.headerButtonsField.getChildren().addLast(node);
    }

    // CORREÇÃO: Aceita Node (para incluir MenuButton e outros)
    public static void addButton(int position, Node node) {
        // Aplica estilo básico se for um botão (ou MenuButton/ButtonBase)
        if (node instanceof ButtonBase btn) {
            if (!btn.getStyleClass().contains("basic-button") && !btn.getStyleClass().contains("cancel-button")) {
                btn.getStyleClass().add("basic-button");
            }
        }
        instance.headerButtonsField.getChildren().add(position, node);
    }


    public static void clearButtons() {
        instance.headerButtonsField.getChildren().clear();
    }


    public static void clear() {
        clearButtons();
        setLabel("Plano de Desenvolvimento Individual");
        setReturnButtonVisible(false);
        setSearchBarVisible(false);
        setNotificationButtonVisible(false);
    }
}