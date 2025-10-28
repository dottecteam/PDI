package com.dottec.pdi.project.pdi.viewmodel;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.util.Arrays;

public class HeaderViewModel {
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

    public static HeaderViewModel instance;

    public static void updateHeader(String page){
        setReturnButtonVisible(false);
        setSearchBarVisible(false);
        setFilterButtonVisible(false);
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
                buildHeaderStructure("Dashboard", false, true, true);
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
                Button buttonAddGoal = new Button("Adicionar Meta");
                MenuItem goalFromTemplateButton = new MenuItem("Templates");
                MenuItem emptyGoalButton = new MenuItem("Nova Meta");
                goalFromTemplateButton.getStyleClass().add("basic-button");
                emptyGoalButton.getStyleClass().add("basic-button");

                goalFromTemplateButton.setOnAction(ft -> {
                    TemplateViewModel.switchScreen("AddGoalFromTemplate.fxml");
                    updateHeader("AddGoalFromTemplate.fxml");
                });

                emptyGoalButton.setOnAction(ft -> {
                    TemplateViewModel.switchScreen("Goal.fxml", controller -> {
                        if(controller instanceof GoalViewModel goalViewModel){
                            goalViewModel.setGoalViewModel(goalViewModel);
                        }
                    });
                    updateHeader("Goal.fxml");
                });

                ContextMenu buttonOptions = new ContextMenu(emptyGoalButton, goalFromTemplateButton);
                buttonOptions.getStyleClass().add("context-menu-buttons");

                buttonAddGoal.setOnMouseClicked(event2 -> buttonOptions.show(buttonAddGoal, Side.LEFT, 20, 40));

                buildHeaderStructure("PDI do Colaborador", true, false, false, buttonAddGoal);
            }
            case "AddGoalFromTemplate.fxml" -> {
                buildHeaderStructure("Adicionar Meta", true, false, false);
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

    private void saveCurrentState(){

    }
}
