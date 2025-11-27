package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.AuthController;
import com.dottec.pdi.project.pdi.controllers.NotificationCreationController;
import com.dottec.pdi.project.pdi.dao.UserDAO;
import com.dottec.pdi.project.pdi.model.User;
import javafx.animation.Animation;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import animatefx.animation.*;


public class LoginViewModel implements Initializable {

    @FXML
    private StackPane rootPane;

    @FXML
    private Polygon bottomPolygon;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button logar;

    @FXML
    private ColumnConstraints formColumn;

    @FXML
    private ColumnConstraints imageColumn;

    @FXML
    private ImageView imageView;

    private static final double MIN_IMAGE_HEIGHT = 1000.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {

            double height = newVal.doubleValue();

            updatePolygonPoints();
            updateImageMargin(height);
            updateImageSize(height);
        });

        rootPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            updatePolygonPoints();
            handleResponsiveLayout(newWidth.doubleValue());
        });


        Pulse animacaoLogin = new Pulse(imageView);
        animacaoLogin.setCycleCount(Animation.INDEFINITE);
        animacaoLogin.setSpeed(0.2);
        animacaoLogin.play();


        logar.setOnMouseEntered(e -> {
            Pulse animacao = new Pulse(logar);
            animacao.setCycleCount(1);
            animacao.play();
        });

    }

    private void updatePolygonPoints() {
        double width = rootPane.getWidth();
        double height = rootPane.getHeight();

        if (width == 0 || height == 0) {
            return;
        }

        ObservableList<Double> points = bottomPolygon.getPoints();
        points.clear();

        points.addAll(
                0.0, height,
                width, height * 0.6,
                width, height,
                0.0, height
        );
    }

    private void handleResponsiveLayout(double width) {
        if (imageView == null) return;

        if (width < 1000) {
            imageView.setVisible(false);
            imageView.setManaged(false);

            formColumn.setPercentWidth(100);
            imageColumn.setPercentWidth(0);

            rootPane.lookupAll(".vbox").forEach(node -> {
                VBox box = (VBox) node;
                box.setAlignment(javafx.geometry.Pos.CENTER);
            });

        } else {
            imageView.setVisible(true);
            imageView.setManaged(true);

            formColumn.setPercentWidth(40);
            imageColumn.setPercentWidth(60);

            rootPane.lookupAll(".vbox").forEach(node -> {
                VBox box = (VBox) node;
                box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            });
        }
    }

    private void updateImageMargin(double height) {
        if (imageView == null) return;

        double offset = -height * 0.25;
        StackPane.setMargin(imageView, new Insets(0, 0, offset, 0));
    }

    private void updateImageSize(double height) {
        if (imageView == null || height == 0) return;
        double proportionalHeight = height * 1.1;
        if (proportionalHeight < MIN_IMAGE_HEIGHT) {
            imageView.setFitHeight(MIN_IMAGE_HEIGHT);
        } else {
            imageView.setFitHeight(proportionalHeight);
        }
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Campos obrigatórios", "Por favor, preencha todos os campos.");
            return;
        }

        User user = UserDAO.login(email, password);

        if (user != null) {
            AuthController.getInstance().login(user);
            NotificationCreationController.createExpirationNotifications(user);
            try {
                Stage currentStage = (Stage) emailField.getScene().getWindow();
                currentStage.close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dottec/pdi/project/pdi/views/Template.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("PDI");
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erro", "Falha ao carregar o painel: " + e.getMessage());
            }
        } else {
            showAlert("Login inválido", "Email ou senha incorretos.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
