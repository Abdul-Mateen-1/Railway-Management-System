package com.example.railwaymanagementsystem.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for Dashboard Screen
 */
public class DashboardController {

    @FXML private Label totalTrainsLabel;
    @FXML private Label activeRoutesLabel;
    @FXML private Label dailyPassengersLabel;
    @FXML private Label revenueLabel;
    @FXML private TableView<String> activityTable;

    @FXML
    private void initialize() {
        // Statistics are already set in FXML
        // In production, fetch from database

        // Initialize activity table with sample data
        ObservableList<String> activities = FXCollections.observableArrayList(
                "Train 1UP departed from Karachi",
                "Train 2DN delayed by 30 minutes",
                "New booking: PNR 12345",
                "Train 5UP arrived at Lahore",
                "Cancellation: PNR 98765"
        );

        // Note: In production, use proper data models
    }

    @FXML
    private void handleViewReports() {
        System.out.println("Navigate to Reports");
    }

    @FXML
    private void handleManageTrains() {
        System.out.println("Navigate to Train Management");
    }

    @FXML
    private void handleViewUsers() {
        System.out.println("Navigate to User Management");
    }

    @FXML
    private void handleSettings() {
        System.out.println("Navigate to Settings");
    }
}
