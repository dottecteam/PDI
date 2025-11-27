package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.model.Collaborator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

public class UserManagementViewModel {
    @FXML private VBox mainField;

    @FXML private Label nameField;
    @FXML private Label emailField;
    @FXML private ImageView userPhoto;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Pane userCard;

    @FXML void initialize() {
        assert btnDelete != null : "fx:id="btnDelete" was not injected: check your FXML file 'UserManagement.fxml'.";
        assert btnEdit != null : "fx:id="btnEdit" was not injected: check your FXML file 'UserManagement.fxml'.";
        assert emailField != null : "fx:id="emailField" was not injected: check your FXML file 'UserManagement.fxml'.";
        assert nameField != null : "fx:id="nameField" was not injected: check your FXML file 'UserManagement.fxml'.";
        assert userPhoto != null : "fx:id="userPhoto" was not injected: check your FXML file 'UserManagement.fxml'.";

    }

}