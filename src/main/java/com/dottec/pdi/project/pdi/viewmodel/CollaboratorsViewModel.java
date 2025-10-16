package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class CollaboratorsViewModel {
    @FXML
    private VBox mainVBox;
    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    public void initialize() {
        // Garantindo que a ScrollPane se ajuste ao tamanho
        mainScrollPane.setFitToHeight(true);
        mainScrollPane.setFitToWidth(true);

        loadAndDisplayCollaborators();
    }

    private void loadAndDisplayCollaborators() {
        // Usando o Controller estático (padrão que definimos)
        List<Collaborator> collaborators = CollaboratorController.findAllCollaborators();
        listCollaborators(collaborators);
    }

    public void listCollaborators(List<Collaborator> collaborators) {
        // Limpar antes de adicionar novos elementos
        mainVBox.getChildren().clear();

        collaborators.forEach(collaborator -> {
            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().add("stackpane-collaborator");
            stackPane.setId(String.valueOf(collaborator.getId()));
            stackPane.setOnMouseClicked(event -> openCollaboratorPage(collaborator));

            // --- 1. Label Nome ---
            Label name = new Label(collaborator.getName());
            name.getStyleClass().add("label-collaborator-name");

// --- 2. Label Departamento ---
            String departmentName = "Sem setor definido";
            if (collaborator.getDepartment() != null && collaborator.getDepartment().getName() != null) {
                departmentName = collaborator.getDepartment().getName();
            }
            Label department = new Label(departmentName);
            department.getStyleClass().add("label-collaborator-department");

// --- NOVO: VBox para alinhar nome e departamento verticalmente ---
            VBox textBox = new VBox(5); // ← o número é o "gap" (5px entre os labels)
            textBox.getStyleClass().add("stackpane-inner-collaborator");
            textBox.getChildren().addAll(department, name);
            textBox.setAlignment(Pos.CENTER_LEFT);

// --- 3. Label Status ---
            Label status = new Label();
            status.getStyleClass().add("label-collaborator-status");

            switch (collaborator.getStatus()) {
                case active -> {
                    status.setText("Ativo");
                    status.setStyle("-fx-background-color: #AF69CD; -fx-text-fill: white; -fx-padding: 2 12;");
                }
                case inactive -> {
                    status.setText("Inativo");
                    status.setStyle("-fx-background-color: #E6CCEF; -fx-text-fill: #5c5c5c; -fx-padding: 2 12;");
                }
                case on_leave -> {
                    status.setText("Afastado");
                    status.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #5c5c5c; -fx-padding: 2 12;");
                }
                default -> {
                    status.setText("Desconhecido");
                    status.setStyle("-fx-background-color: grey; -fx-text-fill: white; -fx-padding: 2 12;");
                }
            }

// --- Adiciona ao StackPane ---
            stackPane.getChildren().addAll(textBox, status);

// Alinhamentos
            StackPane.setAlignment(textBox, Pos.CENTER_LEFT);
            StackPane.setAlignment(status, Pos.CENTER_RIGHT);

            // --- 5. Adiciona Margem e ao VBox ---
            VBox.setMargin(stackPane, new Insets(0, 0, 10, 0)); // Recupera a margem de 10px abaixo
            mainVBox.getChildren().add(stackPane);
        });
    }

    private void openCollaboratorPage(Collaborator collaborator) {

        TemplateViewModel.switchScreen("Goals.fxml", controller -> {
            if (controller instanceof CollaboratorGoalsViewModel c) {
                c.setCollaborator(collaborator);
            }
        });
    }
}