package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.services.AppSession;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Controller for Cancel Ticket Screen
 */
public class CancelTicketController {

    @FXML private TextField pnrField;
    @FXML private TableView<Booking> bookingsTable;

    private final BackendService backend = BackendService.getInstance();
    private final AppSession session = AppSession.getInstance();
    private ObservableList<Booking> allBookings;
    private FilteredList<Booking> activeUserBookings;

    @FXML
    private void initialize() {
        initializeData();
        setupTable();
    }

    private void initializeData() {
        allBookings = backend.getAllBookings();
        Optional<String> userIdOpt = session.getCurrentUser().map(u -> u.getId());

        if (userIdOpt.isPresent()) {
            String userId = userIdOpt.get();
            Predicate<Booking> isCancellable = booking ->
                    booking.getUserId().equals(userId) && "Confirmed".equalsIgnoreCase(booking.getStatus());
            
            activeUserBookings = new FilteredList<>(allBookings, isCancellable);
        } else {
            activeUserBookings = new FilteredList<>(allBookings, booking -> false);
            showError("Please log in to see your bookings.");
        }

        bookingsTable.setItems(activeUserBookings);

        allBookings.addListener((javafx.collections.ListChangeListener.Change<? extends Booking> c) -> {
            activeUserBookings.setPredicate(activeUserBookings.getPredicate());
        });
    }

    private void setupTable() {
        bookingsTable.getColumns().clear();

        TableColumn<Booking, String> pnrCol = new TableColumn<>("PNR");
        pnrCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));

        TableColumn<Booking, String> trainCol = new TableColumn<>("Train");
        trainCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrainName()));

        TableColumn<Booking, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTravelDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        ));

        TableColumn<Booking, String> routeCol = new TableColumn<>("Route");
        routeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFromStation() + " → " + cellData.getValue().getToStation()
        ));

        TableColumn<Booking, Integer> seatsCol = new TableColumn<>("Seats");
        seatsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getNumberOfSeats()).asObject());

        TableColumn<Booking, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTotalAmount()).asObject());

        TableColumn<Booking, Void> actionsCol = new TableColumn<>("Actions");
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

        bookingsTable.getColumns().addAll(pnrCol, trainCol, dateCol, routeCol, seatsCol, amountCol, actionsCol);
    }

    @FXML
    private void handleSearchPNR() {
        String pnr = pnrField.getText().trim();

        if (pnr.isEmpty()) {
            showError("Please enter a PNR");
            return;
        }

        Optional<Booking> foundBooking = activeUserBookings.stream()
                .filter(booking -> booking.getId().equalsIgnoreCase(pnr))
                .findFirst();

        if (foundBooking.isPresent()) {
            bookingsTable.getSelectionModel().select(foundBooking.get());
            bookingsTable.scrollTo(foundBooking.get());
        } else {
            showError("PNR not found in your active bookings.");
        }
    }

    private void handleCancelBooking(Booking booking) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Booking");
        confirm.setHeaderText("Cancel booking " + booking.getId() + "?");
        confirm.setContentText(
                "Train: " + booking.getTrainName() + "\n" +
                        "Route: " + booking.getFromStation() + " → " + booking.getToStation() + "\n" +
                        "Date: " + booking.getTravelDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + "\n" +
                        "Amount: PKR " + String.format("%,.0f", booking.getTotalAmount()) + "\n\n" +
                        "Refund: PKR " + String.format("%,.0f", booking.getTotalAmount() * 0.8) + " (80%)\n\n" +
                        "Are you sure you want to cancel?"
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                booking.setStatus("Cancelled");
                if (backend.updateBooking(booking)) {
                    showSuccess("Booking cancelled successfully!\n\n" +
                            "Refund of PKR " + String.format("%,.0f", booking.getTotalAmount() * 0.8) +
                            " will be processed in 3-5 business days.");
                } else {
                    showError("Failed to cancel the booking. Please try again.");
                    booking.setStatus("Confirmed");
                }
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
