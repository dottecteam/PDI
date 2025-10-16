package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.AuthController; // Garante a importação correta
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginViewModel {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;


    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // 1. Validação de campos vazios
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Campos obrigatórios", "Por favor, preencha o email e a senha.", Alert.AlertType.WARNING);
            return;
        }

        // 2. Chamada ao Controller estático
        boolean success = AuthController.login(email, password);

        if (success) {
            // Login bem-sucedido: Navegar para o Dashboard
            try {
                // Use a referência do recurso (Dashboard.fxml)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dottec/pdi/project/pdi/Dashboard.fxml"));
                Parent root = loader.load();

                // Obter o Stage atual
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
            } catch (Exception e) {
                // Erro ao carregar a próxima tela (problema de FXML/IO)
                showAlert("Erro de Navegação", "Falha ao carregar o painel: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            // Login falhou
            // Utilizamos ERROR ou INFORMATION dependendo da regra de negócio,
            // mas ERROR é melhor para credenciais inválidas.
            showAlert("Login Inválido", "Email ou senha incorretos. Por favor, tente novamente.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Exibe um alerta personalizado.
     * @param title O título da janela de alerta.
     * @param message O conteúdo da mensagem.
     * @param type O tipo de alerta (WARNING, ERROR, INFORMATION).
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}