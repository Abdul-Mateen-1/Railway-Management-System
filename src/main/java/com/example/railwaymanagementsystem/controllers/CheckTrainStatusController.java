package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.models.Schedule;
import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Controller for Check Train Status Screen
 */
public class CheckTrainStatusController {

    @FXML private TextField searchField;
    @FXML private VBox statusContainer;
    @FXML private Label trainInfoLabel;
    @FXML private Label routeLabel;
    @FXML private Label scheduledTimeLabel;
    @FXML private Label statusLabel;
    @FXML private Label locationLabel;
    @FXML private Label arrivalLabel;

    private final BackendService backend = BackendService.getInstance();

    @FXML
    private void initialize() {
        // Hide status container initially
        if (statusContainer != null) {
            statusContainer.setVisible(false);
        }
    }

    @FXML
    private void handleCheckStatus() {
        String search = searchField.getText().trim();

        if (search.isEmpty()) {
            showError("Please enter a train number or PNR");
            return;
        }

        findTrainDetails(search.toUpperCase());
    }

    @FXML
    private void checkTrain1UP() {
        searchField.setText("1UP");
        handleCheckStatus();
    }

    @FXML
    private void checkTrain2DN() {
        searchField.setText("2DN");
        handleCheckStatus();
    }

    @FXML
    private void checkTrain3UP() {
        searchField.setText("3UP");
        handleCheckStatus();
    }

    private void findTrainDetails(String query) {
        Optional<Train> trainOpt = backend.getTrainByNumber(query);
        Optional<Schedule> scheduleOpt = Optional.empty();
        Optional<Booking> bookingOpt = Optional.empty();

        if (trainOpt.isEmpty()) {
            bookingOpt = backend.getBookingById(query);
            if (bookingOpt.isPresent()) {
                trainOpt = backend.getTrainByNumber(bookingOpt.get().getTrainNumber());
            }
        }

        if (trainOpt.isEmpty()) {
            showError("No train or booking found for reference: " + query);
            if (statusContainer != null) {
                statusContainer.setVisible(false);
            }
            return;
        }

        Train train = trainOpt.get();
        scheduleOpt = backend.getScheduleForTrain(train.getTrainNumber());
        displayTrainStatus(train, scheduleOpt.orElse(null), bookingOpt.orElse(null));
    }

    private void displayTrainStatus(Train train, Schedule schedule, Booking booking) {
        if (statusContainer != null) {
            statusContainer.setVisible(true);
        }

        if (trainInfoLabel != null) {
            trainInfoLabel.setText(train.getTrainNumber() + " - " + train.getTrainName());
        }

        String fallbackRoute = train.getRoute() == null ? "" : train.getRoute();

        if (routeLabel != null) {
            if (booking != null) {
                routeLabel.setText(booking.getFromStation() + " → " + booking.getToStation());
            } else if (schedule != null) {
                routeLabel.setText(schedule.getRoute());
            } else {
                routeLabel.setText(fallbackRoute);
            }
        }

        if (scheduledTimeLabel != null) {
            if (schedule != null) {
                scheduledTimeLabel.setText(schedule.getDepartureTime() + " - " + schedule.getArrivalTime());
            } else {
                scheduledTimeLabel.setText("Schedule not available");
            }
        }

        if (statusLabel != null) {
            String status = train.getStatus();
            statusLabel.setText(status);
            if (status.toLowerCase().contains("delay")) {
                statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #d97706;");
            } else if (status.toLowerCase().contains("cancel")) {
                statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #dc2626;");
            } else {
                statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #16a34a;");
            }
        }

        if (locationLabel != null) {
            String location = switch (train.getStatus()) {
                case "Delayed" -> "Train departed but running behind schedule";
                case "Cancelled" -> "Train did not depart today";
                default -> "On route between " + extractStation(train.getRoute(), true) + " and " +
                        extractStation(train.getRoute(), false);
            };
            locationLabel.setText(location);
        }

        if (arrivalLabel != null) {
            if (schedule != null) {
                arrivalLabel.setText("Expected arrival: " + schedule.getArrivalTime() +
                        (train.getStatus().toLowerCase().contains("delay") ? " (may be delayed)" : ""));
            } else {
                arrivalLabel.setText("Arrival information not available");
            }
        }
    }

    private String extractStation(String route, boolean first) {
        if (route == null) {
            return "";
        }
        String[] parts = route.split("-");
        if (parts.length == 0) {
            return "";
        }
        return first ? parts[0].trim() : parts[parts.length - 1].trim();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}