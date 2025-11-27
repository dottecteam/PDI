package com.dottec.pdi.project.pdi.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

record User(String name, String email, String photoPath) {}

public class UserManagementController {

    private com.dottec.pdi.project.pdi.model.User user; // Objeto de dados
    private Consumer<com.dottec.pdi.project.pdi.model.User> onEditCallback;
    private Consumer<com.dottec.pdi.project.pdi.model.User> onDeleteCallback;

    @FXML
    private VBox userListContainer;

    public UserManagementController(Consumer<com.dottec.pdi.project.pdi.model.User> onDeleteCallback) {
        this.onDeleteCallback = onDeleteCallback;
    }

    /**
     * O método initialize é chamado automaticamente após todos os
     * elementos FXML terem sido carregados.
     */
    @FXML
    public void initialize() {
        System.out.println("Tela de Gerenciamento de Usuários Inicializada.");
        loadUsers();
    }

    /**
     * 1. Simula o carregamento dos dados dos usuários.
     * 2. Chama a função para renderizar cada item.
     */
    private void loadUsers() {
        // Simulação de dados
        List<User> users = Arrays.asList(
                new User("Alice Smith", "alice@dottec.com", "user.png"),
                new User("Bruno Mendes", "bruno@dottec.com", "user.png"),
                new User("Carla Faria", "carla@dottec.com", "user.png")
        );

        // Renderiza cada usuário na interface
        for (User user : users) {
            renderUserCard(user);
        }
    }

    /**
     * Carrega e configura o FXML do "Card de Usuário" para um usuário específico.
     * @param user O objeto de dados User.
     */
    private void renderUserCard(User user) {
        try {
            // 1. Carrega o FXML do item da lista (seu card)
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("UserCard.fxml")
            );

            // 2. Carrega o nó visual (VBox, Pane, etc.)
            VBox userCardView = loader.load();

            // 3. Obtém o Controller do Card (UserCardController)
            UserCardController cardController = loader.getController();

            // 4. INJEÇÃO DE DADOS: Passa os dados para o controller do item.
            // O UserCardController deve ter um método set/setData.
            cardController.setData(
                    new com.dottec.pdi.project.pdi.model.User(),
                    this::handleEditAction,
                    this::handleDeleteAction
            );

            userListContainer.getChildren().add(userCardView);

        } catch (IOException e) {
            System.err.println("Erro ao carregar o card de usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteAction(com.dottec.pdi.project.pdi.model.User user) {
    }

    private void handleEditAction(com.dottec.pdi.project.pdi.model.User user) {
    }
}