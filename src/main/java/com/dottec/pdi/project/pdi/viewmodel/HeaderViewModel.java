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

public class HeaderViewModel{
    @FXML private HBox header;
    @FXML private HBox headerHBox;
    @FXML private HBox headerButtonsField;

    //Header items
    @FXML private Label headerLabel;
    @FXML private Button returnButton;
    @FXML private Button filterButton;
    @FXML private TextField searchBar;
    @FXML private StackPane searchBarStackPane;

    public HeaderViewModel() {}

    static HeaderViewModel instance;
    public static void setInstance(HeaderViewModel headerViewModel){
        instance = headerViewModel;
    }
    public static HeaderViewModel getController(){
        return instance;
    }

    public static void updateHeader(String page){
        instance.buildHeader(page);
    }

    @FXML
    private void initialize(){
        instance = this;
        returnButton.setOnMouseClicked(e -> TemplateViewModel.goBack());
    }

    private void buildHeader(String page){
        switch(page) {
            case "Dashboard.fxml" -> {
                AuthController auth = AuthController.getInstance();
                User currentUser = auth.getLoggedUser();

                if (currentUser != null){
                    if (currentUser.getRole() == Role.department_manager) {
                        buildHeaderStructure("Dashboard - Setor " + currentUser.getDepartment().getName(), false, false, true);
                    }
                } else{
                    buildHeaderStructure("Dashboard", false, false, true);
                }
            }
            case "Collaborators.fxml" -> {
                Button buttonAddCollaborator = new Button("Adicionar Colaborador");
                buttonAddCollaborator.setOnMouseClicked(event2 -> {
                    TemplateViewModel.switchScreen("RegisterCollaborator.fxml");
                    updateHeader("RegisterCollaborator.fxml");
                });

                buildHeaderStructure("Colaboradores", false, true, true, buttonAddCollaborator);
            }
            case "RegisterCollaborator.fxml" -> {
                buildHeaderStructure("Adicionar Colaborador", true, false, false);
            }
            case "CollaboratorGoals.fxml" -> {
                buildHeaderStructure("PDI do Colaborador", true, false, false);
            }
            case "AddGoalFromTemplate.fxml", "Goal.fxml" -> {
                buildHeaderStructure("Adicionar Meta", true, false, false);
            }
            case "AddActivity.fxml" -> {
                buildHeaderStructure("Adicionar Atividade", true, false, false);
            }
            case "Settings.fxml" -> {
                Button buttonAddSector = new Button("Adicionar Setor");
                buttonAddSector.setOnMouseClicked(event2 -> {
                    TemplateViewModel.switchScreen("AddSector.fxml");
                    updateHeader("AddSector.fxml");
                });

                buildHeaderStructure("Setor", false, false, false, buttonAddSector);
            }
            case "AddSector.fxml" -> {
                buildHeaderStructure("Adicionar Setor", true, false, false);
            }
            case "UserManagement.fxml" -> {
                buildHeaderStructure("Gerenciar Usuários", true, false, false);
            }
            default -> {
                buildHeaderStructure("Plano de Desenvolvimento Individual", false, false, false);
            }

        }

    }

    public void buildHeaderStructure(String label, boolean returnBtn, boolean searchBar, boolean filterButton, Node... headerItems) {
        headerLabel.setText(label);
        headerButtonsField.getChildren().clear();

        if (headerItems == null) return;

        setReturnButtonVisible(returnBtn);
        setFilterButtonVisible(filterButton);
        setSearchBarVisible(searchBar);

        Arrays.stream(headerItems).forEach(item -> {
            if (item instanceof Button btn) {
                btn.getStyleClass().add("basic-button");
                headerButtonsField.getChildren().add(btn);
            }
        });
    }

    public static void setReturnButtonVisible(Boolean visible){
        instance.returnButton.setVisible(visible);
        instance.returnButton.setManaged(visible);
    }

    public static void setSearchBarVisible(Boolean visible){
        instance.searchBarStackPane.setVisible(visible);
        instance.searchBarStackPane.setManaged(visible);
    }

    public static void setFilterButtonVisible(Boolean visible){
        instance.filterButton.setVisible(visible);
        instance.filterButton.setManaged(visible);
    }

    public static void setLabel(String label){
        instance.headerLabel.setText(label);
    }

    public static void removeButton(int index){
        instance.headerButtonsField.getChildren().remove(index);
    }
    public static void removeLastButton(){
        instance.headerButtonsField.getChildren().removeLast();
    }
    public static void removeFirstButton(){
        instance.headerButtonsField.getChildren().removeFirst();
    }
    public static void addButton(Button button){
        instance.headerButtonsField.getChildren().addLast(button);
    }
    public static void addButton(int position, Button button){
        instance.headerButtonsField.getChildren().add(position, button);
    }
    public static void clearButtons(){
        instance.headerButtonsField.getChildren().clear();
    }

    public static void clear(){
        clearButtons();
        setLabel("Plano de Desenvolvimento Individual");
        setReturnButtonVisible(false);
        setSearchBarVisible(false);
    }
}
