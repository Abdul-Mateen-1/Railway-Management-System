package com.example.railwaymanagementsystem.controllers;

import javafx.fxml.FXML;

public class GenerateReportsController {

    @FXML
    private void handleRevenueReport() {
        AdminPanelController.getInstance().loadContent("RevenueReport.fxml", null);
    }

    @FXML
    private void handleUserActivityReport() {
        AdminPanelController.getInstance().loadContent("UserActivityReport.fxml", null);
    }

    @FXML
    private void handleTrainPerformanceReport() {
        AdminPanelController.getInstance().loadContent("TrainPerformanceReport.fxml", null);
    }
}
