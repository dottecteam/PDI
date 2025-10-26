package com.dottec.pdi.project.pdi.viewmodel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SectorRegistrationViewModel extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        Parent root = 
        FXMLLoader.load(getClass().getResource("/com/dottec/pdi/project/pdi/views/sectorRegistration.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("test");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}