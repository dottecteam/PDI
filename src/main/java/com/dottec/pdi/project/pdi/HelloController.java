    package com.dottec.pdi.project.pdi;

    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Label;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.Node;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.input.MouseEvent;
    import javafx.scene.layout.BorderPane;
    import javafx.stage.Stage;
    import java.io.IOException;
    import java.net.URL;
    import java.util.ResourceBundle;

    public class HelloController implements Initializable {

        @FXML //Declara os ID's criados
        private AnchorPane leftMenu;

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
        private AnchorPane tmpCenter;

        @FXML
        private BorderPane mainPane;


        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            carregarPagina("Goal.fxml");
        }

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