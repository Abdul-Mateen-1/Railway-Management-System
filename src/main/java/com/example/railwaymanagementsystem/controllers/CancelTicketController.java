package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Controller for Cancel Ticket Screen
 */
public class CancelTicketController {

    @FXML private TextField pnrField;
    @FXML private TableView<Booking> bookingsTable;

    private ObservableList<Booking> bookingsData;

    @FXML
    private void initialize() {
        initializeData();
        setupTable();
    }

    private void initializeData() {
        bookingsData = FXCollections.observableArrayList(
                createBooking("PNR123456", "1UP", "Karachi Express", "Karachi", "Lahore",
                        LocalDate.now().plusDays(5), 2, 5000.0),
                createBooking("PNR789012", "2DN", "Lahore Express", "Lahore", "Karachi",
                        LocalDate.now().plusDays(10), 1, 3000.0),
                createBooking("PNR345678", "3UP", "Green Line", "Islamabad", "Multan",
                        LocalDate.now().plusDays(3), 3, 6600.0)
        );

        bookingsTable.setItems(bookingsData);
    }

    private Booking createBooking(String id, String trainNum, String trainName,
                                  String from, String to, LocalDate date, int seats, double amount) {
        return new Booking(id, "USER001", trainNum, trainNum, trainName,
                from, to, date, seats, "Economy", amount, "Confirmed", LocalDateTime.now());
    }

    private void setupTable() {
        TableColumn<Booking, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(120);

        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button cancelBtn = new Button("❌ Cancel");
            private final HBox pane = new HBox(cancelBtn);

            {
                pane.setAlignment(Pos.CENTER);
                cancelBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; " +
                        "-fx-padding: 6px 12px; -fx-cursor: hand; -fx-background-radius: 6px;");

                cancelBtn.setOnAction(e -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    handleCancelBooking(booking);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        bookingsTable.getColumns().add(actionsCol);
    }

    @FXML
    private void handleSearchPNR() {
        String pnr = pnrField.getText().trim();

        if (pnr.isEmpty()) {
            showError("Please enter a PNR");
            return;
        }

        // Search in bookings
        boolean found = false;
        for (Booking booking : bookingsData) {
            if (booking.getId().equalsIgnoreCase(pnr)) {
                bookingsTable.getSelectionModel().select(booking);
                bookingsTable.scrollTo(booking);
                found = true;
                break;
            }
        }

        if (!found) {
            showError("PNR not found");
        }
    }

    private void handleCancelBooking(Booking booking) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Booking");
        confirm.setHeaderText("Cancel booking " + booking.getId() + "?");
        confirm.setContentText(
                "Train: " + booking.getTrainName() + "\n" +
                        "Route: " + booking.getFromStation() + " → " + booking.getToStation() + "\n" +
                        "Date: " + booking.getTravelDate() + "\n" +
                        "Amount: PKR " + String.format("%,.0f", booking.getTotalAmount()) + "\n\n" +
                        "Refund: PKR " + String.format("%,.0f", booking.getTotalAmount() * 0.8) + " (80%)\n\n" +
                        "Are you sure you want to cancel?"
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                bookingsData.remove(booking);
                showSuccess("Booking cancelled successfully!\n\n" +
                        "Refund of PKR " + String.format("%,.0f", booking.getTotalAmount() * 0.8) +
                        " will be processed in 3-5 business days.");
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
