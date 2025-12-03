// main/java/com/dottec/pdi/project/pdi/viewmodel/ManagementHubViewModel.java
package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.AuthController;
import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ManagementHubViewModel {

    @FXML
    private VBox sectorManagementCard;

    @FXML
    private VBox userManagementCard;

    @FXML
    private Label restrictionLabel;

    private User loggedUser;

    @FXML
    public void initialize() {
        loggedUser = AuthController.getInstance().getLoggedUser();

        // 1. Aplica restrição ao Card de Setores (Apenas RH)
        if (loggedUser == null || loggedUser.getRole() != Role.hr_manager) {
            sectorManagementCard.getStyleClass().add("disabled-access");
            sectorManagementCard.setDisable(true);
            restrictionLabel.setVisible(true);
            restrictionLabel.setText("O Gerenciamento de Setores é restrito a Gerentes de RH.");
        }

        // 2. Aplica restrição ao Card de Usuários (RH e Geral)
        if (loggedUser == null || (loggedUser.getRole() != Role.hr_manager && loggedUser.getRole() != Role.general_manager)) {
            userManagementCard.getStyleClass().add("disabled-access");
            userManagementCard.setDisable(true);
            if (!restrictionLabel.isVisible()) {
                restrictionLabel.setVisible(true);
                restrictionLabel.setText("Apenas Gerentes de RH e Gerais podem acessar a gestão de Usuários.");
            }
        }
    }

    @FXML
    private void goToSectorManagement(MouseEvent event) {
        if (sectorManagementCard.isDisable()) return;
        TemplateViewModel.switchScreen("Settings.fxml");
        HeaderViewModel.updateHeader("Settings.fxml");
    }

    @FXML
    private void goToUserManagement(MouseEvent event) {
        if (userManagementCard.isDisable()) return;
        TemplateViewModel.switchScreen("UserManagement.fxml");
        HeaderViewModel.updateHeader("UserManagement.fxml");
    }
}