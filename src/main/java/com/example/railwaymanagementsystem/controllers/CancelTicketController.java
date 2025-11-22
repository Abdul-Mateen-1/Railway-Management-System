package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Booking;
<<<<<<< HEAD
import com.example.railwaymanagementsystem.services.AppSession;
import com.example.railwaymanagementsystem.services.BackendService;
=======
>>>>>>> 1d6f09b6bebb19db100a8e2b6fdd7dc914d8eec9
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import java.time.LocalDate;
import java.time.LocalDateTime;
<<<<<<< HEAD
import java.util.List;
import java.util.stream.Collectors;
=======
>>>>>>> 1d6f09b6bebb19db100a8e2b6fdd7dc914d8eec9

/**
 * Controller for Cancel Ticket Screen
 */
public class CancelTicketController {

<<<<<<< HEAD
    @FXML private TableView<Booking> bookingsTable;
    
    private ObservableList<Booking> bookingsData;
    private final BackendService backend = BackendService.getInstance();
    private final AppSession session = AppSession.getInstance();

    @FXML
    private void initialize() {
        setupTable();
        loadBookings();
    }

    private void loadBookings() {
        session.getCurrentUser().ifPresentOrElse(user -> {
            List<Booking> bookings = backend.getBookingsForUser(user.getId()).stream()
                    .filter(b -> "Confirmed".equals(b.getStatus()) || "Pending".equals(b.getStatus()))
                    .filter(b -> b.getTravelDate().isAfter(LocalDate.now().minusDays(1)))
                    .collect(Collectors.toList());
            
            bookingsData = FXCollections.observableArrayList(bookings);
            bookingsTable.setItems(bookingsData);
        }, () -> showError("Please log in to view bookings"));
    }

    private void setupTable() {
        // Clear existing columns and configure them programmatically
        bookingsTable.getColumns().clear();
        
        // PNR Column
        TableColumn<Booking, String> pnrCol = new TableColumn<>("PNR");
        pnrCol.setPrefWidth(120);
        pnrCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));
        
        // Train Column
        TableColumn<Booking, String> trainCol = new TableColumn<>("Train");
        trainCol.setPrefWidth(150);
        trainCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getTrainNumber() + " - " + data.getValue().getTrainName()));
        
        // Date Column
        TableColumn<Booking, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(100);
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getTravelDate().toString()));
        
        // Route Column
        TableColumn<Booking, String> routeCol = new TableColumn<>("Route");
        routeCol.setPrefWidth(200);
        routeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getFromStation() + " → " + data.getValue().getToStation()));
        
        // Seats Column
        TableColumn<Booking, String> seatsCol = new TableColumn<>("Seats");
        seatsCol.setPrefWidth(60);
        seatsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            String.valueOf(data.getValue().getNumberOfSeats())));
        
        // Amount Column
        TableColumn<Booking, String> amountCol = new TableColumn<>("Amount");
        amountCol.setPrefWidth(100);
        amountCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            String.format("PKR %,.0f", data.getValue().getTotalAmount())));
        
        // Actions Column
=======
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
>>>>>>> 1d6f09b6bebb19db100a8e2b6fdd7dc914d8eec9
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

<<<<<<< HEAD
        bookingsTable.getColumns().addAll(pnrCol, trainCol, dateCol, routeCol, seatsCol, amountCol, actionsCol);
    }


=======
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
>>>>>>> 1d6f09b6bebb19db100a8e2b6fdd7dc914d8eec9

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
<<<<<<< HEAD
                boolean success = backend.cancelBooking(booking.getId());
                if (success) {
                    bookingsData.remove(booking);
                    showSuccess("Booking cancelled successfully!\n\n" +
                            "Refund of PKR " + String.format("%,.0f", booking.getTotalAmount() * 0.8) +
                            " will be processed in 3-5 business days.");
                } else {
                    showError("Failed to cancel booking. Please try again.");
                }
=======
                bookingsData.remove(booking);
                showSuccess("Booking cancelled successfully!\n\n" +
                        "Refund of PKR " + String.format("%,.0f", booking.getTotalAmount() * 0.8) +
                        " will be processed in 3-5 business days.");
>>>>>>> 1d6f09b6bebb19db100a8e2b6fdd7dc914d8eec9
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
