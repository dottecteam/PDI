package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.AuthController;
import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.*;

import animatefx.animation.*;

import javafx.scene.control.ProgressIndicator;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.concurrent.Task;

import static java.util.Collections.sort;

public class CollaboratorsViewModel {
    @FXML private VBox mainVBox;

    List<Collaborator> collaborators = new ArrayList<>();
    Set<Department> departments = new TreeSet<>(Comparator.comparing(Department::getName));

    private List<Integer> filteredDepartments = new ArrayList<>();
    private List<CollaboratorStatus> filteredStatuses = new ArrayList<>(Arrays.asList(CollaboratorStatus.values()));


    @FXML
    public void initialize() {
        User user = AuthController.getInstance().getLoggedUser();

        if(user.getRole().equals(Role.general_manager) || user.getRole().equals(Role.hr_manager)){
            collaborators = CollaboratorController.findAllCollaborators();
            collaborators.forEach(collaborator -> departments.add(collaborator.getDepartment()));
            setFilterMenu(true);
        } else {
            collaborators = CollaboratorController.findByDepartmentId(user.getDepartment().getId());
            for (Collaborator collaborator : collaborators) {
                System.out.println(collaborator.toString());
            }
            filteredDepartments.add(user.getDepartment().getId());
            setFilterMenu(false);
        }
        listCollaborators();
    }

    private void setFilterMenu(boolean filterByDepartment){
        //Status based filter
        List<Node> statusList = new ArrayList<>();
        for(CollaboratorStatus status : CollaboratorStatus.values()){
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(true);
            checkBox.setOnAction(e -> {
                if(!filteredStatuses.contains(status)) {
                    filteredStatuses.add(status);
                } else {
                    filteredStatuses.remove(status);
                }
            });
            switch (status.name()) {
                case "active" -> checkBox.setText("Ativo");
                case "on_leave"-> checkBox.setText("Afastado");
                case "inactive" -> checkBox.setText("Inativo");
            }
            statusList.add(checkBox);
        }

        FilterMenuViewModel filterMenu = new FilterMenuViewModel();

        filterMenu.getConfirmFilterButton().setOnMouseClicked(e -> listCollaborators());
        filterMenu.addFilterField("Filtrar por status", statusList);

        //Department based filter
        if(filterByDepartment) {
            List<Node> departmentList = new ArrayList<>();
            departments.forEach(department -> filteredDepartments.add(department.getId()));
            departments.forEach(department -> {
                CheckBox checkBox = new CheckBox(department.getName());
                checkBox.setSelected(true);
                checkBox.setOnAction(e -> {
                    int departmentId = department.getId();
                    if (!filteredDepartments.contains(departmentId)) {
                        filteredDepartments.add(Integer.valueOf(departmentId));
                    } else {
                        filteredDepartments.remove(Integer.valueOf(departmentId));
                    }
                });
                departmentList.add(checkBox);
            });
            filterMenu.addFilterField("Filtrar por setor", departmentList);
        }

        Button filterButton = HeaderViewModel.getController().getFilterButton();

        filterButton.setOnMouseClicked(e -> filterMenu.show(filterButton));

    }

    public void listCollaborators() {
        // Limpar antes de adicionar novos elementos
        mainVBox.getChildren().clear();

        collaborators.sort(Comparator.comparing(Collaborator::getStatus));
        collaborators.forEach(collaborator -> {
            boolean statusOk = filteredStatuses.contains(collaborator.getStatus());
            boolean departmentOk = filteredDepartments.contains(collaborator.getDepartment().getId());

            if(statusOk && departmentOk) {
                StackPane stackPane = new StackPane();
                stackPane.getStyleClass().add("stackpane-collaborator");
                stackPane.setId(String.valueOf(collaborator.getId()));
                stackPane.setOnMouseClicked(event -> openCollaboratorPage(collaborator));

            AnchorPane card = new AnchorPane();
            new animatefx.animation.FadeIn(card).play();
            card.setOnMouseEntered(e -> new animatefx.animation.Pulse(card).play());



            // --- 1. Label Nome ---
            Label name = new Label(collaborator.getName());
            name.getStyleClass().add("label-collaborator-name");

                // --- 2. Label Departamento ---
                String departmentName = "Sem setor definido";
                if (collaborator.getDepartment() != null && collaborator.getDepartment().getName() != null) {
                    departmentName = collaborator.getDepartment().getName();
                }
                Label department = new Label(departmentName);
                department.getStyleClass().addAll("label-collaborator-department", "mid-label");

                // --- NOVO: VBox para alinhar nome e departamento verticalmente ---
                VBox textBox = new VBox(5); // ← o número é o "gap" (5px entre os labels)
                textBox.getStyleClass().add("stackpane-inner-collaborator");
                textBox.getChildren().addAll(department, name);
                textBox.setAlignment(Pos.CENTER_LEFT);

                // --- 3. Label Status ---
                Label status = new Label();
                status.getStyleClass().add("label-status");

                switch (collaborator.getStatus()) {
                    case active -> {
                        status.setText("Ativo");
                        status.setStyle("-fx-background-color: #6D00A1; -fx-text-fill: white");
                    }
                    case inactive -> {
                        status.setText("Inativo");
                        status.setStyle("-fx-background-color: #E6CCEF; -fx-text-fill: #5c5c5c");
                    }
                    case on_leave -> {
                        status.setText("Afastado");
                        status.setStyle("-fx-background-color: #AF69CD; -fx-text-fill: #5c5c5c;");
                    }
                    default -> {
                        status.setText("Desconhecido");
                        status.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
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
            }
        });
    }

    private void openCollaboratorPage(Collaborator collaborator) {

        TemplateViewModel.switchScreen("CollaboratorGoals.fxml", controller -> {
            if (controller instanceof CollaboratorGoalsViewModel c) {
                c.setCollaborator(collaborator);
            }
        });
    }
}