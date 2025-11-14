package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.controllers.DepartmentController;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.utils.FXUtils;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import animatefx.animation.*;

import javafx.scene.control.ProgressIndicator;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.concurrent.Task;

import static java.util.Collections.sort;


public class CollaboratorsViewModel {
    @FXML private VBox mainVBox;

    @FXML
    public void initialize() {
        loadAndDisplayCollaborators();

        this.header = HeaderViewModel.getController();

        //Configura o listener para a barra de pesquisa
        this.setupSearchListener();

        loadAndDisplayCollaborators();

        setFilterMenu();
    }

    private void setFilterMenu(){
        //Status based filter
        List<Node> statusList = new ArrayList<>();
        for(CollaboratorStatus status : CollaboratorStatus.values()){
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(true);
            switch (status.name()) {
                case "active" -> checkBox.setText("Ativo");
                case "on_leave"-> checkBox.setText("Afastado");
                case "inactive" -> checkBox.setText("Inativo");
            }
            statusList.add(checkBox);
        }

        //Department based filter
        List<Node> departmentList = new ArrayList<>();
        List<Department> departments = DepartmentController.findAllDepartments();
        departments.sort(Comparator.comparing(Department::getName));
        departments.forEach(department -> {
            CheckBox checkBox = new CheckBox(department.getName());
            departmentList.add(checkBox);
        });

        FilterMenuViewModel filterMenu = new FilterMenuViewModel();
        filterMenu.getConfirmFilterButton().setOnMouseClicked(e -> handleFilter());
        filterMenu.addFilterField("Filtrar por status", statusList);
        filterMenu.addFilterField("Filtrar por setor", departmentList);

        Button filterButton = HeaderViewModel.getController().getFilterButton();

        filterButton.setOnMouseClicked(e -> filterMenu.show(filterButton));

    }

    private void handleFilter(){
        //TODO colocar aqui a função de filtragem
    }

    private void setupSearchListener() {
        // Adiciona o listener na propriedade de texto exposta pelo Header
        header.getSearchText().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> performSearch(newValue));
            pause.playFromStart();
        });
    }

    private void performSearch(String query) {
        //Se a busca estiver vazia, carrega todos os colaboradores
        if (query == null || query.trim().isEmpty()) {
            loadAndDisplayCollaborators();
            return;
        }

        mainVBox.getChildren().clear();
        ProgressIndicator pi = new ProgressIndicator();
        mainVBox.getChildren().add(pi);
        mainVBox.setAlignment(Pos.CENTER);


        Task<List<Collaborator>> searchTask = new Task<>() {
            @Override
            protected List<Collaborator> call() throws Exception {
                return CollaboratorController.searchCollaborators(query);
            }
        };

        searchTask.setOnSucceeded(e -> {
            List<Collaborator> results = searchTask.getValue();
            listCollaborators(results);
        });

        searchTask.setOnFailed(e -> {
            // Lidar com erros
            mainVBox.getChildren().clear();
            mainVBox.getChildren().add(new Label("Erro ao buscar dados."));
            mainVBox.setAlignment(Pos.CENTER);
            searchTask.getException().printStackTrace();
        });

        new Thread(searchTask).start();
    }

    private final PauseTransition pause = new PauseTransition(Duration.millis(350));
    private HeaderViewModel header;

    private void loadAndDisplayCollaborators() {
        // Usando o Controller estático (padrão que definimos)
        List<Collaborator> collaborators = CollaboratorController.findAllCollaborators();
        listCollaborators(collaborators);
    }

    public void listCollaborators(List<Collaborator> collaborators) {
        // Limpar antes de adicionar novos elementos
        mainVBox.getChildren().clear();

        collaborators.sort(Comparator.comparing(Collaborator::getStatus));
        collaborators.forEach(collaborator -> {
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