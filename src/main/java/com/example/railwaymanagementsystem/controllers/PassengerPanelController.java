package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.RailSafarApp;
import com.example.railwaymanagementsystem.services.AppSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * Controller for the Passenger Panel - Main container with sidebar
 */
public class PassengerPanelController {

    @FXML private StackPane contentArea;

    // Button references
    @FXML private Button btnBookTicket;
    @FXML private Button btnViewSchedule;
    @FXML private Button btnCheckStatus;
    @FXML private Button btnCancelTicket;
    @FXML private Button btnNotifications;
    @FXML private Button btnPayment;
    @FXML private Button btnPaymentHistory;
    @FXML private Button btnProfile;
    @FXML private Button btnLogout;

    private Button currentActiveButton;

    private final AppSession session = AppSession.getInstance();

    @FXML
    private void initialize() {
        // Load Book Ticket by default
        currentActiveButton = btnBookTicket;
        showBookTicket();
    }

    @FXML
    private void showBookTicket() {
        setActiveButton(btnBookTicket);
        loadContent("BookTicket.fxml");
    }

    @FXML
    private void showViewSchedule() {
        setActiveButton(btnViewSchedule);
        loadContent("ViewSchedule.fxml");
    }

    @FXML
    private void showCheckStatus() {
        setActiveButton(btnCheckStatus);
        loadContent("CheckTrainStatus.fxml");
    }

    @FXML
    private void showCancelTicket() {
        setActiveButton(btnCancelTicket);
        loadContent("CancelTicket.fxml");
    }

    @FXML
    private void showNotifications() {
        setActiveButton(btnNotifications);
        loadContent("Notifications.fxml");
    }

    @FXML
    private void showPayment() {
        setActiveButton(btnPayment);
        loadContent("Payment.fxml");
    }

    @FXML
    private void showPaymentHistory() {
        setActiveButton(btnPaymentHistory);
        loadContent("PaymentHistory.fxml");
    }

    @FXML
    private void showProfile() {
        setActiveButton(btnProfile);
        loadContent("UserProfile.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            session.clear();
            RailSafarApp.showWelcomeScreen();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error logging out: " + e.getMessage());
        }
    }

    /**
     * Handle mouse entered event for buttons (hover effect)
     */
    @FXML
    private void onMouseEntered(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button != currentActiveButton && button != btnLogout) {
            button.setStyle("-fx-background-color: #165638; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16; -fx-font-size: 14px; -fx-border-width: 0; -fx-background-radius: 8; -fx-cursor: hand;");
        } else if (button == btnLogout) {
            button.setStyle("-fx-background-color: #165638; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: #e8f5f0; -fx-border-width: 1 0 0 0;");
        }
    }

    /**
     * Handle mouse exited event for buttons (remove hover effect)
     */
    @FXML
    private void onMouseExited(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button != currentActiveButton && button != btnLogout) {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16; -fx-font-size: 14px; -fx-border-width: 0; -fx-background-radius: 8; -fx-cursor: hand;");
        } else if (button == btnLogout) {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: #e8f5f0; -fx-border-width: 1 0 0 0;");
        }
    }

    /**
     * Set the active button and update styles
     */
    private void setActiveButton(Button button) {
        if (currentActiveButton != null) {
            currentActiveButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16; -fx-font-size: 14px; -fx-border-width: 0; -fx-background-radius: 8; -fx-cursor: hand;");
        }
        currentActiveButton = button;
        button.setStyle("-fx-background-color: #165638; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16; -fx-font-size: 14px; -fx-border-width: 0; -fx-background-radius: 8; -fx-cursor: hand;");
    }

    /**
     * Load content into the content area
     */
    private void loadContent(String fxmlFile) {
        try {
            java.net.URL resourceUrl = RailSafarApp.class.getResource(fxmlFile);
            if (resourceUrl == null) {
                throw new Exception("Cannot find FXML file: " + fxmlFile);
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent content = loader.load();

            // Special case for PaymentController to refresh its content
            if ("Payment.fxml".equals(fxmlFile)) {
                PaymentController controller = loader.getController();
                if (controller != null) {
                    controller.refresh();
                }
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading " + fxmlFile + ": " + e.getMessage());
            javafx.scene.control.Label placeholder = new javafx.scene.control.Label(
                    "Error: Cannot load " + fxmlFile + "\n\n" + e.getMessage()
            );
            placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #dc2626; -fx-padding: 20;");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(placeholder);
        }
    }
}
