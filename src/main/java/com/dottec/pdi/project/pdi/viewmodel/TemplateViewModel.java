package com.dottec.pdi.project.pdi.viewmodel;

import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
import javafx.scene.Node;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.control.TextField;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.Parent;
    import javafx.scene.input.MouseEvent;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.layout.HBox;

import java.io.IOException;
    import java.net.URL;
    import java.util.Arrays;
    import java.util.ResourceBundle;
import java.util.function.Consumer;

public class TemplateViewModel implements Initializable {
        @FXML
        private AnchorPane leftMenu;

        @FXML
        private AnchorPane menuDashboard;

        @FXML
        private AnchorPane menuCollaborators;

        @FXML
        private AnchorPane menuGoalTemplates;

        @FXML
        private AnchorPane menuSettings;

        @FXML
        private AnchorPane menuProfile;

        @FXML
        private AnchorPane AnchorMainPane;

        //Label
        @FXML
        private Label labelArrow;

        @FXML
        private Label labelCollaborator;

        @FXML
        private Label labelSector;

        @FXML
        private Label labelSettings;

        @FXML
        private Label labelProfile;

        @FXML
        private Label headerLabel;


        @FXML
        private BorderPane mainPane;

        @FXML
        private BorderPane tmpCenter;

        @FXML
        private ImageView menuLogo;

        @FXML
        private HBox header;

        @FXML
        private HBox headerHBox;

        @FXML
        private HBox headerItemsField;

        @FXML
        private HBox headerButtonsField;

        @FXML
        private HBox headerSearchBarField;

        @FXML
        private HBox headerFilterButtonField;

        @FXML
        private HBox headerReturnButtonField;



        //Define a página que inicializa com o projeto
        String currentPage = "Dashboard.fxml";
        String lastPage = "";
        private static TemplateViewModel instance;

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            loadPage(currentPage);

            Button buttonFilterDashboard = new Button("Filtrar");
            buttonFilterDashboard.setId("filterDashboard");
            buttonFilterDashboard.getStyleClass().add("filter-button");

