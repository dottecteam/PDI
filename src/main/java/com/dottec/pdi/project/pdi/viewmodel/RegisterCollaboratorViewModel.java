package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.controllers.DepartmentController;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Department; // Import do Model
import com.dottec.pdi.project.pdi.utils.FXUtils;
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
        populateDepartments(); // Método para popular o ChoiceBox

        //TextFields focused mode
        formPane.getChildren().stream()
                .filter(node -> node instanceof StackPane)
                .forEach(stack -> {
                    StackPane stackPane = (StackPane) stack;
                    var input = stackPane.getChildren().get(1);
                    Label label = (Label) stackPane.getChildren().get(0);

                    input.focusedProperty().addListener((obs, oldVal, newVal) -> updateLabel(input, label));
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

    private void updateLabel(Node node, Label label) {
        if (node.isFocused() || FXUtils.isFilled(node)) {
            label.getStyleClass().add("formInput-label-focused");
        } else {
            label.getStyleClass().remove("formInput-label-focused");
        }
    }

    @FXML
    private void saveCollaborator(ActionEvent event) {
        if (!validateFields()) {
            TemplateViewModel.showErrorMessage("Erro de validação", "Por favor, preencha todos os campos obrigatórios corretamente.");
            return;
        }

        String name = formAddCollaboratorName.getText();
        String email = formAddCollaboratorEmail.getText();
        String cpf = formAddCollaboratorCPF.getText();
        Department selectedDepartment = formAddCollaboratorDepartment.getValue();

        try {

            CollaboratorController.saveCollaborator(name, cpf, email, selectedDepartment);

            TemplateViewModel.showSuccessMessage("Colaborador cadastrado com sucesso.");
            TemplateViewModel.switchScreen("Collaborators.fxml");
        } catch (Exception e) {
            TemplateViewModel.showErrorMessage("Erro no Cadastro", "Ocorreu um erro ao salvar o colaborador: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        if (!FieldValidator.validarCampo(formAddCollaboratorName.getText())) {
            System.out.println("Nome Inválido");
            return false;
        }
        if (!FieldValidator.validarCampo(formAddCollaboratorCPF.getText()) || !FieldValidator.validarCPF(formAddCollaboratorCPF.getText())) {
            System.out.println("CPF Inválido");
            return false;
        }
        if (!FieldValidator.validarCampo(formAddCollaboratorEmail.getText()) || !FieldValidator.validarEmail(formAddCollaboratorEmail.getText())) {
            System.out.println("Email Inválido");
            return false;
        }
        // Validação para o ChoiceBox de Departamento
        if (formAddCollaboratorDepartment.getValue() == null) {
            System.out.println("Departamento não selecionado");
            return false;
        }
        // As validações de 'Experience' e 'Observations' foram removidas pois não estão no novo model de Collaborator.

        return true;
    }
}