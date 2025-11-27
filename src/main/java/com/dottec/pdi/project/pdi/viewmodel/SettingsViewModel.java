package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.dao.DepartmentDAO;
import com.dottec.pdi.project.pdi.model.Department;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SettingsViewModel implements Initializable {

    @FXML
    private VBox vboxSectorsList;

    @FXML
    private Button btnAddSector;

    private final String IMG_PATH = "/com/dottec/pdi/project/pdi/static/img/";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDepartments();

        if (btnAddSector != null) {
            btnAddSector.setOnAction(event -> {
                TemplateViewModel.switchScreen("AddSector.fxml");
            });
        }
    }

    public void loadDepartments() {
        try {
            vboxSectorsList.getChildren().clear();
            List<Department> departments = DepartmentDAO.readAll();

            if (departments.isEmpty()) {
                Label lblEmpty = new Label("Nenhum setor encontrado.");
                lblEmpty.getStyleClass().add("label-sector-name");
                lblEmpty.setStyle("-fx-text-fill: gray;");
                vboxSectorsList.getChildren().add(lblEmpty);
            } else {
                for (Department dept : departments) {
                    HBox card = createDepartmentCard(dept);
                    vboxSectorsList.getChildren().add(card);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            TemplateViewModel.showErrorMessage("Erro ao carregar setores", e.getMessage());
        }
    }

    private HBox createDepartmentCard(Department department) {
        // 1. Container Principal do Card
        HBox card = new HBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(20);
        card.getStyleClass().add("hbox-sector");

        // 2. Imagem do Hexágono
        ImageView imgHexagon = carregarImagem("hexagon.png", 50, 50);

        // 3. Label do Nome
        Label lblNome = new Label(department.getName());
        lblNome.getStyleClass().add("label-sector-name");

        // 4. Espaçador (empurra botões para direita)
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 5. Botões de Ação
        HBox boxBotoes = new HBox(15);
        boxBotoes.setAlignment(Pos.CENTER);

        // --- REMOVIDO: Botão Editar ---
        // Button btnEdit = criarBotaoIcone("edit.png");
        // btnEdit.setOnAction(e -> abrirModalEdicao(department));

        // Botão Excluir (Mantido)
        Button btnDelete = criarBotaoIcone("trash.png");
        btnDelete.setOnAction(e -> deletarDepartamento(department));

        // Adiciona APENAS o botão Delete
        boxBotoes.getChildren().add(btnDelete);

        card.getChildren().addAll(imgHexagon, lblNome, spacer, boxBotoes);

        return card;
    }

    private void deletarDepartamento(Department department) {
        try {
            DepartmentDAO.deleteById(department.getId());
            TemplateViewModel.showSuccessMessage("Setor removido com sucesso!");
            loadDepartments();
        } catch (Exception e) {
            TemplateViewModel.showErrorMessage("Erro ao excluir", e.getMessage());
        }
    }

    // --- REMOVIDO: Método abrirModalEdicao não é mais necessário ---

    private Button criarBotaoIcone(String nomeImagem) {
        Button btn = new Button();
        btn.getStyleClass().add("button-icon");

        ImageView icon = carregarImagem(nomeImagem, 24, 24);
        if (icon != null) {
            btn.setGraphic(icon);
        } else {
            btn.setText("X");
        }
        return btn;
    }

    private ImageView carregarImagem(String nomeArquivo, double w, double h) {
        try {
            String fullPath = IMG_PATH + nomeArquivo;
            URL imgUrl = getClass().getResource(fullPath);

            if (imgUrl != null) {
                ImageView iv = new ImageView(new Image(imgUrl.toExternalForm()));
                iv.setFitWidth(w);
                iv.setFitHeight(h);
                iv.setPreserveRatio(true);
                return iv;
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem: " + nomeArquivo);
        }
        return null;
    }
}