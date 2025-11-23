package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Map;
import java.util.stream.Collectors;

public class TrainPerformanceReportController {

    @FXML private TableView<TrainPerformance> trainPerformanceTable;
    @FXML private TableColumn<TrainPerformance, String> trainNumberColumn;
    @FXML private TableColumn<TrainPerformance, String> trainNameColumn;
    @FXML private TableColumn<TrainPerformance, Number> bookingCountColumn;

    private final BackendService backend = BackendService.getInstance();

    @FXML
    private void initialize() {
        setupTable();
        loadReportData();
    }

    private void setupTable() {
        trainNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrainNumber()));
        trainNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrainName()));
        bookingCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBookingCount()));
    }

    private void loadReportData() {
        ObservableList<Train> trains = backend.getTrains();
        ObservableList<Booking> bookings = backend.getAllBookings();

        Map<String, Long> bookingCounts = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getTrainId, Collectors.counting()));

        ObservableList<TrainPerformance> trainPerformances = FXCollections.observableArrayList();
        for (Train train : trains) {
            long count = bookingCounts.getOrDefault(train.getId(), 0L);
            trainPerformances.add(new TrainPerformance(train.getTrainNumber(), train.getTrainName(), (int) count));
        }

        trainPerformanceTable.setItems(trainPerformances);
    }

    @FXML
    private void handleGoBack() {
        AdminPanelController.getInstance().loadContent("GenerateReports.fxml", null);
    }

    // Helper class for the report table
    public static class TrainPerformance {
        private final String trainNumber;
        private final String trainName;
        private final int bookingCount;

        public TrainPerformance(String trainNumber, String trainName, int bookingCount) {
            this.trainNumber = trainNumber;
            this.trainName = trainName;
            this.bookingCount = bookingCount;
        }

        public String getTrainNumber() {
            return trainNumber;
        }

        public String getTrainName() {
            return trainName;
        }

        public int getBookingCount() {
            return bookingCount;
        }
    }
}
