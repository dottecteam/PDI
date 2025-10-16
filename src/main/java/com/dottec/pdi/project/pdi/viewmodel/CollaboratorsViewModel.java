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

            // --- 2. Label Departamento (Lógica de Nulo Corrigida) ---
            String departmentName = "Sem setor definido";
            if (collaborator.getDepartment() != null && collaborator.getDepartment().getName() != null) {
                departmentName = collaborator.getDepartment().getName();
            }
            Label department = new Label(departmentName);
            department.getStyleClass().add("label-collaborator-department");

            // --- 3. Label Status (Switch Case Completo e Estilos) ---
            Label status = new Label();
            status.getStyleClass().add("label-collaborator-status");

            // Usa o switch case robusto da sua primeira versão e adiciona o padding
            switch (collaborator.getStatus()) {
                case active:
                    status.setText("Ativo");
                    // Estilo embutido para cor, padding deve vir do CSS ou ser fixo aqui:
                    status.setStyle("-fx-background-color: #AF69CD; -fx-text-fill: white; -fx-padding: 2 12;");
                    break;
                case inactive:
                    status.setText("Inativo");
                    status.setStyle("-fx-background-color: #E6CCEF; -fx-text-fill: #5c5c5c; -fx-padding: 2 12;");
                    break;
                case on_leave: // Status 'Afastado' recuperado
                    status.setText("Afastado");
                    status.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #5c5c5c; -fx-padding: 2 12;");
                    break;
                default:
                    status.setText("Desconhecido");
                    status.setStyle("-fx-background-color: grey; -fx-text-fill: white; -fx-padding: 2 12;");
                    break;
            }

            // --- 4. Adiciona e Alinha no StackPane ---
            stackPane.getChildren().addAll(name, department, status);

            // Alinhamento corrigido para o padrão mais legível:
            StackPane.setAlignment(name, Pos.CENTER_LEFT);    // Nome no centro-esquerda
            StackPane.setAlignment(department, Pos.TOP_LEFT); // Departamento no topo-esquerda
            StackPane.setAlignment(status, Pos.CENTER_RIGHT); // Status no centro-direita (fica mais limpo)

            // --- 5. Adiciona Margem e ao VBox ---
            VBox.setMargin(stackPane, new Insets(0, 0, 10, 0)); // Recupera a margem de 10px abaixo
            mainVBox.getChildren().add(stackPane);
        });
    }

    private void openCollaboratorPage(Collaborator collaborator) {
        System.out.println("Navegando para a página do colaborador: " + collaborator.getName());

        // Uso o método mais genérico (trocarDeTela) da sua segunda versão
        // Se este método estiver no TemplateViewModel, mantenha assim.
        /*
        TemplateViewModel.trocarDeTela("Goals.fxml", controller -> {
            if (controller instanceof CollaboratorGoalsViewModel c) {
                c.setCollaborator(collaborator);
            }
        });
        */
    }
}