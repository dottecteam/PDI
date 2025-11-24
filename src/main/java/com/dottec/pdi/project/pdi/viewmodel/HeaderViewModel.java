package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.enums.Role;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.Arrays;

import com.dottec.pdi.project.pdi.controllers.AuthController;
import com.dottec.pdi.project.pdi.model.User;

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
    private TextField searchBar;
    @FXML
    private StackPane searchBarStackPane;

    public HeaderViewModel() {
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
    }

    @FXML
    private void goToNotifications(MouseEvent event) {
        TemplateViewModel.switchScreen("Notifications.fxml");
        updateHeader("Notifications.fxml");
    }

    private void buildHeader(String page) {
        // Por padrão, o botão de notificação deve ser visível (true)
        boolean showNotificationButton = true;

        switch (page) {
            case "Dashboard.fxml" -> {
                AuthController auth = AuthController.getInstance();
                User currentUser = auth.getLoggedUser();

                if (currentUser != null) {
                    if (currentUser.getRole() == Role.department_manager) {
                        buildHeaderStructure("Dashboard - Setor " + currentUser.getDepartment().getName(), false, false, true, showNotificationButton);
                    }
                } else {
                    buildHeaderStructure("Dashboard", false, false, true, showNotificationButton);
                }
            }
            case "Collaborators.fxml" -> {
                Button buttonAddCollaborator = new Button("Adicionar Colaborador");
                buttonAddCollaborator.setOnMouseClicked(event2 -> {
                    TemplateViewModel.switchScreen("RegisterCollaborator.fxml");
                    updateHeader("RegisterCollaborator.fxml");
                });

                buildHeaderStructure("Colaboradores", false, true, true, showNotificationButton, buttonAddCollaborator);
            }
            // NOVO: Caso para a tela de Notificações, e atualização das telas modais (que não devem ter o botão)
            case "RegisterCollaborator.fxml", "CollaboratorGoals.fxml", "AddGoalFromTemplate.fxml", "Goal.fxml",
                 "AddActivity.fxml", "Notifications.fxml" -> {
                buildHeaderStructure(
                        switch (page) {
                            case "RegisterCollaborator.fxml" -> "Adicionar Colaborador";
                            case "CollaboratorGoals.fxml" -> "PDI do Colaborador";
                            case "AddGoalFromTemplate.fxml", "Goal.fxml" -> "Adicionar Meta";
                            case "AddActivity.fxml" -> "Adicionar Atividade";
                            case "Notifications.fxml" -> "Notificações"; // NOVO
                            default -> "Plano de Desenvolvimento Individual";
                        },
                        true, false, false, false // Oculta o botão de notificação nessas telas
                );
            }
            default -> {
                buildHeaderStructure("Plano de Desenvolvimento Individual", false, false, false, showNotificationButton);
            }
        }
    }

    public void buildHeaderStructure(String label, boolean returnBtn, boolean searchBar, boolean filterButton, boolean notificationButton, Node... headerItems) {
        headerLabel.setText(label);
        headerButtonsField.getChildren().clear();

        if (headerItems == null) return;

        setReturnButtonVisible(returnBtn);
        setFilterButtonVisible(filterButton);
        setSearchBarVisible(searchBar);
        setNotificationButtonVisible(notificationButton); // NOVO

        Arrays.stream(headerItems).forEach(item -> {
            if (item instanceof Button btn) {
                btn.getStyleClass().add("basic-button");
                headerButtonsField.getChildren().add(btn);
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

    public static void addButton(Button button) {
        instance.headerButtonsField.getChildren().addLast(button);
    }

    public static void addButton(int position, Button button) {
        instance.headerButtonsField.getChildren().add(position, button);
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
