package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.utils.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.function.Consumer;

public class TemplateViewModel implements Initializable {

    //Declara os ID's criados

    //AnchorPane
    @FXML private AnchorPane leftMenu;
    @FXML private AnchorPane menuDashboard;
    @FXML private AnchorPane menuCollaborators;
    @FXML private AnchorPane menuGoalTemplates;
    @FXML private AnchorPane menuSettings;
    @FXML private AnchorPane menuProfile;
    @FXML private AnchorPane AnchorMainPane;

    //Label
    @FXML private Label labelArrow;
    @FXML private Label labelCollaborator;
    @FXML private Label labelSector;
    @FXML private Label labelSettings;
    @FXML private Label labelProfile;

    //StackPane
    @FXML private StackPane mainStackPane;

    //BorderPane
    @FXML private BorderPane mainPane;
    @FXML private BorderPane tmpCenter;

    //ImageView
    @FXML private ImageView menuLogo;

    private final String mainPage = "Dashboard.fxml";

    //Define a página que inicializa com o projeto

    private final Stack<Node> pageStack = new Stack<>();
    private final Stack<String> pageNameStack = new Stack<>();
    private String currentPage = mainPage;
    public static String getCurrentPage(){return instance.currentPage;}
    public static void setCurrentPage(String pageName){instance.currentPage = pageName;}
    private static TemplateViewModel instance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarPagina(mainPage);
        loadHeader(mainPage);
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

    private void updateSideBar(String pageName){
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
    }

    public void carregarPagina(String pageName){
        carregarPagina(pageName, controller -> {});
    }

    //Método carregarPagina (para carregar uma pagina)
    public void carregarPagina(String pageName,  Consumer<Object> configurator) {
        updateSideBar(pageName);
        //'chama' a pagina
        try{
            String caminhoCompleto = "/com/dottec/pdi/project/pdi/views/" + pageName;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoCompleto));
            Parent root = loader.load();
            Object controller = loader.getController();
            configurator.accept(controller);

            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            pageNameStack.push(currentPage);
            currentPage = pageName;
            if (tmpCenter.getCenter() != null) {
                pageStack.push(tmpCenter.getCenter());
            }

            tmpCenter.setCenter(scrollPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void goBack(){
        if(!instance.pageStack.isEmpty()){
            Node previousPage = instance.pageStack.pop();
            String pageName = instance.pageNameStack.pop();
            setCurrentPage(pageName);
            instance.loadHeader(pageName);
            instance.tmpCenter.setCenter(previousPage);
        }
    }

    private void loadHeader(String pageName){
        try{
            String fxmlPath = "/com/dottec/pdi/project/pdi/views/Header.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            HeaderViewModel headerViewModel = loader.getController();
            HeaderViewModel.updateHeader(pageName);

            tmpCenter.setTop(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMessage(String headerMessage, String message){
        FXUtils.buildMessageBox(instance.mainStackPane, message, headerMessage);
    }

    public static void showSuccessMessage(String message){
        FXUtils.buildMessageBox(instance.mainStackPane, message, "Sucesso!");
    }

    public static void showSuccessMessage(String headerMessage, String message){
        FXUtils.buildMessageBox(instance.mainStackPane, message, headerMessage);
    }

    public static void showErrorMessage(String message){
        FXUtils.buildMessageBox(true, instance.mainStackPane, message, "Erro!");
    }

    public static void showErrorMessage(String headerMessage, String message){
        FXUtils.buildMessageBox(true, instance.mainStackPane, message, headerMessage);
    }


}