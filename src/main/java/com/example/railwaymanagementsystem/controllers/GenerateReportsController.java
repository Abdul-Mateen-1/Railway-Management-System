package com.example.railwaymanagementsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

/**
 * Controller for Generate Reports Screen
 */
public class GenerateReportsController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> formatCombo;

    @FXML
    private void initialize() {
        // Set default dates
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());

        // Set default values
        categoryCombo.setValue("All Categories");
        formatCombo.setValue("PDF");
    }

    @FXML
    private void handleRevenueReport() {
        showReportGeneration("Revenue Report", "Monthly revenue analysis report generated successfully!");
    }

    @FXML
    private void handlePerformanceReport() {
        showReportGeneration("Train Performance Report", "Performance analysis report generated successfully!");
    }

    @FXML
    private void handlePassengerReport() {
        showReportGeneration("Passenger Statistics Report", "Passenger data report generated successfully!");
    }

    @FXML
    private void handleRouteReport() {
        showReportGeneration("Route Analysis Report", "Route profitability report generated successfully!");
    }

    @FXML
    private void handleCustomReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showError("Please select both start and end dates");
            return;
        }

        if (startDate.isAfter(endDate)) {
            showError("Start date must be before end date");
            return;
        }

        String category = categoryCombo.getValue();
        String format = formatCombo.getValue();

        showReportGeneration("Custom Report",
                String.format("Custom %s report (%s to %s) generated as %s",
                        category, startDate, endDate, format));
    }

    private void showReportGeneration(String reportType, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Generated");
        alert.setHeaderText(reportType);
        alert.setContentText(message + "\n\nIn production, this would generate and download a real file.");
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
