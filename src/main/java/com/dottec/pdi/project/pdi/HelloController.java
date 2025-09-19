package com.dottec.pdi.project.pdi;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private AnchorPane leftMenu;
    private Label labelArrow;
    private Label labelCollaborator;
    private Label labelSector;
    private Label labelSettings;
    private Label labelProfile;

}