            buildHeader(false,"Dashboard", buttonFilterDashboard);
            instance = this;
        }

        //Define os métodos que "chamam" as páginas para a tela
        @FXML
        void goToLastPage(MouseEvent event){
            String tempPage = currentPage;
            currentPage = lastPage;
            lastPage = tempPage;
            loadPage(currentPage);
        }

        @FXML
        void goToMain(MouseEvent event){
            lastPage = currentPage;
            currentPage = "Dashboard.fxml";
            loadPage(currentPage);

            Button buttonFilterDashboard = new Button("Filtrar");
            buttonFilterDashboard.setId("filterDashboard");
            buttonFilterDashboard.getStyleClass().add("filter-button");

            buildHeader(false,"Dashboard", buttonFilterDashboard);
        }

        @FXML
        void goToDashboard(MouseEvent event) {
            lastPage = currentPage;
            currentPage = "Dashboard.fxml";
            loadPage(currentPage);

            Button buttonFilterDashboard = new Button("Filtrar");
            buttonFilterDashboard.setId("filterDashboard");
            buttonFilterDashboard.getStyleClass().add("filter-button");

            buildHeader(false,"Dashboard", buttonFilterDashboard);
        }

        @FXML
        void goToCollaborators(MouseEvent event) {
            lastPage = currentPage;
            currentPage = "Collaborators.fxml";
            loadPage(currentPage);

            Button buttonAddCollaborator = new Button("Adicionar Colaborador");
            buttonAddCollaborator.setId("buttonAddCollaborator");
            buttonAddCollaborator.setOnMouseClicked(event2 -> switchScreen("RegisterCollaborator.fxml"));

            Button buttonFilterCollaborators = new Button("Filtrar");
            buttonFilterCollaborators.getStyleClass().add("filter-button");

            TextField searchBarCollaborators = new TextField();
            searchBarCollaborators.setId("searchBarCollaborators");

            buildHeader(false,"Colaboradores", buttonAddCollaborator, buttonFilterCollaborators, searchBarCollaborators);
        }

        @FXML
        void goToGoalTemplates(MouseEvent event) {
            lastPage = currentPage;
            currentPage = "GoalTemplates.fxml";
            loadPage(currentPage);

            buildHeader(false, "Modelos");
        }

        @FXML
        void goToSettings(MouseEvent event) {
            lastPage = currentPage;
            currentPage = "Settings.fxml";
            loadPage(currentPage);

            buildHeader(false, "Gerenciamento");
        }

        @FXML
        void goToProfile(MouseEvent event) {
            lastPage = currentPage;
            currentPage = "Profile.fxml";
            loadPage(currentPage);

            buildHeader(false, "Perfil");
        }

        public void loadPage(String pageName){
            loadPage(pageName, controller -> {});
        }

        //Método carregarPagina (para carregar uma pagina)
        public void loadPage(String pageName, Consumer<Object> configurator) {

            //Configuracao pro 'selecionado' do menu
            menuDashboard.getStyleClass().remove("selecionado");
            menuCollaborators.getStyleClass().remove("selecionado");
            menuGoalTemplates.getStyleClass().remove("selecionado");
            menuSettings.getStyleClass().remove("selecionado");
            menuProfile.getStyleClass().remove("selecionado");

            switch (pageName) {
                case "Dashboard.fxml":
                    menuDashboard.getStyleClass().add("selecionado");
                    break;
                case "Collaborators.fxml":
                    menuCollaborators.getStyleClass().add("selecionado");

                    break;
                case "GoalTemplates.fxml":
                    menuGoalTemplates.getStyleClass().add("selecionado");
                    break;
                case "Settings.fxml":
                    menuSettings.getStyleClass().add("selecionado");
                    break;
                case "Profile.fxml":
                    menuProfile.getStyleClass().add("selecionado");
                    break;
            }

            //'chama' a pagina
            Parent root = null;
            try{
                String path = "/com/dottec/pdi/project/pdi/views/" + pageName;

                FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
                root = loader.load();

                Object controller = loader.getController();
                configurator.accept(controller);
            } catch (IOException e) {
                e.printStackTrace();
            }
            tmpCenter.setCenter(root);
        }


        public static void switchScreen(String pageName) {
            instance.loadPage(pageName, controller -> {});
        }

        public static void switchScreen(String pageName, Consumer<Object> configurator) {
            // Ele usa a instância que salvamos para chamar o método real de troca de página.
            if (instance != null) {
                instance.loadPage(pageName, configurator);
            } else {
                System.err.println("A instância do TemplateController é nula. A tela principal já foi carregada?");
            }
        }

        public void buildHeader(boolean returnButton, String label, Node... headerItems) {
            headerLabel.setText(label);

            headerButtonsField.getChildren().clear();
            headerReturnButtonField.getChildren().clear();
            headerSearchBarField.getChildren().clear();
            headerFilterButtonField.getChildren().clear();

            if (headerItems == null) return;
            if (returnButton){
                Image img = new Image(getClass().getResourceAsStream("/com/dottec/pdi/project/pdi/static/img/arrow-left.png"));
                ImageView icon = new ImageView(img);
                icon.setFitWidth(16);
                icon.setFitHeight(16);
                Button returnBtn = new Button();
                returnBtn.setGraphic(icon);
                returnBtn.getStyleClass().add("return-button");
                headerReturnButtonField.getChildren().add(returnBtn);
                returnBtn.setOnMouseClicked(this::goToLastPage);
            }

            Arrays.stream(headerItems).forEach(item -> {
                if (item instanceof Button btn) {
                    if (btn.getStyleClass().contains("filter-button")) {
                        headerFilterButtonField.getChildren().add(btn);
                    } else {
                        btn.getStyleClass().add("basic-button");
                        headerButtonsField.getChildren().add(btn);
                    }
                } else if (item instanceof TextField tf) {
                    tf.getStyleClass().add("search-bar");
                    Label searchIcon = new Label("🔍");
                    searchIcon.setStyle("-fx-padding: 1; -fx-font-size: 24; -fx-text-fill: #4B0081");
                    headerSearchBarField.getChildren().add(searchIcon);
                    headerSearchBarField.getChildren().add(tf);
                } else {
                    System.out.println("Node ignorado: " + item.getClass().getSimpleName());
                }
            });
        }
    }