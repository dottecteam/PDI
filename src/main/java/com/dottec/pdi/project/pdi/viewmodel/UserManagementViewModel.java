package com.dottec.pdi.project.pdi.viewmodel;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class UserManagementViewModel {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView backArrow;

    @FXML
    private Button buttonAdd;

    @FXML
    private Button buttonFilterPDIsPage;

    @FXML
    private ImageView deleteUser;

    @FXML
    private ImageView editUser;

    @FXML
    private HBox headerHBox;

    @FXML
    private TextField inputSearchUser;

    @FXML
    private ImageView searchUser;

    @FXML
    private Label userEmail;

    @FXML
    private Label userName;

    @FXML
    private Pane userPane;

    @FXML
    private ImageView userPhoto;

    @FXML
    void initialize() {
        assert backArrow != null : "fx:id=\"backArrow\" was not injected: check your FXML file 'UserManagementView.fxml'.";
        assert buttonAdd != null : "fx:id=\"buttonAdd\" was not injected: check your FXML file 'UserManagementView.fxml'.";
        assert buttonFilterPDIsPage != null : "fx:id=\"buttonFilterPDIsPage\" was not injected: check your FXML file 'UserManagementView.fxml'.";
        assert deleteUser != null : "fx:id=\"deleteUser\" was not injected: check your FXML file 'UserManagementView.fxml'.";
        assert editUser != null : "fx:id=\"editUser\" was not injected: check your FXML file 'UserManagementView.fxml'.";
        assert headerHBox != null : "fx:id=\"headerHBox\" was not injected: check your FXML file 'UserManagementView.fxml'.";
        assert inputSearchUser != null : "fx:id=\"inputSearchUser\" was not injected: check your FXML file 'UserManagementView.fxml'.";
        assert searchUser != null : "fx:id=\"searchUser\" was not injected: check your FXML file 'UserManagementView.fxml'.";
        assert userEmail != null : "fx:id=\"userEmail\" was not injected: check your FXML file 'UserManagementView.fxml'.";
        assert userName != null : "fx:id=\"userName\" was not injected: check your FXML file 'UserManagementView.fxml'.";
        assert userPane != null : "fx:id=\"userPane\" was not injected: check your FXML file 'UserManagementView.fxml'.";
        assert userPhoto != null : "fx:id=\"userPhoto\" was not injected: check your FXML file 'UserManagementView.fxml'.";

    }

}
