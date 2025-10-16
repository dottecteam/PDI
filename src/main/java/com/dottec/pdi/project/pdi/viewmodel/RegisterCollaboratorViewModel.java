package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.controllers.DepartmentController;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Department; // Import do Model
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
import javafx.util.StringConverter; // Import necessário

import java.util.List;

public class RegisterCollaboratorViewModel {
    //fields
    @FXML
    private TextField formAddCollaboratorName;
    @FXML
    private TextField formAddCollaboratorEmail;
    @FXML
    private TextField formAddCollaboratorCPF;
    @FXML
    private ChoiceBox<Department> formAddCollaboratorDepartment;

    @FXML
    private VBox formPane;

    @FXML
    public void initialize() {
        disableMouseOnLabels(formPane);
        populateDepartments(); // Método para popular o ChoiceBox

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
                    } else if (input instanceof ChoiceBox choiceBox) {
                        choiceBox.focusedProperty().addListener((obs, oldVal, newVal) -> updateChoiceBoxLabel(choiceBox, label));
                    }
                });
    }

    /**
     * Busca os departamentos do banco de dados e os adiciona ao ChoiceBox.
     */
    private void populateDepartments() {
        // Busca a lista de departamentos usando o controller
        List<Department> departments = DepartmentController.findAllDepartments();
        formAddCollaboratorDepartment.getItems().addAll(departments);

        // Configura como o objeto Department deve ser exibido no ChoiceBox (mostrando seu nome)
        formAddCollaboratorDepartment.setConverter(new StringConverter<Department>() {
            @Override
            public String toString(Department department) {
                // Se o objeto for nulo, retorna uma string vazia, senão, retorna o nome.
                return department == null ? "" : department.getName();
            }

            @Override
            public Department fromString(String string) {
                // Não é necessário implementar para um ChoiceBox não editável.
                return null;
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

    private void updateChoiceBoxLabel(ChoiceBox<?> choiceBox, Label label){
        if (choiceBox.isFocused() || choiceBox.getValue() != null) {
            label.setStyle("-fx-text-fill: #4B0081; -fx-padding: 1 15;");
        } else {
            label.setStyle("-fx-text-fill: #808080; -fx-padding: 15");
        }
    }

    // Button Click
    @FXML
    private void saveCollaborator(ActionEvent event){
        if (!validateFields()){
            showErrorAlert("Erro de validação", "Por favor, preencha todos os campos obrigatórios corretamente.");
            return;
        }

        // Obtém os dados dos campos do formulário
        String name = formAddCollaboratorName.getText();
        String email = formAddCollaboratorEmail.getText();
        String cpf = formAddCollaboratorCPF.getText();
        Department selectedDepartment = formAddCollaboratorDepartment.getValue(); // Pega o objeto Department selecionado

        try {
            // Usa o controller para salvar o colaborador
            CollaboratorController.saveCollaborator(name, cpf, email, selectedDepartment);
            showSuccessAlert("Sucesso!", "Colaborador cadastrado com sucesso.");
            // Opcional: Limpar os campos após o sucesso
            // clearFields();
        } catch (Exception e) {
            showErrorAlert("Erro no Cadastro", "Ocorreu um erro ao salvar o colaborador: " + e.getMessage());
        }
    }

    private boolean validateFields(){
        if(!FieldValidator.validarCampo(formAddCollaboratorName.getText())) {
            System.out.println("Nome Inválido");
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
        // Validação para o ChoiceBox de Departamento
        if(formAddCollaboratorDepartment.getValue() == null) {
            System.out.println("Departamento não selecionado");
            return false;
        }
        // As validações de 'Experience' e 'Observations' foram removidas pois não estão no novo model de Collaborator.

        return true;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}