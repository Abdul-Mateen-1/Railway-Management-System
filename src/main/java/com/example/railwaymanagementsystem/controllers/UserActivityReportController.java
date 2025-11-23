package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.models.User;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserActivityReportController {

    @FXML private TableView<UserActivity> userActivityTable;
    @FXML private TableColumn<UserActivity, String> userNameColumn;
    @FXML private TableColumn<UserActivity, String> userEmailColumn;
    @FXML private TableColumn<UserActivity, Number> bookingCountColumn;

    private final BackendService backend = BackendService.getInstance();

    @FXML
    private void initialize() {
        setupTable();
        loadReportData();
    }

    private void setupTable() {
        userNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserName()));
        userEmailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserEmail()));
        bookingCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBookingCount()));
    }

    private void loadReportData() {
        ObservableList<User> users = backend.getUsers();
        ObservableList<Booking> bookings = backend.getAllBookings();

        Map<String, Long> bookingCounts = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getUserId, Collectors.counting()));

        ObservableList<UserActivity> userActivities = FXCollections.observableArrayList();
        for (User user : users) {
            long count = bookingCounts.getOrDefault(user.getId(), 0L);
            userActivities.add(new UserActivity(user.getName(), user.getEmail(), (int) count));
        }

        userActivityTable.setItems(userActivities);
    }

    @FXML
    private void handleGoBack() {
        AdminPanelController.getInstance().loadContent("GenerateReports.fxml", null);
    }

    // Helper class for the report table
    public static class UserActivity {
        private final String userName;
        private final String userEmail;
        private final int bookingCount;

        public UserActivity(String userName, String userEmail, int bookingCount) {
            this.userName = userName;
            this.userEmail = userEmail;
            this.bookingCount = bookingCount;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public int getBookingCount() {
            return bookingCount;
        }
    }
}
