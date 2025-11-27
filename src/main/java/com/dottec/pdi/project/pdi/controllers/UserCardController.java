package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.util.function.Consumer;

public class UserCardController {

    // FXML fields (corretos)
    @FXML private Label nameField;
    @FXML private Label emailField;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private User user; // Objeto de dados
    private Consumer<User> onEditCallback;
    private Consumer<User> onDeleteCallback;

    public void setData(User user, Consumer<User> onEdit, Consumer<User> onDelete) {
        this.user = user;
        this.onEditCallback = onEdit;
        this.onDeleteCallback = onDelete;

        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
    }


    @FXML
    public void handleEditAction(ActionEvent event) {
        if (onEditCallback != null) {
            onEditCallback.accept(this.user);
        }
    }

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        if (onDeleteCallback != null) {
            onDeleteCallback.accept(this.user);
        }
    }
}