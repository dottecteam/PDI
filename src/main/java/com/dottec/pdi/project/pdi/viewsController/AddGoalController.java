package com.dottec.pdi.project.pdi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;

// Enum para representar os diferentes status possíveis
enum GoalStatus {
    EM_CURSO,
    CONCLUIDO,
    ADICIONAR
}

// Classe para modelar cada item da lista
class Goal {
    private String title;
    private GoalStatus status;

    public Goal(String title, GoalStatus status) {
        this.title = title;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public GoalStatus getStatus() {
        return status;
    }
}

public class AddGoalController {


        @FXML
        private ListView<Goal> goalsListView;

        @FXML
        public void initialize() {
            // 1. Crie a lista de dados (metas) : ESSA PARTE VAI MUDAR COM A CRIAÇÃO DO BANCO DE DADOS
            ObservableList<Goal> goals = FXCollections.observableArrayList(
                    new Goal("Onboarding para novos colaboradores", GoalStatus.EM_CURSO),
                    new Goal("Comunicação e oratória", GoalStatus.EM_CURSO),
                    new Goal("Gestão para resultados", GoalStatus.ADICIONAR),
                    new Goal("Planejamento estratégico", GoalStatus.CONCLUIDO),
                    new Goal("Gestão do tempo", GoalStatus.EM_CURSO),
                    new Goal("Segurança do trabalho", GoalStatus.EM_CURSO),
                    new Goal("Processo de gestão da qualidade", GoalStatus.ADICIONAR)
            );

            // 2. Associe os dados à ListView
            goalsListView.setItems(goals);

            // 3. Crie a fábrica de células customizadas
            goalsListView.setCellFactory(new Callback<ListView<Goal>, ListCell<Goal>>() {
                @Override
                public ListCell<Goal> call(ListView<Goal> listView) {
                    return new GoalCell();
                }
            });
        }

        // Classe interna para representar a célula customizada
        private static class GoalCell extends ListCell<Goal> {
            private HBox hbox = new HBox();
            private Label label = new Label();
            private Region spacer = new Region(); // Espaçador flexível
            private Button addButton = new Button("Adicionar");
            private Label statusLabel = new Label();

            public GoalCell() {
                super();
                HBox.setHgrow(spacer, Priority.ALWAYS); // Faz o espaçador crescer
                addButton.getStyleClass().add("add-button"); // Classe CSS para o botão

                // Lógica do botão (exemplo)
                addButton.setOnAction(event -> {
                    System.out.println("Adicionando: " + getItem().getTitle());
                    // Aqui você colocaria a lógica para adicionar a meta
                });
            }

            @Override
            protected void updateItem(Goal goal, boolean empty) {
                super.updateItem(goal, empty);
                if (empty || goal == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    label.setText(goal.getTitle());
                    label.setStyle("-fx-font-size: 16px;");

                    // Limpa o HBox e o reconfigura para o item atual
                    hbox.getChildren().clear();
                    hbox.setAlignment(Pos.CENTER);

                    hbox.getChildren().add(label);
                    hbox.getChildren().add(spacer);

                    // Decide qual componente de status mostrar
                    switch (goal.getStatus()) {
                        case ADICIONAR:
                            hbox.getChildren().add(addButton);
                            break;
                        case EM_CURSO:
                            statusLabel.setText("📖 Em curso");
                            statusLabel.getStyleClass().setAll("status-label", "em-curso");
                            hbox.getChildren().add(statusLabel);
                            break;
                        case CONCLUIDO:
                            statusLabel.setText("✔ Concluído");
                            statusLabel.getStyleClass().setAll("status-label", "concluido");
                            hbox.getChildren().add(statusLabel);
                            break;
                    }
                    setGraphic(hbox);
                }
            }
        }
}
