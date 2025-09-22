package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.Application;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.enums.Status;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

import java.io.IOException;

public class RegisterCollaboratorController {
    //fields
    @FXML
    private TextField formAddCollaboratorName;
    @FXML
    private TextField formAddCollaboratorEmail;
    @FXML
    private TextField formAddCollaboratorCPF;
    @FXML
    private ChoiceBox<String> formAddCollaboratorDepartment;
    @FXML
    private ChoiceBox<String> formAddCollaboratorRole;
    @FXML
    private TextField formAddCollaboratorExperience;
    @FXML
    private TextField formAddCollaboratorObservations;
    private Status status = Status.active;

    @FXML
    private VBox formPane;


    //buttons
    @FXML
    private Button formAddCollaboratorConfirmButton;

    private String[] departments= {"Desenvolvimento", "UX/UI Design", "Infraestrutura de TI", "Inteligência de Dados"};
    private String[] roles= {"Desenvolvedor Fullstack", "UX Designer", "Analista de Dados", "Desenvolvedor Backend"};

    CollaboratorController collaboratorController = new CollaboratorController();

    @FXML
    public void initialize() {
        //formAddCollaboratorConfirmButton.setOnAction(event -> saveCollaborator());
        disableMouseOnLabels(formPane);

        //filling department choice box
        formAddCollaboratorDepartment.getItems().addAll(departments);
        formAddCollaboratorRole.getItems().addAll(roles);

        //TextFields focused mode
        formPane.getChildren().stream()
                .filter(node -> node instanceof StackPane)
                .forEach(stack -> {
                    StackPane stackPane = (StackPane) stack;
                    var input = stackPane.getChildren().get(0);
                    Label label = (Label) stackPane.getChildren().get(1);

                    if(input instanceof TextField textField){
                        textField.focusedProperty().addListener((obs, oldVal, newVal) -> updateLabel(textField, label));
                        textField.textProperty().addListener((obs, oldVal, newVal) -> updateLabel(textField, label));
                    } else if (stackPane.getChildren().get(0) instanceof ChoiceBox choiceBox) {
                        choiceBox.focusedProperty().addListener((obs, oldVal, newVal) -> updateChoiceBoxLabel(choiceBox, label));
                    }

                });


    }

    private void disableMouseOnLabels(Parent parent){
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Label) {
                node.setMouseTransparent(true);
            }
            if (node instanceof Parent) {
                disableMouseOnLabels((Parent) node);
            }
        }
    }

    private void updateLabel(TextField textField, Label label){
        if (textField.isFocused() || !textField.getText().isEmpty()) {
            label.setStyle("-fx-text-fill: #4B0081; -fx-padding: 1 20;");
        } else {
            label.setStyle("-fx-text-fill: #808080; -fx-padding: 20;");
        }
    }

    private void updateChoiceBoxLabel(ChoiceBox choiceBox, Label label){
        if (choiceBox.isFocused() || choiceBox.getValue() != null) {
            label.setStyle("-fx-text-fill: #4B0081; -fx-padding: 1 20;");
        } else {
            label.setStyle("-fx-text-fill: #808080; -fx-padding: 20;");
        }
    }

    /*private void saveCollaborator(){
        int id = 123;
        String name = formAddCollaboratorName.getText();
        String email = formAddCollaboratorEmail.getText();
        String cpf = formAddCollaboratorCPF.getText();
        int department = 2;
        String role = formAddCollaboratorRole.getText();
        String experience = formAddCollaboratorExperience.getText();
        String observations = formAddCollaboratorObservations.getText();

        Collaborator collaborator = new Collaborator(id, name, email, cpf, department, role, experience, observations, status);

        collaboratorController.saveCollaborator(collaborator);
    }*/






}
