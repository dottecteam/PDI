// main/java/com/dottec/pdi/project/pdi/viewmodel/UserFormViewModel.java
package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.DepartmentController;
import com.dottec.pdi.project.pdi.controllers.UserController;
import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.enums.UserStatus;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.User;
import com.dottec.pdi.project.pdi.utils.FXUtils;
import com.dottec.pdi.project.pdi.utils.PasswordHasher;
import com.dottec.pdi.project.pdi.utils.FieldValidator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class UserFormViewModel {

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ChoiceBox<Role> cbRole;
    @FXML
    private ChoiceBox<Department> cbDepartment;
    @FXML
    private VBox formPane;
    @FXML
    private StackPane passwordFieldContainer;
    @FXML
    private StackPane departmentFieldContainer;
    @FXML
    private Button btnConfirm;
    @FXML
    private Label passwordLabel;

    private User userToEdit = null;

    @FXML
    public void initialize() {
        populateRoles();
        populateDepartments();
        setupFieldFocus();

        // Listener para o Cargo: mostra o departamento apenas para Gerente de Departamento
        cbRole.getSelectionModel().selectedItemProperty().addListener((obs, oldRole, newRole) -> {
            boolean isManager = newRole == Role.department_manager;
            departmentFieldContainer.setVisible(isManager);
            departmentFieldContainer.setManaged(isManager);
        });
    }

    public void setUserToEdit(User user) {
        this.userToEdit = user;
        if (user != null) {
            txtName.setText(user.getName());
            txtEmail.setText(user.getEmail());
            cbRole.setValue(user.getRole());

            if (user.getRole() == Role.department_manager && user.getDepartment() != null) {
                cbDepartment.setValue(user.getDepartment());
            }

            // Oculta o campo de senha para Edição.
            passwordFieldContainer.setVisible(false);
            passwordFieldContainer.setManaged(false);
            btnConfirm.setText("Salvar Alterações");

            // CORREÇÃO DO BUG VISUAL: Força a label a subir (flutuar) quando os dados são carregados
            enforceLabelFocus(); // <--- CHAMADA ADICIONADA AQUI
        } else {
            btnConfirm.setText("Adicionar Usuário");
        }
    }

    private void enforceLabelFocus() {
        formPane.getChildren().stream()
                .filter(node -> node instanceof StackPane)
                .forEach(stack -> {
                    StackPane stackPane = (StackPane) stack;
                    if (stackPane.getChildren().size() > 1) {
                        Node input = stackPane.getChildren().get(1);
                        Label label = (Label) stackPane.getChildren().get(0);

                        // Chama updateLabel, que verifica se o campo está preenchido (mesmo sem foco)
                        updateLabel(input, label);
                    }
                });
    }

    private void populateRoles() {
        cbRole.getItems().addAll(Arrays.asList(Role.values()));
        cbRole.setConverter(new StringConverter<Role>() {
            @Override
            public String toString(Role role) {
                if (role == null) {
                    return "";
                }
                return translateRole(role);
            }

            @Override
            public Role fromString(String string) {
                return null;
            }
        });
    }

    private String translateRole(Role role) {
        return switch (role) {
            case hr_manager -> "Gerente de RH";
            case department_manager -> "Gerente de Departamento";
            case general_manager -> "Gerente Geral";
        };
    }

    private void populateDepartments() {
        List<Department> departments = DepartmentController.findAllDepartments();
        cbDepartment.getItems().addAll(departments);

        cbDepartment.setConverter(new StringConverter<Department>() {
            @Override
            public String toString(Department department) {
                return department == null ? "" : department.getName();
            }

            @Override
            public Department fromString(String string) {
                return null;
            }
        });
    }

    private void setupFieldFocus() {
        formPane.getChildren().stream()
                .filter(node -> node instanceof StackPane)
                .forEach(stack -> {
                    StackPane stackPane = (StackPane) stack;
                    if (stackPane.getChildren().size() > 1) {
                        Node input = stackPane.getChildren().get(1);
                        Label label = (Label) stackPane.getChildren().get(0);

                        input.focusedProperty().addListener((obs, oldVal, newVal) -> updateLabel(input, label));
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
    private void handleSave(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        String name = txtName.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        Role selectedRole = cbRole.getValue();
        Department selectedDepartment = cbDepartment.getValue();

        try {
            User newUser = userToEdit != null ? userToEdit : new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setRole(selectedRole);
            newUser.setStatus(UserStatus.active);

            // Lógica de Departamento
            if (selectedRole == Role.department_manager) {
                newUser.setDepartment(selectedDepartment);
            } else {
                newUser.setDepartment(null);
            }

            if (userToEdit == null) {
                // Criação (requer senha)
                String passwordHash = PasswordHasher.hash(password);
                newUser.setPasswordHash(passwordHash);

                boolean saved = UserController.addUser(newUser);
                if (saved) {
                    FXUtils.showSuccessMessage("Usuário " + name + " cadastrado com sucesso.");
                } else {
                    FXUtils.showErrorMessage("Erro no Cadastro", "Não foi possível adicionar o usuário. Email já em uso?");
                    return;
                }
            } else {
                // Edição (senha não é alterada aqui)
                boolean updated = UserController.updateUser(newUser);
                if (updated) {
                    FXUtils.showSuccessMessage("Usuário " + name + " atualizado com sucesso.");
                } else {
                    FXUtils.showErrorMessage("Erro na Atualização", "Não foi possível atualizar o usuário.");
                    return;
                }
            }

            // Retorna para a lista de usuários
            TemplateViewModel.switchScreen("UserManagement.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            FXUtils.showErrorMessage("Erro no Processamento", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        TemplateViewModel.goBack();
    }

    private boolean validateFields() {
        if (!FieldValidator.validarCampo(txtName.getText())) {
            FXUtils.showErrorMessage("O nome é obrigatório.");
            return false;
        }
        if (!FieldValidator.validarCampo(txtEmail.getText()) || !FieldValidator.validarEmail(txtEmail.getText())) {
            FXUtils.showErrorMessage("O email é inválido ou obrigatório.");
            return false;
        }
        if (cbRole.getValue() == null) {
            FXUtils.showErrorMessage("O Cargo é obrigatório.");
            return false;
        }

        // Validação de senha na criação
        if (userToEdit == null && !FieldValidator.validarCampo(txtPassword.getText())) {
            FXUtils.showErrorMessage("A senha é obrigatória para o novo usuário.");
            return false;
        }

        // Validação de departamento para Gerente de Departamento
        if (cbRole.getValue() == Role.department_manager && cbDepartment.getValue() == null) {
            FXUtils.showErrorMessage("O Setor é obrigatório para Gerentes de Departamento.");
            return false;
        }

        return true;
    }
}