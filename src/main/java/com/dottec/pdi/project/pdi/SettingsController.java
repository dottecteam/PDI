package com.dottec.pdi.project.pdi;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SettingsController implements ParentController{

    private TemplateController TemplateController;

    @Override
    public void setTemplateController(TemplateController controller) {
        this.TemplateController = controller;
    }

    @FXML
    void goToTeste(MouseEvent event) {
        if (TemplateController != null) {
            TemplateController.carregarPagina("Dashboard.fxml");
        } else {
            System.err.println("TemplateController não foi injetado!");
        }
    }
}