package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.model.Collaborator;
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
        mainScrollPane.setFitToHeight(true);
        mainScrollPane.setFitToWidth(true);
        loadAndDisplayCollaborators();
    }

    private void loadAndDisplayCollaborators() {
        List<Collaborator> collaborators = CollaboratorController.findAllCollaborators();
        listCollaborators(collaborators);
    }

    public void listCollaborators(List<Collaborator> collaborators) {
        mainVBox.getChildren().clear();

        collaborators.forEach(collaborator -> {
            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().add("stackpane-collaborator");
            stackPane.setId(String.valueOf(collaborator.getId()));
            stackPane.setOnMouseClicked(event -> openCollaboratorPage(collaborator));

            Label name = new Label(collaborator.getName());
            name.getStyleClass().add("label-collaborator-name");

            String departmentName = "Sem setor definido";
            if (collaborator.getDepartment() != null && collaborator.getDepartment().getName() != null) {
                departmentName = collaborator.getDepartment().getName();
            }
            Label department = new Label(departmentName);
            department.getStyleClass().add("label-collaborator-department");

            Label status = new Label();
            status.getStyleClass().add("label-collaborator-status");

            // Usa um switch para definir texto e estilo, facilitando a adição de novos status
            switch (collaborator.getStatus()) {
                case active:
                    status.setText("Ativo");
                    status.setStyle("-fx-background-color: #AF69CD; -fx-text-fill: white;");
                    break;
                case inactive:
                    status.setText("Inativo");
                    status.setStyle("-fx-background-color: #E6CCEF; -fx-text-fill: #5c5c5c;");
                    break;
                case on_leave:
                    status.setText("Afastado");
                    status.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #5c5c5c;");
                    break;
                default:
                    status.setText("Desconhecido");
                    status.setStyle("-fx-background-color: #grey; -fx-text-fill: white;");
                    break;
            }

            stackPane.getChildren().addAll(name, department, status);
            StackPane.setAlignment(name, Pos.CENTER_LEFT);
            StackPane.setAlignment(department, Pos.TOP_LEFT);
            StackPane.setAlignment(status, Pos.CENTER_RIGHT);


            VBox.setMargin(stackPane, new Insets(0, 0, 10, 0));


            mainVBox.getChildren().add(stackPane);
        });
    }

    private void openCollaboratorPage(Collaborator collaborator) {
        System.out.println("Navegando para a página do colaborador: " + collaborator.getName());
        /*
        TemplateViewModel.switchScreen("Goals.fxml", controller -> {
            if (controller instanceof CollaboratorGoalsViewModel c) {
                c.setCollaborator(collaborator);
            }
        });
        */
    }
}