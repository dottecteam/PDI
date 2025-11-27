package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.controllers.AuthController;
import com.dottec.pdi.project.pdi.dao.AuthDAO;
import com.dottec.pdi.project.pdi.dao.UserDAO;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.enums.UserStatus;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.User;
import com.dottec.pdi.project.pdi.utils.PasswordHasher;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import com.dottec.pdi.project.pdi.viewmodel.TemplateViewModel;
import javafx.util.Pair;


public class ProfileViewModel implements Initializable {
    private User user;

    @FXML
    private Label labelNome;

    @FXML
    private Label labelEmail;

    @FXML
    private Label labelCargo;

    @FXML
    private Label labelStatus;

    @FXML
    private AnchorPane anchorLogout;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUser(AuthController.getInstance().getLoggedUser());
        updateUserFields();
    }

    //Método para chamar a tela de login quando clicar em 'logout'
    public static void switchToLoginScene(Node sourceNode) {
        URL resource = ProfileViewModel.class.getResource("/com/dottec/pdi/project/pdi/views/Login.fxml");

        try {
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = (Stage) sourceNode.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Login - PDI");
            stage.show();

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de Login. Caminho FXML incorreto: " + resource);
            e.printStackTrace();
        } catch (ClassCastException e) {
            System.err.println("Erro ao obter o Stage. O Node de origem não está em uma Scene ativa.");
            e.printStackTrace();
        }
    }

    @FXML
    public void logout(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja mesmo sair?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            AuthController.getInstance().logout();
            switchToLoginScene((Node) event.getSource());
        } else {
            event.consume();
        }

    }

    public void setUser(User user) {
        this.user = user;
        if (user.getStatus() == UserStatus.active) {
            updateUserFields();
        } else {
            updateUserFields();
        }
    }

    public String findDepartment(int id) {
        String sqlDepartment = "SELECT dep_name FROM departments INNER JOIN users ON departments.dep_id = users.department_id where use_id = ?;";

        String dep = null;
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sqlDepartment)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dep = rs.getString("dep_name");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar departamento por ID: " + e.getMessage(), e);
        }
        return dep;
    }

    private void updateUserFields() {
        if (user == null) return;

        labelNome.setText(user.getName());
        labelEmail.setText(user.getEmail());

        String cargo = "";
        String userCargo = String.valueOf(user.getRole());
        switch (userCargo) {
            case "department_manager":
                // GARANTINDO que o nome do setor seja preenchido
                String departmentName = findDepartment(AuthController.getInstance().getLoggedUser().getId());
                cargo = "Gerente do setor " + (departmentName != null ? departmentName : "Não Definido");
                break;
            case "hr_manager":
                cargo = "Gerente de RH";
                break;
            case "general_manager":
                cargo = "Gerente geral";
                break;
            default:
                cargo = userCargo;
        }


        String status = "";
        String userStatus = String.valueOf(user.getStatus());

        // Remove as classes de status antigas antes de aplicar as novas
        labelStatus.getStyleClass().removeAll("status-active", "status-inactive");

        switch (userStatus) {
            case "active":
                status = "Ativo";
                labelStatus.getStyleClass().add("status-active");
                // CORREÇÃO VISUAL: Se você quer que o texto seja claro no CSS, não precisa do -fx-text-fill aqui.
                break;

            case "inactive":
                status = "Inativo";
                labelStatus.getStyleClass().add("status-inactive");
                // CORREÇÃO VISUAL: Se você quer que o texto seja claro no CSS, não precisa do -fx-text-fill aqui.
                break;

            default:
                status = userStatus;
        }
        labelStatus.setText(status);
        labelCargo.setText(cargo);

    }

    @FXML
    private void handlePasswordChange() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Alterar Senha");
        dialog.setHeaderText("Insira sua senha atual e a nova senha.");

        // Configura o botão de confirmação
        ButtonType loginButtonType = new ButtonType("Confirmar", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Cria os campos de entrada (senha atual, nova senha)
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField currentPassword = new PasswordField();
        currentPassword.setPromptText("Senha Atual");
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("Nova Senha");
        PasswordField confirmNewPassword = new PasswordField();
        confirmNewPassword.setPromptText("Confirmar Nova Senha");

        grid.add(new Label("Senha Atual:"), 0, 0);
        grid.add(currentPassword, 1, 0);
        grid.add(new Label("Nova Senha:"), 0, 1);
        grid.add(newPassword, 1, 1);
        grid.add(new Label("Confirmar Senha:"), 0, 2);
        grid.add(confirmNewPassword, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Adiciona a lógica de validação
        Platform.runLater(currentPassword::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(currentPassword.getText(), newPassword.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(passwords -> {
            String currentPass = passwords.getKey();
            String newPass = passwords.getValue();
            String confirmedPass = confirmNewPassword.getText();

            if (newPass.isEmpty() || currentPass.isEmpty() || confirmedPass.isEmpty()) {
                TemplateViewModel.showErrorMessage("Erro de Validação", "Todos os campos de senha são obrigatórios.");
                return;
            }

            if (!newPass.equals(confirmedPass)) {
                TemplateViewModel.showErrorMessage("Erro de Validação", "A nova senha e a confirmação não coincidem.");
                return;
            }

            // Requisito de complexidade (opcional, aqui apenas verifica se tem um tamanho mínimo)
            if (newPass.length() < 6) {
                TemplateViewModel.showErrorMessage("Erro de Validação", "A nova senha deve ter pelo menos 6 caracteres.");
                return;
            }

            // Lógica de Atualização
            performPasswordUpdate(currentPass, newPass);
        });
    }

    // NOVO MÉTODO: Executa a atualização da senha no banco de dados
    private void performPasswordUpdate(String currentPassword, String newPassword) {
        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser == null) {
            TemplateViewModel.showErrorMessage("Erro", "Usuário não autenticado.");
            return;
        }

        // 1. Verificar a senha atual
        String storedHash = loggedUser.getPasswordHash();

        // CORREÇÃO: Utilizar PasswordUtil para verificar a senha (se o projeto utiliza jbcrypt)
        // Se o projeto usa a implementação do PasswordHasher para BCrypt:
        if (!PasswordHasher.verify(currentPassword, storedHash)) {
            // Se o projeto usa a implementação do LoginViewModel original para simulação de hashcode (NÃO RECOMENDADO, mas presente em alguns arquivos):
            // if (currentPassword.hashCode() != storedHash.hashCode()) {
            TemplateViewModel.showErrorMessage("Erro de Segurança", "A senha atual está incorreta.");
            return;
        }

        // 2. Criar o novo hash da senha
        String newPasswordHash = PasswordHasher.hash(newPassword);

        // 3. Atualizar no banco
        try {
            UserDAO.updatePassword(loggedUser.getId(), newPasswordHash);

            // 4. Atualizar o objeto do usuário logado em memória
            loggedUser.setPasswordHash(newPasswordHash);

            TemplateViewModel.showSuccessMessage("Sucesso", "Senha alterada com sucesso!");
        } catch (Exception e) {
            TemplateViewModel.showErrorMessage("Erro no Banco de Dados", "Falha ao atualizar a senha: " + e.getMessage());
        }
    }
}
