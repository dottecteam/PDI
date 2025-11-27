package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

import java.util.function.Consumer;

public class UserCardController {

    // FXML fields (corretos)
    @FXML
    private Label nameField;
    @FXML
    private Label emailField;
    @FXML
    private Label roleLabel; // NOVO: Adicionado para o Cargo
    @FXML
    private Label statusLabel; // NOVO: Adicionado para o Status
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    private User user; // Objeto de dados
    private Consumer<User> onEditCallback;
    private Consumer<User> onDeleteCallback;

    public void setData(User user, Consumer<User> onEdit, Consumer<User> onDelete) {
        this.user = user;
        this.onEditCallback = onEdit;
        this.onDeleteCallback = onDelete;

        nameField.setText(user.getName());
        emailField.setText(user.getEmail());

        // NOVO: Adiciona a Role (Cargo) e o Departamento para Gerentes
        String cargo = switch (user.getRole().name()) {
            case "department_manager" -> {
                if (user.getDepartment() != null && user.getDepartment().getName() != null) {
                    // Exibe o nome do departamento: "Gerente: Desenvolvimento"
                    yield "Gerente: " + user.getDepartment().getName();
                } else {
                    // Caso o Gerente de Dept. não tenha um setor vinculado
                    yield "Gerente Dept. (Sem setor)";
                }
            }
            case "hr_manager" -> "Gerente RH";
            case "general_manager" -> "Gerente Geral";
            default -> user.getRole().name(); // Fallback para outros cargos
        };
        roleLabel.setText(cargo);

        // NOVO: Adiciona o Status e o estilo
        String statusText = user.getStatus().name().equals("active") ? "ATIVO" : "INATIVO";
        statusLabel.setText(statusText);
        statusLabel.setStyle(user.getStatus().name().equals("active") ?
                "-fx-background-color: #AF69CD; -fx-text-fill: white;" :
                "-fx-background-color: #DDDDDD; -fx-text-fill: #555555;");
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