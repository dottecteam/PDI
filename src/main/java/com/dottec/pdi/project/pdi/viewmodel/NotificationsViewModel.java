package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.AuthController;
import com.dottec.pdi.project.pdi.controllers.NotificationController;
import com.dottec.pdi.project.pdi.model.Notification;
import com.dottec.pdi.project.pdi.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class NotificationsViewModel {

    @FXML private ListView<Notification> notificationsListView;

    @FXML
    public void initialize() {
        notificationsListView.setCellFactory(param -> new NotificationCell());
        loadNotifications();
    }

    private void loadNotifications() {
        User user = AuthController.getInstance().getLoggedUser();
        if (user == null) {
            TemplateViewModel.showErrorMessage("Erro", "Nenhum usuário logado para carregar notificações.");
            return;
        }

        List<Notification> allNotifications = NotificationController.findAllNotifications();

        List<Notification> userNotifications = allNotifications.stream()
                .filter(n -> n.getUserId() == user.getId())
                .toList();

        ObservableList<Notification> observableNotifications = FXCollections.observableArrayList(userNotifications);
        notificationsListView.setItems(observableNotifications);

        if (userNotifications.isEmpty()) {
            Label emptyLabel = new Label("Você não possui novas notificações.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080;");
            notificationsListView.setPlaceholder(emptyLabel);
        }
    }

    private class NotificationCell extends ListCell<Notification> {
        private final HBox hbox = new HBox(10);
        private final VBox textVBox = new VBox(2);
        private final Label messageLabel = new Label();
        private final Label typeLabel = new Label();
        private final Region spacer = new Region();

        public NotificationCell() {
            super();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            hbox.setAlignment(Pos.CENTER_LEFT);
            textVBox.setAlignment(Pos.CENTER_LEFT);

            // Estilos para os textos
            messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black; -fx-wrap-text: true; -fx-max-width: 500;");
            typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #AF69CD; -fx-font-weight: bold;");

            textVBox.getChildren().addAll(messageLabel, typeLabel);
            hbox.getChildren().addAll(textVBox, spacer);
        }

        @Override
        protected void updateItem(Notification notification, boolean empty) {
            super.updateItem(notification, empty);
            if (empty || notification == null) {
                setGraphic(null);
                setStyle("-fx-background-color: transparent;");
            } else {
                messageLabel.setText(notification.getNotMessage());
                typeLabel.setText("Tipo: " + notification.getNotType().name().toUpperCase());

                // Define a cor de fundo com base no status de leitura
                if (notification.isNotIsRead()) {
                    setStyle("-fx-background-color: #FFFFFF; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
                } else {
                    setStyle("-fx-background-color: #CCA9DD; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
                }

                setGraphic(hbox);
                setPadding(new javafx.geometry.Insets(15, 15, 15, 15));
                setPrefHeight(VBox.USE_COMPUTED_SIZE);

                setOnMouseClicked(e -> {
                    if (!notification.isNotIsRead()) {
                        NotificationController.markAsRead(notification.getNotId());
                        notification.setNotIsRead(true);
                        updateItem(notification, false);
                    }
                });
            }
        }
    }
}