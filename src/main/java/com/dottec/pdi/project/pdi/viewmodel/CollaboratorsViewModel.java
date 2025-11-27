package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.AuthController;
import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.User;
import javafx.application.Platform;
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
import java.util.stream.Collectors;

import animatefx.animation.*;

import javafx.scene.control.ProgressIndicator;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.concurrent.Task;

import static java.util.Collections.sort;

public class CollaboratorsViewModel {
    @FXML private VBox mainVBox;

    List<Collaborator> allCollaborators = new ArrayList<>(); // Lista completa para pesquisa
    Set<Department> departments = new TreeSet<>(Comparator.comparing(Department::getName));

    private List<Integer> filteredDepartments = new ArrayList<>();
    private List<CollaboratorStatus> filteredStatuses = new ArrayList<>(Arrays.asList(CollaboratorStatus.values()));
    private String searchText = ""; // Novo campo para o texto de pesquisa

    @FXML
    public void initialize() {
        User user = AuthController.getInstance().getLoggedUser();

        // 1. Carregar a lista inicial de colaboradores
        if(user.getRole().equals(Role.general_manager) || user.getRole().equals(Role.hr_manager)){
            allCollaborators = CollaboratorController.findAllCollaborators();
            allCollaborators.forEach(collaborator -> {
                if (collaborator.getDepartment() != null) {
                    departments.add(collaborator.getDepartment());
                }
            });
            setFilterMenu(true);
        } else {
            // Gerente de Departamento - filtra apenas o seu departamento
            allCollaborators = CollaboratorController.findByDepartmentId(user.getDepartment().getId());
            if (user.getDepartment() != null) {
                departments.add(user.getDepartment());
                filteredDepartments.add(user.getDepartment().getId());
            }
            setFilterMenu(false); // Não mostra filtro de departamento para gerentes de setor
        }

        // 2. Adicionar listener para a barra de pesquisa
        Platform.runLater(() -> {
            HeaderViewModel.getController().getSearchText().addListener((obs, oldV, newV) -> {
                searchText = newV.trim().toLowerCase();
                listCollaborators(); // Recarrega a lista toda vez que o texto mudar
            });
        });

        // 3. Exibir a lista inicial
        listCollaborators();
    }

    private void setFilterMenu(boolean filterByDepartment){
        FilterMenuViewModel filterMenu = new FilterMenuViewModel();

        // --- Filtro por Status ---
        List<Node> statusList = new ArrayList<>();
        // Limpa e repopula a lista de status no filtro para refletir o estado inicial
        filteredStatuses.clear();
        Arrays.asList(CollaboratorStatus.values()).forEach(s -> filteredStatuses.add(s));

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

        filterMenu.addFilterField("Filtrar por status", statusList);

        // --- Filtro por Departamento (Apenas para Gerente Geral/RH) ---
        if(filterByDepartment) {
            List<Node> departmentList = new ArrayList<>();
            // Limpa e repopula a lista de departamentos no filtro para refletir o estado inicial
            filteredDepartments.clear();
            departments.forEach(department -> filteredDepartments.add(department.getId()));

            departments.stream()
                    .sorted(Comparator.comparing(Department::getName))
                    .forEach(department -> {
                        CheckBox checkBox = new CheckBox(department.getName());
                        checkBox.setSelected(true);
                        checkBox.setOnAction(e -> {
                            int departmentId = department.getId();
                            if (checkBox.isSelected()) {
                                if (!filteredDepartments.contains(departmentId)) {
                                    filteredDepartments.add(departmentId);
                                }
                            } else {
                                filteredDepartments.remove(Integer.valueOf(departmentId));
                            }
                        });
                        departmentList.add(checkBox);
                    });
            filterMenu.addFilterField("Filtrar por setor", departmentList);
        }

        filterMenu.getConfirmFilterButton().setOnMouseClicked(e -> listCollaborators());

        Button filterButton = HeaderViewModel.getController().getFilterButton();
        filterButton.setOnMouseClicked(e -> filterMenu.show(filterButton));

    }

    public void listCollaborators() {
        // Limpar antes de adicionar novos elementos
        mainVBox.getChildren().clear();

        List<Collaborator> filteredCollaborators = allCollaborators.stream()
                // 1. Filtro por Status e Departamento
                .filter(collaborator -> {
                    boolean statusOk = filteredStatuses.contains(collaborator.getStatus());

                    boolean departmentOk = false;
                    if(collaborator.getDepartment() != null) {
                        departmentOk = filteredDepartments.contains(collaborator.getDepartment().getId());
                    } else {
                        // Trata colaboradores sem departamento (que geralmente teriam department_id=0 ou NULL)
                        // Como filteredDepartments só tem IDs > 0, colaboradores sem departamento serão excluídos, o que é o comportamento esperado.
                        // Se quisermos incluir colaboradores sem departamento (se tiverem sido selecionados),
                        // precisaríamos tratar IDs nulos/zero no filtro. Por agora, assume-se que todos têm departamento.
                        // Se o usuário logado for dept manager, ele não terá filtro de depto, então o filteredDepartments
                        // terá apenas o ID do departamento dele.
                        if(!filteredDepartments.isEmpty()) { // Se o filtro está ativo (General/HR Manager)
                            departmentOk = false;
                        } else { // Se não há filtro de departamento (Dept Manager)
                            departmentOk = true; // Permite todos, pois o 'allCollaborators' já está filtrado.
                        }
                    }

                    // Para o caso do Dept Manager, filteredDepartments terá apenas 1 ID e departments.size() será 1.
                    // O filtro de departamento é redundante se allCollaborators já estiver filtrado,
                    // mas a lógica genérica é mantida aqui.
                    if (departments.size() == 1 && filteredDepartments.contains(departments.iterator().next().getId())) {
                        departmentOk = true; // Desabilita o filtro de departamento para Dept Manager.
                    }

                    return statusOk && departmentOk;
                })
                // 2. Filtro por Busca de Texto (Nome ou CPF/Email se fossem visíveis)
                .filter(collaborator -> {
                    if (searchText.isEmpty()) return true;

                    String name = collaborator.getName().toLowerCase();
                    String cpf = collaborator.getCpf() != null ? collaborator.getCpf().toLowerCase() : "";
                    String email = collaborator.getEmail() != null ? collaborator.getEmail().toLowerCase() : "";

                    return name.contains(searchText) || cpf.contains(searchText) || email.contains(searchText);
                })
                .collect(Collectors.toList());


        filteredCollaborators.sort(Comparator.comparing(Collaborator::getStatus));

        if(filteredCollaborators.isEmpty()){
            Label label = new Label("Sem resultados a serem exibidos.");
            label.getStyleClass().add("mid-label");
            mainVBox.getChildren().add(label);
            return;
        }

        filteredCollaborators.forEach(collaborator -> {
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