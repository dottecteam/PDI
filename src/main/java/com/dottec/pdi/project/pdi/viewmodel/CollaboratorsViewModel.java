package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.model.Collaborator;
import javafx.fxml.FXML;
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


    CollaboratorController collaboratorController = new CollaboratorController();
    private List<Collaborator> collaborators = collaboratorController.findAllCollaborators();


    public void initialize() {
        mainScrollPane.setFitToHeight(true);
        mainScrollPane.setFitToWidth(true);

        listCollaborators((collaborators));

    }

    public void listCollaborators(List<Collaborator> collaborators){
        collaborators.forEach(collaborator -> {
            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().add("stackpane-collaborator");
            stackPane.setId(String.valueOf(collaborator.getId()));
            stackPane.setOnMouseClicked(event -> openCollaboratorPage(collaborator));

            Label name = new Label(collaborator.getName());
            name.getStyleClass().add("label-collaborator-name");

            Label department = new Label(String.valueOf(collaborator.getDepartment()));
            department.getStyleClass().add("label-collaborator-department");

            Label status = new Label();
            if(collaborator.getStatus() == CollaboratorStatus.active){
                status.setText("Ativo");
                status.setStyle("-fx-background-color: #AF69CD; -fx-padding: 2 12");
            } else if(collaborator.getStatus() == CollaboratorStatus.inactive) {
                status.setText("Inativo");
                status.setStyle("-fx-background-color: #E6CCEF");
            }
            status.getStyleClass().add("label-collaborator-status");

            stackPane.getChildren().addAll(name, department, status);
            stackPane.setAlignment(name, Pos.BOTTOM_LEFT);
            stackPane.setAlignment(department, Pos.TOP_LEFT);
            stackPane.setAlignment(status, Pos.BOTTOM_RIGHT);

            mainVBox.getChildren().add(stackPane);
        });
    }

    private void openCollaboratorPage(Collaborator collaborator){
        TemplateViewModel.trocarDeTela("Goals.fxml", controller -> {
            if (controller instanceof CollaboratorGoalsViewModel c) {
                c.setCollaborator(collaborator);
            }
        });
    }


}
