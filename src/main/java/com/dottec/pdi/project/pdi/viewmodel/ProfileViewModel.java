package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.controllers.AuthController;
import com.dottec.pdi.project.pdi.dao.AuthDAO;
import com.dottec.pdi.project.pdi.dao.UserDAO;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.enums.UserStatus;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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


public class ProfileViewModel implements Initializable{
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
        if(user.getStatus() == UserStatus.active){
            updateUserFields();
        } else {
            updateUserFields();
        }
    }

    public String findDepartment(int id){
        String sqlDepartment = "SELECT dep_name FROM departments INNER JOIN users ON departments.dep_id = users.department_id where use_id = ?;";

        String dep = null;
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sqlDepartment)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
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
        switch(userCargo){
            case "department_manager":
                cargo = "Gerente do setor " + findDepartment(AuthController.getInstance().getLoggedUser().getId());
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
        switch(userStatus){
            case "active":
                status = "Ativo";
                labelStatus.getStyleClass().add("status-active");
                break;

            case "inactive":
                status = "Inativo";
                labelStatus.getStyleClass().add("status-inactive");
                break;

            default:
                status = userStatus;
        }
        labelStatus.setText(status);
        labelCargo.setText(cargo);

    }
}
