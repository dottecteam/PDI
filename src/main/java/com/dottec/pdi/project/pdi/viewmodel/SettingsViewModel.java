package com.dottec.pdi.project.pdi.viewmodel;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class SettingsViewModel {

    //'Puxa' o metodo do pai
    @FXML
    void goToTeste(MouseEvent event){
        TemplateViewModel.trocarDeTela("Dashboard.fxml");
    }
}