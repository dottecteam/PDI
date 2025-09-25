    package com.dottec.pdi.project.pdi;

    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.geometry.Pos;
    import javafx.scene.Node;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.control.TextField;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.Parent;
    import javafx.scene.input.MouseEvent;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.VBox;
    import javafx.scene.shape.Line;

    import java.io.IOException;
    import java.net.URL;
    import java.util.Arrays;
    import java.util.ResourceBundle;

    public class TemplateController implements Initializable {

        //Declara os ID's criados

        //AnchorPane
        @FXML
        private AnchorPane leftMenu;

        @FXML
        private AnchorPane menuDashboard;

        @FXML
        private AnchorPane menuCollaborators;

        @FXML
        private AnchorPane menuModels;

        @FXML
        private AnchorPane menuSettings;

        @FXML
        private AnchorPane menuProfile;

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


        //BorderPane
        @FXML
        private BorderPane mainPane;

        @FXML
        private BorderPane tmpCenter;

        //ImageView
        @FXML
        private ImageView menuLogo;


        //HBox
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
        


        //Define a página que inicializa com o projeto

        String paginaPadrao = "Dashboard.fxml";

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            buildHeader("Dashboard");
            carregarPagina(paginaPadrao);
        }

        //Define os métodos que "chamam" as páginas para a tela
        @FXML
        void goToMain(MouseEvent event){
            carregarPagina(paginaPadrao);
        }

        @FXML
        void goToDashboard(MouseEvent event) {
            carregarPagina("Dashboard.fxml");
        }

        @FXML
        void goToCollaborators(MouseEvent event) {
            carregarPagina("Collaborators.fxml");
        }

        @FXML
        void goToModels(MouseEvent event) {
            carregarPagina("Models.fxml");
        }

        @FXML
        void goToSettings(MouseEvent event) {
            carregarPagina("Settings.fxml");
        }

        @FXML
        void goToProfile(MouseEvent event) {
            carregarPagina("Profile.fxml");
        }

        //Méeodo carregarPagina (para carregar uma pagina)
        private void carregarPagina(String nomePagina) {

            //Configuracao pro 'selecionado' do menu
            menuDashboard.getStyleClass().remove("selecionado");
            menuCollaborators.getStyleClass().remove("selecionado");
            menuModels.getStyleClass().remove("selecionado");
            menuSettings.getStyleClass().remove("selecionado");
            menuProfile.getStyleClass().remove("selecionado");

            switch (nomePagina) {
                case "Dashboard.fxml":
                    menuDashboard.getStyleClass().add("selecionado");

                    Button buttonFilterDashboard = new Button("Filtrar");
                    buttonFilterDashboard.setId("filterDashboard");
                    buttonFilterDashboard.getStyleClass().add("filter-button");

                    buildHeader("Dashboard", buttonFilterDashboard);
                    break;
                case "Collaborators.fxml":
                    menuCollaborators.getStyleClass().add("selecionado");
                    Button buttonAddCollaborator = new Button("Adicionar Colaborador");
                    buttonAddCollaborator.setId("buttonAddCollaborator");

                    Button buttonFilterCollaborators = new Button("Filtrar");
                    buttonFilterCollaborators.getStyleClass().add("filter-button");
                    buttonFilterCollaborators.setId("buttonFilterCollaborators");

                    TextField searchBarCollaborators = new TextField();
                    searchBarCollaborators.setId("searchBarCollaborators");

                    buildHeader("Colaboradores", buttonAddCollaborator, buttonFilterCollaborators, searchBarCollaborators);

                    break;
                case "Models.fxml":
                    menuModels.getStyleClass().add("selecionado");
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
                String caminhoCompleto = "/com/dottec/pdi/project/pdi/views/" + nomePagina;
                root = FXMLLoader.load(getClass().getResource(caminhoCompleto));
            } catch (IOException e) {
                e.printStackTrace();
            }
            tmpCenter.setCenter(root);
        }

        public void buildHeader(String label, Node... headerItems) {
            headerLabel.setText(label);

            headerButtonsField.getChildren().clear();
            headerSearchBarField.getChildren().clear();
            headerFilterButtonField.getChildren().clear();

            if (headerItems == null) return;

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