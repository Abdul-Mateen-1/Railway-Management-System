package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Controller for Dashboard Screen
 */
public class DashboardController {

    @FXML private Label totalTrainsLabel;
    @FXML private Label activeRoutesLabel;
    @FXML private TableView<Booking> activityTable;
    @FXML private TableColumn<Booking, String> timeColumn;
    @FXML private TableColumn<Booking, String> activityColumn;
    @FXML private TableColumn<Booking, String> trainColumn;
    @FXML private TableColumn<Booking, String> statusColumn;

    private final BackendService backend = BackendService.getInstance();

    @FXML
    private void initialize() {
        loadDashboardStats();
        setupActivityTable();
        loadRecentActivity();
    }

    private void loadDashboardStats() {
        int totalTrains = backend.getTrains().size();
        totalTrainsLabel.setText(String.valueOf(totalTrains));

        long uniqueRoutes = backend.getSchedules().stream()
                .map(schedule -> schedule.getRoute())
                .distinct()
                .count();
        activeRoutesLabel.setText(String.valueOf(uniqueRoutes));
    }

    private void setupActivityTable() {
        timeColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm a");
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBookingDateTime().format(formatter));
        });
        activityColumn.setCellValueFactory(cellData -> {
            String activity = String.format("New booking for %d seats from %s to %s",
                    cellData.getValue().getNumberOfSeats(),
                    cellData.getValue().getFromStation(),
                    cellData.getValue().getToStation());
            return new javafx.beans.property.SimpleStringProperty(activity);
        });
        trainColumn.setCellValueFactory(new PropertyValueFactory<>("trainName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadRecentActivity() {
        ObservableList<Booking> recentBookings = FXCollections.observableArrayList(
                backend.getAllBookings().stream()
                        .sorted((b1, b2) -> b2.getBookingDateTime().compareTo(b1.getBookingDateTime()))
                        .limit(5)
                        .collect(Collectors.toList())
        );
        activityTable.setItems(recentBookings);
    }

    @FXML
    private void handleViewReports() {
        AdminPanelController.getInstance().loadContent("GenerateReports.fxml", null);
    }

    @FXML
    private void handleManageTrains() {
        AdminPanelController.getInstance().loadContent("TrainManagement.fxml", null);
    }

    @FXML
    private void handleViewUsers() {
        AdminPanelController.getInstance().loadContent("UserManagement.fxml", null);
    }

    @FXML
    private void handleSettings() {
        AdminPanelController.getInstance().loadContent("Settings.fxml", null);
    }
}
