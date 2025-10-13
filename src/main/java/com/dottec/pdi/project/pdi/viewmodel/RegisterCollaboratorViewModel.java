package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.utils.FieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;

public class RegisterCollaboratorViewModel {
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

    private CollaboratorStatus collaboratorStatus = CollaboratorStatus.active;

    @FXML
    private VBox formPane;

    private String[] departments= {"Desenvolvimento", "UX/UI Design", "Infraestrutura de TI", "Inteligência de Dados"};

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

    private void updateLabel(TextField textField, Label inputLabel){
        if (textField.isFocused() || !textField.getText().isEmpty()) {
            inputLabel.setStyle("-fx-text-fill: #4B0081; -fx-padding: 1 15;");
        } else {
            inputLabel.setStyle("-fx-text-fill: #808080; -fx-padding: 15;");
        }
    }

    private void updateChoiceBoxLabel(ChoiceBox choiceBox, Label label){
        if (choiceBox.isFocused() || choiceBox.getValue() != null) {
            label.setStyle("-fx-text-fill: #4B0081; -fx-padding: 1 15;");
        } else {
            label.setStyle("-fx-text-fill: #808080; -fx-padding: 15");
        }
    }

    // Button Click
    @FXML
    private void saveCollaborator(ActionEvent event){

        if (!valideFields()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de validação");
            alert.setHeaderText(null);
            alert.setContentText("Há algum campo inválido");
            alert.showAndWait();
            return;
        }

        String name = formAddCollaboratorName.getText();
        String email = formAddCollaboratorEmail.getText();
        String cpf = formAddCollaboratorCPF.getText();

        // Teste
        // São valores que precisarão passar por busca no banco de dados para o select
          int department = 1;
          int role = 1;

        String experience = formAddCollaboratorExperience.getText();
        String observations = formAddCollaboratorObservations.getText();

        Collaborator collaborator = new Collaborator(0, name, email, cpf, department, role, experience, observations, collaboratorStatus);


        // Teste
        // Observando se os valores estão chegando
        System.out.println(collaborator.toString());
        collaboratorController.saveCollaborator(collaborator);
    }

    private boolean valideFields(){
        if(!FieldValidator.validarCampo(formAddCollaboratorName.getText())) {
            System.out.println("Nome Inválido");
            return false;
        }
        if(!FieldValidator.validarCampo(formAddCollaboratorObservations.getText())){
            System.out.println("Observação Inválido");
            return false;
        }
        if(!FieldValidator.validarCampo(formAddCollaboratorExperience.getText())) {
            System.out.println("Experiência Inválido");
            return false;
        }
        if(!FieldValidator.validarCampo(formAddCollaboratorCPF.getText()) || !FieldValidator.validarCPF(formAddCollaboratorCPF.getText())) {
            System.out.println("CPF Inválido");
            return false;
        }
        if(!FieldValidator.validarCampo(formAddCollaboratorEmail.getText()) || !FieldValidator.validarEmail(formAddCollaboratorEmail.getText())) {
            System.out.println("Email Inválido");
            return false;
        }

        return true;
    }
}
