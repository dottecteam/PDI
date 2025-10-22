package com.dottec.pdi.project.pdi.viewmodel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class TemplateViewModel implements Initializable {

    //Declara os ID's criados

    //AnchorPane
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

    @FXML
    private HBox headerReturnButtonField;

    private String mainPage = "DashboardTags.fxml";

    //Define a página que inicializa com o projeto

    String previousPage;
    private static TemplateViewModel instance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarPagina(mainPage);
        carregarHeader(mainPage);
        instance = this;
    }

    //Define os métodos que "chamam" as páginas para a tela

    @FXML
    void goToMain(MouseEvent event){
        HeaderViewModel.updateHeader(mainPage);
        carregarPagina(mainPage);
    }

    @FXML
    void goToDashboard(MouseEvent event) {
        HeaderViewModel.updateHeader("Dashboard.fxml");
        carregarPagina("Dashboard.fxml");
    }

    @FXML
    void goToCollaborators(MouseEvent event) {
        HeaderViewModel.updateHeader("Collaborators.fxml");
        carregarPagina("Collaborators.fxml");
    }

    @FXML
    void goToGoalTemplates(MouseEvent event) {
        HeaderViewModel.updateHeader("Collaborators.fxml");
        carregarPagina("GoalTemplates.fxml");
    }

    @FXML
    void goToSettings(MouseEvent event) {
        HeaderViewModel.updateHeader("Settings.fxml");
        carregarPagina("Settings.fxml");
    }

    @FXML
    void goToProfile(MouseEvent event) {
        HeaderViewModel.updateHeader("Profile.fxml");
        carregarPagina("Profile.fxml");
    }

    public void carregarPagina(String nomePagina){
        carregarPagina(nomePagina, controller -> {});
    }

    //Método carregarPagina (para carregar uma pagina)
    public void carregarPagina(String nomePagina,  Consumer<Object> configurator) {

        //Configuracao pro 'selecionado' do menu
        menuDashboard.getStyleClass().remove("selecionado");
        menuCollaborators.getStyleClass().remove("selecionado");
        menuGoalTemplates.getStyleClass().remove("selecionado");
        menuSettings.getStyleClass().remove("selecionado");
        menuProfile.getStyleClass().remove("selecionado");

        switch (nomePagina) {
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
            String caminhoCompleto = "/com/dottec/pdi/project/pdi/views/" + nomePagina;

            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoCompleto));
            root = loader.load();

            Object controller = loader.getController();
            configurator.accept(controller);

        } catch (IOException e) {
            e.printStackTrace();
        }
        tmpCenter.setCenter(root);
    }


    public static void switchScreen(String nomePagina) {
        instance.carregarPagina(nomePagina,  controller -> {});
    }

    public static void switchScreen(String nomePagina, Consumer<Object> configurator) {
        // Ele usa a instância que salvamos para chamar o método real de troca de página.
        if (instance != null) {
            instance.carregarPagina(nomePagina, configurator);
            HeaderViewModel.updateHeader(nomePagina);
        } else {
            System.err.println("A instância do TemplateController é nula. A tela principal já foi carregada?");
        }
    }

    private void carregarHeader(String nomePagina){
        Parent root = null;
        try{
            String caminhoCompleto = "/com/dottec/pdi/project/pdi/views/Header.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoCompleto));
            root = loader.load();

            HeaderViewModel headerViewModel = loader.getController();
            HeaderViewModel.updateHeader(nomePagina);

            tmpCenter.setTop(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}