package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.AuthController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LoginViewModel {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button logar;


    @FXML
    public void handleLogin(MouseEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Campos obrigatórios", "Por favor, preencha todos os campos.");
            return;
        }

        boolean success = AuthController.login(email, password);

        if (success) {
            try {
                Stage currentStage = (Stage) emailField.getScene().getWindow();
                currentStage.close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dottec/pdi/project/pdi/views/Template.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("PDI");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                showAlert("Erro", "Falha ao carregar o painel: " + e.getMessage());
            }
        } else {
            showAlert("Login inválido", "Email ou senha incorretos.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
