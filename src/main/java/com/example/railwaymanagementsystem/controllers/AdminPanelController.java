package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.RailSafarApp;
import com.example.railwaymanagementsystem.services.AppSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the Admin Panel - Main container with sidebar
 */
public class AdminPanelController {

    @FXML private StackPane contentArea;
    @FXML private Button dashboardButton;
    @FXML private Button trainManagementButton;
    @FXML private Button scheduleManagementButton;
    @FXML private Button reportsButton;
    @FXML private Button userManagementButton;
    @FXML private Button settingsButton;

    private List<Button> navigationButtons;
    private static AdminPanelController instance;

    public AdminPanelController() {
        instance = this;
    }

    public static AdminPanelController getInstance() {
        return instance;
    }

    @FXML
    private void initialize() {
        navigationButtons = List.of(dashboardButton, trainManagementButton, scheduleManagementButton,
                reportsButton, userManagementButton, settingsButton);
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        loadContent("Dashboard.fxml", dashboardButton);
    }

    @FXML
    private void showTrainManagement() {
        loadContent("TrainManagement.fxml", trainManagementButton);
    }

    @FXML
    private void showScheduleManagement() {
        loadContent("ScheduleManagement.fxml", scheduleManagementButton);
    }

    @FXML
    private void showReports() {
        loadContent("GenerateReports.fxml", reportsButton);
    }

    @FXML
    private void showUserManagement() {
        loadContent("UserManagement.fxml", userManagementButton);
    }

    @FXML
    private void showSettings() {
        loadContent("Settings.fxml", settingsButton);
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

    private void setActiveButton(Button activeButton) {
        for (Button button : navigationButtons) {
            if (button.equals(activeButton)) {
                if (!button.getStyleClass().contains("active")) {
                    button.getStyleClass().add("active");
                }
            } else {
                button.getStyleClass().remove("active");
            }
        }
    }

    public void loadContent(String fxmlFile, Button activeButton) {
        try {
            if (activeButton != null) {
                setActiveButton(activeButton);
            } else {
                // If no button is passed, clear all active states
                for (Button button : navigationButtons) {
                    button.getStyleClass().remove("active");
                }
            }
            FXMLLoader loader = new FXMLLoader(RailSafarApp.class.getResource(fxmlFile));
            Parent content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading " + fxmlFile + ": " + e.getMessage());
        }
    }
}
