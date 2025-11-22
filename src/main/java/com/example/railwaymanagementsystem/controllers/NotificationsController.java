package com.example.railwaymanagementsystem.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Controller for Notifications Screen
 */
public class NotificationsController {

    @FXML private VBox notificationsList;

    @FXML
    private void initialize() {
        loadNotifications();
    }

    private void loadNotifications() {
        notificationsList.getChildren().clear();

        // Mock notifications
        addNotification("🎫", "Booking Confirmed",
                "Your ticket for Train 1UP (Karachi Express) has been confirmed. PNR: PNR123456",
                "2 hours ago", true);

        addNotification("⚠️", "Train Delay Alert",
                "Train 2DN (Lahore Express) is delayed by 30 minutes. Updated arrival: 9:30 PM",
                "5 hours ago", false);

        addNotification("💰", "Refund Processed",
                "Refund of PKR 2,400 for cancelled booking PNR987654 has been processed",
                "1 day ago", false);

        addNotification("🔔", "Schedule Change",
                "Train 3UP (Green Line) schedule has been updated. Check new timings",
                "2 days ago", false);

        addNotification("✅", "Payment Successful",
                "Payment of PKR 5,000 received for booking PNR123456",
                "2 days ago", false);

        addNotification("ℹ️", "New Route Announcement",
                "New express train route announced: Karachi - Multan direct service",
                "3 days ago", false);
    }

    private void addNotification(String icon, String title, String message,
                                 String time, boolean unread) {
        HBox notification = new HBox(15);
        notification.setAlignment(Pos.TOP_LEFT);
        notification.setPadding(new Insets(15));
        notification.setStyle(
                "-fx-background-color: " + (unread ? "#e8f5f0" : "white") + "; " +
                        "-fx-border-color: #e5e7eb; -fx-border-width: 1px; " +
                        "-fx-border-radius: 8px; -fx-background-radius: 8px;"
        );

        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");

        // Content
        VBox content = new VBox(5);
        HBox.setHgrow(content, Priority.ALWAYS);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        header.getChildren().addAll(titleLabel, spacer, timeLabel);

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 13px;");
        messageLabel.setWrapText(true);

        content.getChildren().addAll(header, messageLabel);

        // Actions
        VBox actions = new VBox(5);
        actions.setAlignment(Pos.CENTER);

        if (unread) {
            Button markReadBtn = new Button("✓");
            markReadBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            markReadBtn.setTooltip(new Tooltip("Mark as read"));
            markReadBtn.setOnAction(e -> {
                notification.setStyle(notification.getStyle().replace("#e8f5f0", "white"));
                markReadBtn.setVisible(false);
            });
            actions.getChildren().add(markReadBtn);
        }

        notification.getChildren().addAll(iconLabel, content, actions);
        notificationsList.getChildren().add(notification);
    }

    @FXML
    private void handleMarkAllRead() {
        loadNotifications(); // Reload without unread notifications
        showSuccess("All notifications marked as read");
    }

    @FXML
    private void showAllNotifications() {
        loadNotifications();
    }

    @FXML
    private void showUnreadNotifications() {
        notificationsList.getChildren().clear();
        addNotification("🎫", "Booking Confirmed",
                "Your ticket has been confirmed", "2 hours ago", true);
    }

    @FXML
    private void showBookingNotifications() {
        notificationsList.getChildren().clear();
        addNotification("🎫", "Booking Confirmed",
                "Your ticket has been confirmed", "2 hours ago", false);
        addNotification("💰", "Refund Processed",
                "Refund has been processed", "1 day ago", false);
    }

    @FXML
    private void showStatusNotifications() {
        notificationsList.getChildren().clear();
        addNotification("⚠️", "Train Delay Alert",
                "Train is delayed", "5 hours ago", false);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
