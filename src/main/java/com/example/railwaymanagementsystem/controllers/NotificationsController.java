package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Notification;
import com.example.railwaymanagementsystem.services.AppSession;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

/**
 * Controller for Notifications Screen
 */
public class NotificationsController {

    @FXML private VBox notificationsList;

    private final BackendService backend = BackendService.getInstance();
    private final AppSession session = AppSession.getInstance();
    private ObservableList<Notification> userNotifications;

    @FXML
    private void initialize() {
        session.getCurrentUser().ifPresent(user -> {
            userNotifications = backend.getNotificationsForUser(user.getId());
            loadNotifications();
            
            // Listen for changes and reload
            userNotifications.addListener((ListChangeListener<Notification>) c -> {
                loadNotifications();
            });
        });
    }

    private void loadNotifications() {
        notificationsList.getChildren().clear();
        if (userNotifications != null) {
            for (Notification notification : userNotifications) {
                addNotificationToView(notification);
            }
        }
    }

    private void addNotificationToView(Notification notification) {
        HBox notificationBox = new HBox(15);
        notificationBox.setAlignment(Pos.TOP_LEFT);
        notificationBox.setPadding(new Insets(15));
        updateNotificationStyle(notificationBox, notification);

        Label iconLabel = new Label(getIconForMessage(notification.getMessage()));
        iconLabel.setStyle("-fx-font-size: 24px;");

        VBox content = new VBox(5);
        HBox.setHgrow(content, Priority.ALWAYS);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(getTitleForMessage(notification.getMessage()));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timeLabel = new Label(notification.getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd, hh:mm a")));
        timeLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        header.getChildren().addAll(titleLabel, spacer, timeLabel);

        Label messageLabel = new Label(notification.getMessage());
        messageLabel.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 13px;");
        messageLabel.setWrapText(true);

        content.getChildren().addAll(header, messageLabel);

        VBox actions = new VBox(5);
        actions.setAlignment(Pos.CENTER);

        if (!notification.isRead()) {
            Button markReadBtn = new Button("✓");
            markReadBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            markReadBtn.setTooltip(new Tooltip("Mark as read"));
            markReadBtn.setOnAction(e -> {
                notification.setRead(true);
                updateNotificationStyle(notificationBox, notification);
                markReadBtn.setVisible(false);
            });
            actions.getChildren().add(markReadBtn);
        }

        notificationBox.getChildren().addAll(iconLabel, content, actions);
        notificationsList.getChildren().add(notificationBox);
    }

    private void updateNotificationStyle(HBox box, Notification notification) {
        box.setStyle(
                "-fx-background-color: " + (notification.isRead() ? "white" : "#e8f5f0") + "; " +
                "-fx-border-color: #e5e7eb; -fx-border-width: 1px; " +
                "-fx-border-radius: 8px; -fx-background-radius: 8px;"
        );
    }

    private String getIconForMessage(String message) {
        if (message.contains("booked")) return "🎫";
        if (message.contains("Payment")) return "✅";
        if (message.contains("cancelled")) return "❌";
        if (message.contains("delay")) return "⚠️";
        return "🔔";
    }

    private String getTitleForMessage(String message) {
        if (message.contains("booked")) return "Booking Pending";
        if (message.contains("Payment")) return "Payment Successful";
        if (message.contains("cancelled")) return "Booking Cancelled";
        if (message.contains("delay")) return "Train Delay Alert";
        return "System Notification";
    }

    @FXML
    private void handleMarkAllRead() {
        if (userNotifications != null) {
            userNotifications.forEach(n -> n.setRead(true));
            loadNotifications();
            showSuccess("All notifications marked as read");
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
