package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.enums.Role;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Arrays;
import java.util.Objects;

import com.dottec.pdi.project.pdi.controllers.AuthController;
import com.dottec.pdi.project.pdi.model.User;

public class HeaderViewModel{
    @FXML
    private HBox header;

    @FXML
    private HBox headerHBox;

    @FXML
    private HBox headerItemsField;

    @FXML
    private Label headerLabel;

    @FXML
    private HBox headerButtonsField;

    @FXML
    private HBox headerSearchBarField;

    @FXML
    private HBox headerFilterButtonField;

    @FXML
    private HBox headerReturnButtonField;

    private String[] pageHistory = new String[10];

    public HeaderViewModel() {}

    public static HeaderViewModel instance;

    private void updatePageHistory(String page){
        for(int i = pageHistory.length - 1; i > 0; i--){
            pageHistory[i] = pageHistory[i-1];
        }
        pageHistory[0] = page;
    }

    public static void updateHeader(String page){
        instance.buildHeader(page);
        instance.updatePageHistory(page);
    }

    @FXML
    private void initialize(){
        instance = this;
    }

    private void buildHeader(String page){
        switch(page) {
            case "Dashboard.fxml" -> {
                Button buttonFilterDashboard = new Button("Filtrar");
                buttonFilterDashboard.setId("filterDashboard");
                buttonFilterDashboard.getStyleClass().add("filter-button");

                AuthController auth = AuthController.getInstance();
                User currentUser = auth.getLoggedUser();

                if (currentUser != null){
                    if (currentUser.getRole() == Role.department_manager) {
                        buildHeaderStructure(false, "Dashboard - Setor " + currentUser.getDepartment().getName(), buttonFilterDashboard);
                    }
                    else{
                        buildHeaderStructure(false, "Dashboard", buttonFilterDashboard);
                    }
                }

            }
            case "Collaborators.fxml" -> {
                Button buttonAddCollaborator = new Button("Adicionar Colaborador");
                buttonAddCollaborator.setOnMouseClicked(event2 -> {
                    TemplateViewModel.switchScreen("RegisterCollaborator.fxml");
                    updateHeader("RegisterCollaborator.fxml");
                });

                Button buttonFilterCollaborators = new Button("Filtrar");
                buttonFilterCollaborators.getStyleClass().add("filter-button");

                TextField searchBarCollaborators = new TextField();
                searchBarCollaborators.setId("searchBarCollaborators");

                buildHeaderStructure(false,"Colaboradores", buttonAddCollaborator, buttonFilterCollaborators, searchBarCollaborators);
            }
            case "RegisterCollaborator.fxml" -> {
                buildHeaderStructure(true, "Adicionar Colaborador");
            }
            case "Goals.fxml" -> {
                Button buttonAddGoal = new Button("Adicionar Objetivo");
                buttonAddGoal.setOnMouseClicked(event2 -> TemplateViewModel.switchScreen("AddActivity.fxml"));

                Button buttonFilterGoals = new Button("Filtrar");
                buttonFilterGoals.getStyleClass().add("filter-button");

                TextField searchBarGoals = new TextField();
                buildHeaderStructure(true, "PDI do Colaborador", buttonAddGoal, buttonFilterGoals, searchBarGoals);
            }
            case "AddGoal.fxml" -> {
                buildHeaderStructure(true, "Adicionar Atividade");
            }

        }

    }

    public void buildHeaderStructure(boolean returnButton, String label, Node... headerItems) {
        headerLabel.setText(label);

        headerButtonsField.getChildren().clear();
        headerReturnButtonField.getChildren().clear();
        headerSearchBarField.getChildren().clear();
        headerFilterButtonField.getChildren().clear();

        if (headerItems == null) return;

        if (returnButton){
            Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/dottec/pdi/project/pdi/static/img/arrow-left.png")));
            ImageView icon = new ImageView(img);
            icon.setFitWidth(16);
            icon.setFitHeight(16);
            Button returnBtn = new Button();
            returnBtn.setGraphic(icon);
            returnBtn.getStyleClass().add("return-button");
            headerReturnButtonField.getChildren().add(returnBtn);
            returnBtn.setOnMouseClicked(event -> {
                updateHeader(pageHistory[1]);
                TemplateViewModel.switchScreen(pageHistory[0]);
            });
        }

        Arrays.stream(headerItems).forEach(item -> {
            if (item instanceof Button btn) {
                if (btn.getStyleClass().contains("filter-button")) {
                    headerFilterButtonField.getChildren().add(btn);
                } else {
                    btn.getStyleClass().add("basic-button");
                    headerButtonsField.getChildren().add(btn);
                }
            } else if (item instanceof TextField tf) {
                tf.getStyleClass().add("search-bar");
                Label searchIcon = new Label("🔍");
                searchIcon.setStyle("-fx-padding: 1; -fx-font-size: 24; -fx-text-fill: #4B0081");
                headerSearchBarField.getChildren().add(searchIcon);
                headerSearchBarField.getChildren().add(tf);
            } else {
                System.out.println("Node ignorado: " + item.getClass().getSimpleName());
            }
        });
    }
}
