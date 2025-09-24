package com.dottec.pdi.project.pdi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;


import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/dottec/pdi/project/pdi/views/AddGoal.fxml"));
        Parent root = fxmlLoader.load();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        scene.getStylesheets().add(getClass().getResource("/com/dottec/pdi/project/pdi/static/style/style_AddGoal.css").toExternalForm());

        stage.setTitle("PDI");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}