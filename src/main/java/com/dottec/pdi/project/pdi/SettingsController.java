package com.dottec.pdi.project.pdi;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class SettingsController{

    //'Puxa' o metodo do pai
    @FXML
    void goToTeste(MouseEvent event){
        TemplateController.trocarDeTela("Dashboard.fxml");
    }
}