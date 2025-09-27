package com.dottec.pdi.project.pdi;

import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.enums.Status;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

public class CollaboratorsController {
    @FXML
    private VBox mainVBox;
    @FXML
    private ScrollPane mainScrollPane;

    private List<Collaborator> collaborators = Arrays.asList(
            new Collaborator(1, "Marcelino", "marcelino@outlook.com", "12345678910", 2, 2, "Sem experiência", "Sem observações", Status.active),
            new Collaborator(2, "Fernanda", "fernanda.silva@gmail.com", "98765432100", 3, 3, "3 anos em Marketing", "Boa comunicação", Status.active),
            new Collaborator(3, "Rafael", "rafael.santos@yahoo.com", "45678912355", 1, 1, "5 anos em Desenvolvimento", "Especialista em Java", Status.inactive),
            new Collaborator(4, "Juliana", "juliana.mendes@empresa.com", "32165498777", 4, 4, "1 ano em RH", "Organizada e dedicada", Status.active),
            new Collaborator(5, "Carlos", "carlos.pereira@hotmail.com", "74185296311", 5, 5, "10 anos em Gestão", "Ex-gerente de projetos", Status.inactive),
            new Collaborator(6, "Amanda", "amanda.rocha@empresa.com", "85296374122", 2, 2, "2 anos em Suporte Técnico", "Atendimento ao cliente", Status.active)
    );

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

            Label name = new Label(collaborator.getName());
            name.getStyleClass().add("label-collaborator-name");

            Label department = new Label(String.valueOf(collaborator.getDepartment()));
            department.getStyleClass().add("label-collaborator-department");

            Label status = new Label();
            if(collaborator.getStatus() == Status.active){
                status.setText("Ativo");
                status.setStyle("-fx-background-color: #AF69CD; -fx-padding: 2 12");
            } else if(collaborator.getStatus() == Status.inactive) {
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
}
