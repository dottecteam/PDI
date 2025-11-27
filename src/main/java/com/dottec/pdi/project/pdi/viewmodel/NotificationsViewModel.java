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

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationsViewModel {

    @FXML
    private ListView<Notification> notificationsListView;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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

        // TEMPORÁRIO TALVEZ rs...
        List<Notification> allNotifications = NotificationController.findAllNotifications();
        List<Notification> userNotifications = allNotifications.stream()
                .filter(n -> n.getUserId() == user.getId())
                .sorted((n1, n2) -> {
                    int readStatus = Boolean.compare(n1.isNotIsRead(), n2.isNotIsRead());
                    if (readStatus != 0) return readStatus;

                    return n2.getNotCreatedAt().compareTo(n1.getNotCreatedAt());
                })
                .toList();

        ObservableList<Notification> observableNotifications = FXCollections.observableArrayList(userNotifications);
        notificationsListView.setItems(observableNotifications);

        if (userNotifications.isEmpty()) {
            Label emptyLabel = new Label("Você não possui notificações.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080;");
            notificationsListView.setPlaceholder(emptyLabel);
        }
    }

    private class NotificationCell extends ListCell<Notification> {
        private final HBox hbox = new HBox(10);
        private final VBox textVBox = new VBox(5);
        private final Label messageLabel = new Label();
        private final Label typeLabel = new Label();
        private final Label dateLabel = new Label();
        private final Region spacer = new Region();

        public NotificationCell() {
            super();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            hbox.setAlignment(Pos.CENTER_LEFT);
            textVBox.setAlignment(Pos.TOP_LEFT);

            hbox.getStyleClass().add("notification-card");

            messageLabel.getStyleClass().add("notification-message");
            typeLabel.getStyleClass().add("notification-details");
            dateLabel.getStyleClass().add("notification-date");

            HBox bottomDetails = new HBox(15);
            bottomDetails.setAlignment(Pos.CENTER_LEFT);
            bottomDetails.getChildren().addAll(typeLabel, dateLabel);

            textVBox.getChildren().addAll(messageLabel, bottomDetails);
            hbox.getChildren().addAll(textVBox, spacer);
        }

        @Override
        protected void updateItem(Notification notification, boolean empty) {
            super.updateItem(notification, empty);
            if (empty || notification == null) {
                setGraphic(null);
                setPadding(new javafx.geometry.Insets(0));
            } else {

                messageLabel.setText(notification.getNotMessage());
                typeLabel.setText(notification.getNotType().name().toUpperCase().replace("_", " "));

                // Formatação da data
                Timestamp timestamp = notification.getNotCreatedAt();
                String formattedDate = timestamp != null ? timestamp.toLocalDateTime().format(DATE_FORMATTER) : "Data Desconhecida";
                dateLabel.setText(formattedDate);

                hbox.getStyleClass().removeAll("notification-unread", "notification-read");
                if (notification.isNotIsRead()) {
                    hbox.getStyleClass().add("notification-read");
                } else {
                    hbox.getStyleClass().add("notification-unread");
                }

                setGraphic(hbox);
                setPadding(new javafx.geometry.Insets(5, 0, 5, 0));

                setOnMouseClicked(e -> {
                    if (!notification.isNotIsRead()) {
                        NotificationController.markAsRead(notification.getNotId());
                        notification.setNotIsRead(true);
                        updateItem(notification, false);

                        HeaderViewModel.refreshNotificationBadge();
                    }
                });
            }
        }
    }
}