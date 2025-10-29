package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.utils.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

    //Define a página que inicializa com o projeto
    private final String mainPage = "Dashboard.fxml";

    private final Stack<Node> pageStack = new Stack<>();
    private final Stack<Node> headerStack = new Stack<>();
    private final Stack<HeaderViewModel> headerControllersStack = new Stack<>();
    private static TemplateViewModel instance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarPagina(mainPage);
        instance = this;
    }


    //Define os métodos que "chamam" as páginas para a tela

    @FXML
    void goToMain(MouseEvent event){
        carregarPagina(mainPage);
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
    void goToGoalTemplates(MouseEvent event) {
        carregarPagina("GoalTemplates.fxml");
    }

    @FXML
    void goToSettings(MouseEvent event) {
        carregarPagina("Settings.fxml");
    }

    @FXML
    void goToProfile(MouseEvent event) {
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
            loadHeader(pageName);
            String caminhoCompleto = "/com/dottec/pdi/project/pdi/views/" + pageName;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoCompleto));
            Parent root = loader.load();
            Object controller = loader.getController();
            configurator.accept(controller);

            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            if (tmpCenter.getCenter() != null) {
                pageStack.push(tmpCenter.getCenter());
            }

            tmpCenter.setCenter(scrollPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void switchScreen(String pageName) {
        instance.carregarPagina(pageName,  controller -> {});
    }

    public static void switchScreen(String pageName, Consumer<Object> configurator) {
        // Ele usa a instância que salvamos para chamar o método real de troca de página.
        if (instance != null) {
            instance.carregarPagina(pageName, configurator);
        } else {
            System.err.println("A instância do TemplateController é nula. A tela principal já foi carregada?");
        }
    }

    public static void goBack(){
        if(!instance.pageStack.isEmpty()){
            Node previousPage = instance.pageStack.pop();
            Node previousHeader = instance.headerStack.pop();
            HeaderViewModel headerController = instance.headerControllersStack.pop();
            HeaderViewModel.setInstance(headerController);
            instance.tmpCenter.setCenter(previousPage);
            instance.tmpCenter.setTop(previousHeader);
        }
    }

    private void loadHeader(String pageName){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dottec/pdi/project/pdi/views/Header.fxml"));
            Parent root = loader.load();

            if(tmpCenter.getTop() != null){
                headerStack.push(tmpCenter.getTop());
                headerControllersStack.push(HeaderViewModel.getController());
            }

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