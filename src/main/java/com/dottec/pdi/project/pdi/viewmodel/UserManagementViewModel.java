// main/java/com/dottec/pdi/project/pdi/viewmodel/UserManagementViewModel.java
package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.UserController;
import com.dottec.pdi.project.pdi.controllers.UserCardController;
import com.dottec.pdi.project.pdi.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserManagementViewModel {
    @FXML private VBox mainField;

    @FXML void initialize() {
        Platform.runLater(this::loadUsers);
    }

    public void loadUsers() {
        mainField.getChildren().clear();

        List<User> users = UserController.findAll();

        if (users.isEmpty()) {
            mainField.getChildren().add(new Label("Nenhum usuário cadastrado."));
            return;
        }

        for (User user : users) {
            renderUserCard(user);
        }
    }

    private void renderUserCard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dottec/pdi/project/pdi/views/UserCardTemplate.fxml"));
            StackPane userCard = loader.load();

            UserCardController cardController = loader.getController();

            cardController.setData(
                    user,
                    this::handleEditAction,
                    this::handleDeleteAction
            );

            mainField.getChildren().add(userCard);

        } catch (IOException e) {
            e.printStackTrace();
            TemplateViewModel.showErrorMessage("Erro de UI", "Falha ao carregar o card de usuário.");
        }
    }

    private void handleEditAction(User userToEdit) {
        TemplateViewModel.switchScreen("UserForm.fxml", controller -> {
            if (controller instanceof UserFormViewModel formController) {
                formController.setUserToEdit(userToEdit);
            }
        });
        HeaderViewModel.updateHeader("UserForm.fxml");
    }

    private void handleDeleteAction(User userToDelete) {
        String statusMsg = userToDelete.getStatus().name().equals("active") ? "inativar" : "reativar";
        String contentMsg = userToDelete.getStatus().name().equals("active")
                ? "Tem certeza que deseja INATIVAR o usuário " + userToDelete.getName() + "?"
                : "Tem certeza que deseja REATIVAR o usuário " + userToDelete.getName() + "?";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, contentMsg);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = false;

            if (userToDelete.getStatus().name().equals("active")) {
                success = UserController.inactivateUser(userToDelete.getId()); // Inativa (soft delete)
            } else {
                // Reativa o usuário
                userToDelete.setStatus(com.dottec.pdi.project.pdi.enums.UserStatus.active);
                success = UserController.updateUser(userToDelete);
            }

            if (success) {
                TemplateViewModel.showSuccessMessage("Sucesso", "Usuário " + statusMsg + " com sucesso.");
                loadUsers(); // Recarrega a lista
            } else {
                TemplateViewModel.showErrorMessage("Erro", "Falha ao " + statusMsg + " o usuário.");
            }
        }
    }
}