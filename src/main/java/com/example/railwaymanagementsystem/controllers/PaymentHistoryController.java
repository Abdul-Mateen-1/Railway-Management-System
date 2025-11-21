package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.services.AppSession;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for Payment History Screen
 */
public class PaymentHistoryController {

    @FXML private Label totalSpentLabel;
    @FXML private Label thisMonthLabel;
    @FXML private Label totalTripsLabel;
    @FXML private ComboBox<String> filterCombo;
    @FXML private TableView<PaymentRecord> paymentTable;
    @FXML private TableColumn<PaymentRecord, String> dateColumn;
    @FXML private TableColumn<PaymentRecord, String> pnrColumn;
    @FXML private TableColumn<PaymentRecord, String> trainColumn;
    @FXML private TableColumn<PaymentRecord, String> routeColumn;
    @FXML private TableColumn<PaymentRecord, String> amountColumn;
    @FXML private TableColumn<PaymentRecord, String> paymentModeColumn;
    @FXML private TableColumn<PaymentRecord, String> statusColumn;

    private final BackendService backend = BackendService.getInstance();
    private final AppSession session = AppSession.getInstance();
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    @FXML
    private void initialize() {
        if (filterCombo != null) {
            filterCombo.setValue("All Transactions");
        }
        setupTableColumns();
        loadPaymentHistory();
    }

    private void loadPaymentHistory() {
        ObservableList<PaymentRecord> payments = FXCollections.observableArrayList();
        session.getCurrentUser().ifPresent(user -> {
            List<Booking> bookings = backend.getBookingsForUser(user.getId());
            // Only show paid bookings
            payments.addAll(bookings.stream()
                    .filter(booking -> "Paid".equals(booking.getPaymentStatus()))
                    .map(booking -> new PaymentRecord(
                            booking.getBookingDateTime().format(DATE_FORMATTER),
                            booking.getId(),
                            booking.getTrainNumber() + " - " + booking.getTrainName(),
                            booking.getFromStation() + " → " + booking.getToStation(),
                            "PKR " + String.format("%,.0f", booking.getTotalAmount()),
                            booking.getPaymentMethod() != null && !booking.getPaymentMethod().isEmpty() 
                                    ? booking.getPaymentMethod() : "N/A",
                            booking.getPaymentStatus()
                    )).toList());

            totalSpentLabel.setText("PKR " + String.format("%,.0f",
                    bookings.stream().mapToDouble(Booking::getTotalAmount).sum()));
            totalTripsLabel.setText(String.valueOf(bookings.size()));
            long thisMonth = bookings.stream()
                    .filter(b -> b.getBookingDateTime().getMonthValue() == LocalDate.now().getMonthValue())
                    .count();
            thisMonthLabel.setText(String.valueOf(thisMonth));
        });

        if (paymentTable != null) {
            paymentTable.setItems(payments);
        }
    }

    private void setupTableColumns() {
        // Option 1: If you have individual column references in FXML
        if (dateColumn != null) {
            dateColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getDate()));
        }
        if (pnrColumn != null) {
            pnrColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getPnr()));
        }
        if (trainColumn != null) {
            trainColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getTrain()));
        }
        if (routeColumn != null) {
            routeColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getRoute()));
        }
        if (amountColumn != null) {
            amountColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getAmount()));
        }
        if (paymentModeColumn != null) {
            paymentModeColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getPaymentMode()));
        }
        if (statusColumn != null) {
            statusColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getStatus()));
        }

        // Option 2: If columns are defined in FXML but without fx:id, access by index
        // (Use this only if you don't have individual column references)
        /*
        if (paymentTable != null && !paymentTable.getColumns().isEmpty()) {
            if (paymentTable.getColumns().size() > 0) {
                ((TableColumn<PaymentRecord, String>) paymentTable.getColumns().get(0))
                    .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
            }
            if (paymentTable.getColumns().size() > 1) {
                ((TableColumn<PaymentRecord, String>) paymentTable.getColumns().get(1))
                    .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPnr()));
            }
            if (paymentTable.getColumns().size() > 2) {
                ((TableColumn<PaymentRecord, String>) paymentTable.getColumns().get(2))
                    .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrain()));
            }
            if (paymentTable.getColumns().size() > 3) {
                ((TableColumn<PaymentRecord, String>) paymentTable.getColumns().get(3))
                    .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoute()));
            }
            if (paymentTable.getColumns().size() > 4) {
                ((TableColumn<PaymentRecord, String>) paymentTable.getColumns().get(4))
                    .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAmount()));
            }
            if (paymentTable.getColumns().size() > 5) {
                ((TableColumn<PaymentRecord, String>) paymentTable.getColumns().get(5))
                    .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPaymentMode()));
            }
            if (paymentTable.getColumns().size() > 6) {
                ((TableColumn<PaymentRecord, String>) paymentTable.getColumns().get(6))
                    .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
            }
        }
        */
    }

    @FXML
    private void handleExportPDF() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export to PDF");
        alert.setHeaderText("Payment History Export");
        alert.setContentText("Payment history exported successfully!\n\n" +
                "File: payment_history_" + java.time.LocalDate.now() + ".pdf\n\n" +
                "(In production, this would generate a real PDF file)");
        alert.showAndWait();
    }

    // Payment Record class
    public static class PaymentRecord {
        private final String date;
        private final String pnr;
        private final String train;
        private final String route;
        private final String amount;
        private final String paymentMode;
        private final String status;

        public PaymentRecord(String date, String pnr, String train, String route,
                             String amount, String paymentMode, String status) {
            this.date = date;
            this.pnr = pnr;
            this.train = train;
            this.route = route;
            this.amount = amount;
            this.paymentMode = paymentMode;
            this.status = status;
        }

        public String getDate() { return date; }
        public String getPnr() { return pnr; }
        public String getTrain() { return train; }
        public String getRoute() { return route; }
        public String getAmount() { return amount; }
        public String getPaymentMode() { return paymentMode; }
        public String getStatus() { return status; }
    }
}