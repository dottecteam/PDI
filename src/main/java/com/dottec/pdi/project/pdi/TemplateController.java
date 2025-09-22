    package com.dottec.pdi.project.pdi;

    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Label;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.Parent;
    import javafx.scene.input.MouseEvent;
    import javafx.scene.layout.BorderPane;

    import java.io.IOException;
    import java.net.URL;
    import java.util.ResourceBundle;

    public class TemplateController implements Initializable {

        //Declara os ID's criados

        //AnchorPane
        @FXML
        private AnchorPane leftMenu;

        @FXML
        private AnchorPane tmpCenter;

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


        //BorderPane
        @FXML
        private BorderPane mainPane;



        //Menu

        //Define a página que inicializa com o projeto
        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            carregarPagina("Goal.fxml");
        }

        //Define os métodos que "chamam" as páginas para a tela
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

        //Méeodo carregarPagina
        private void carregarPagina(String nomePagina) {
            Parent root = null;
            try{
                String caminhoCompleto = "/com/dottec/pdi/project/pdi/views/" + nomePagina;
                root = FXMLLoader.load(getClass().getResource(caminhoCompleto));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mainPane.setCenter(root);
        }
    }