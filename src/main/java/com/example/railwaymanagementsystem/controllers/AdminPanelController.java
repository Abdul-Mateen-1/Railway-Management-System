package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.RailSafarApp;
import com.example.railwaymanagementsystem.services.AppSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

/**
 * Controller for the Admin Panel - Main container with sidebar
 */
public class AdminPanelController {

    @FXML private StackPane contentArea;

    @FXML
    private void initialize() {
        // Load Train Management by default
        showTrainManagement();
    }

    @FXML
    private void showDashboard() {
        loadContent("Dashboard.fxml");
    }

    @FXML
    private void showTrainManagement() {
        loadContent("TrainManagement.fxml");
    }

    @FXML
    private void showScheduleManagement() {
        loadContent("ScheduleManagement.fxml");

    }

    @FXML
    private void showReports() {
        loadContent("GenerateReports.fxml");
    }

    @FXML
    private void showUserManagement() {
        loadContent("UserManagement.fxml");
    }

    @FXML
    private void showSettings() {
        loadContent("Settings.fxml");
    }

    private final AppSession session = AppSession.getInstance();

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
     * Load content into the content area
     */
    private void loadContent(String fxmlFile) {
        try {
            System.out.println("Loading: " + fxmlFile);

            // Use RailSafarApp.class to load resources (same as showWelcomeScreen)
            java.net.URL resourceUrl = RailSafarApp.class.getResource(fxmlFile);

            if (resourceUrl == null) {
                // Try with leading slash
                resourceUrl = RailSafarApp.class.getResource("/" + fxmlFile);
            }

            if (resourceUrl == null) {
                throw new Exception("Cannot find FXML file: " + fxmlFile);
            }

            System.out.println("Found resource at: " + resourceUrl);

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);

            System.out.println("Successfully loaded: " + fxmlFile);

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
