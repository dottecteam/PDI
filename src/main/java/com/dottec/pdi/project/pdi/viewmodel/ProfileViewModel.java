package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.controllers.AuthController;
import com.dottec.pdi.project.pdi.dao.AuthDAO;
import com.dottec.pdi.project.pdi.dao.UserDAO;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.enums.UserStatus;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.User;
import com.dottec.pdi.project.pdi.utils.FXUtils;
import com.dottec.pdi.project.pdi.utils.PasswordHasher;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.dottec.pdi.project.pdi.viewmodel.TemplateViewModel;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.poi.xslf.draw.geom.XSLFXYAdjustHandle;


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

            FXUtils.switchPage(root);
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
        FXUtils.showConfirmationMessage("Deseja mesmo sair?").setOnMouseClicked(e -> {
            AuthController.getInstance().logout();
            switchToLoginScene((Node) event.getSource());
        });
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
        // Configura o botão de confirmação
        Button confirmButton = new Button("Confirmar");
        confirmButton.getStyleClass().add("basic-button");
        Button cancelButton = new Button("Cancelar");
        cancelButton.getStyleClass().add("cancel-button");

        StackPane currentPassword = createPasswordField("Senha Atual");
        StackPane newPassword = createPasswordField("Nova Senha");
        StackPane confirmNewPassword = createPasswordField("Confirmar Nova Senha");

        StackPane mainStackPane = FXUtils.getMainStackPane();

        StackPane confirmationModal = new StackPane();
        confirmationModal.getStyleClass().add("message");

        VBox vbox = new VBox();
        HBox header = new HBox();
        header.getStyleClass().add("message-header");
        header.getStyleClass().add("message-header-success");

        Label headerLabel = new Label("Confirmar Senha");
        headerLabel.getStyleClass().add("message-header-label");

        Button closeButton = new Button();
        closeButton.getStyleClass().addAll("x-button");
        closeButton.setOnAction(actionEvent -> {
            mainStackPane.getChildren().remove(confirmationModal);
        });
        header.getChildren().addAll(headerLabel, closeButton);

        HBox buttons = new HBox(cancelButton, confirmButton);
        buttons.setSpacing(10);
        buttons.setStyle("-fx-padding: 10;");
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox passwords = new VBox(currentPassword, newPassword, confirmNewPassword);
        passwords.setSpacing(5);
        passwords.setStyle("-fx-padding: 10;");

        vbox.getChildren().addAll(header, passwords, buttons);
        confirmationModal.getChildren().add(vbox);

        mainStackPane.getChildren().addLast(confirmationModal);

        Platform.runLater(() -> {
            mainStackPane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if(!mainStackPane.getChildren().contains(confirmationModal)) return;
                if (!confirmationModal.contains(confirmationModal.screenToLocal(e.getScreenX(), e.getScreenY()))) {
                    mainStackPane.getChildren().remove(confirmationModal);
                }
            });
        });

        // Adiciona a lógica de validação
        Platform.runLater(currentPassword::requestFocus);

        cancelButton.setOnMouseClicked(e -> {
            mainStackPane.getChildren().remove(confirmationModal);
            return;
        });

        confirmButton.setOnMouseClicked(e -> {
            String currentPass = "";
            if(currentPassword.getChildren().getFirst() instanceof PasswordField pf) currentPass = pf.getText();

            String newPass = "";
            if(newPassword.getChildren().getFirst() instanceof PasswordField pf) newPass = pf.getText();

            String confirmedPass = "";
            if(confirmNewPassword.getChildren().getFirst() instanceof PasswordField pf) confirmedPass = pf.getText();


            if (newPass.isEmpty() || currentPass.isEmpty() || confirmedPass.isEmpty()) {
                FXUtils.showErrorMessage("Erro de Validação", "Todos os campos de senha são obrigatórios.");
                return;
            }

            if (!newPass.equals(confirmedPass)) {
                FXUtils.showErrorMessage("Erro de Validação", "A nova senha e a confirmação não coincidem.");
                return;
            }

            // Requisito de complexidade (opcional, aqui apenas verifica se tem um tamanho mínimo)
            if (newPass.length() < 6) {
                FXUtils.showErrorMessage("Erro de Validação", "A nova senha deve ter pelo menos 6 caracteres.");
                return;
            }

            // Lógica de Atualização
            performPasswordUpdate(currentPass, newPass);
            mainStackPane.getChildren().remove(confirmationModal);
        });
    }

    private StackPane createPasswordField(String promptText){
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.getStyleClass().add("formInput");
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.getStyleClass().add("formInput");

        passwordField.setMinHeight(37);
        textField.textProperty().bindBidirectional(passwordField.textProperty());

        passwordField.textProperty().addListener((oldVal, newVal, obs) -> {
            if(passwordField.getText().isBlank()){
                passwordField.setStyle("-fx-text-fill: #4B0081;");
            } else {
                passwordField.setStyle("-fx-font-size: 8; -fx-text-fill: #4B0081;");
            }
        });

        Button showPasswordButton = new Button();
        showPasswordButton.setOnMouseClicked(e -> switchPasswordVisibility(passwordField, textField, showPasswordButton));
        showPasswordButton.setStyle("-fx-background-color: transparent;");

        switchPasswordVisibility(passwordField, textField, showPasswordButton);
        StackPane stackPane = new StackPane(passwordField, textField, showPasswordButton);
        stackPane.setAlignment(Pos.CENTER_RIGHT);
        return stackPane;
    }

    private void switchPasswordVisibility(PasswordField passwordField, TextField passwordTextField, Button showPasswordButton){
        passwordField.setManaged(passwordTextField.isManaged());
        passwordField.setVisible(passwordTextField.isVisible());
        passwordTextField.setManaged(!passwordTextField.isManaged());
        passwordTextField.setVisible(!passwordField.isVisible());

        boolean passwordVisible = passwordTextField.isVisible();
        String imagePath = "/com/dottec/pdi/project/pdi/static/img/Eye.png";
        if(passwordVisible) imagePath = "/com/dottec/pdi/project/pdi/static/img/EyeOff.png";
        ImageView iv = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        iv.setFitHeight(25);
        iv.setFitWidth(25);
        showPasswordButton.setGraphic(iv);
    }

    // NOVO MÉTODO: Executa a atualização da senha no banco de dados
    private void performPasswordUpdate(String currentPassword, String newPassword) {
        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser == null) {
            FXUtils.showErrorMessage("Erro", "Usuário não autenticado.");
            return;
        }

        // 1. Verificar a senha atual
        String storedHash = loggedUser.getPasswordHash();

        // CORREÇÃO: Utilizar PasswordUtil para verificar a senha (se o projeto utiliza jbcrypt)
        // Se o projeto usa a implementação do PasswordHasher para BCrypt:
        if (!PasswordHasher.verify(currentPassword, storedHash)) {
            // Se o projeto usa a implementação do LoginViewModel original para simulação de hashcode (NÃO RECOMENDADO, mas presente em alguns arquivos):
            // if (currentPassword.hashCode() != storedHash.hashCode()) {
            FXUtils.showErrorMessage("Erro de Segurança", "A senha atual está incorreta.");
            return;
        }

        // 2. Criar o novo hash da senha
        String newPasswordHash = PasswordHasher.hash(newPassword);

        // 3. Atualizar no banco
        try {
            UserDAO.updatePassword(loggedUser.getId(), newPasswordHash);

            // 4. Atualizar o objeto do usuário logado em memória
            loggedUser.setPasswordHash(newPasswordHash);

            FXUtils.showSuccessMessage("Sucesso", "Senha alterada com sucesso!");
        } catch (Exception e) {
            FXUtils.showErrorMessage("Erro no Banco de Dados", "Falha ao atualizar a senha: " + e.getMessage());
        }
    }
}
