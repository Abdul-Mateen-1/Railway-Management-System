package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RevenueReportController {

    @FXML private Label totalRevenueLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> pnrColumn;
    @FXML private TableColumn<Booking, String> trainColumn;
    @FXML private TableColumn<Booking, String> dateColumn;
    @FXML private TableColumn<Booking, Number> amountColumn;

    private final BackendService backend = BackendService.getInstance();

    @FXML
    private void initialize() {
        setupTable();
        loadReportData();
    }

    private void setupTable() {
        pnrColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        trainColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrainName()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTravelDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        ));
        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalAmount()));
    }

    private void loadReportData() {
        List<Booking> confirmedBookings = backend.getAllBookings().stream()
                .filter(b -> "Confirmed".equalsIgnoreCase(b.getStatus()))
                .collect(Collectors.toList());

        double totalRevenue = confirmedBookings.stream()
                .mapToDouble(Booking::getTotalAmount)
                .sum();

        totalRevenueLabel.setText(String.format("PKR %,.2f", totalRevenue));
        totalBookingsLabel.setText(String.format("From %d confirmed bookings", confirmedBookings.size()));

        ObservableList<Booking> bookingData = FXCollections.observableArrayList(confirmedBookings);
        bookingsTable.setItems(bookingData);
    }

    @FXML
    private void handleGoBack() {
        AdminPanelController.getInstance().loadContent("GenerateReports.fxml", null);
    }
}
