package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.services.AppSession;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controller for Payment Screen
 */
public class PaymentController {

    @FXML private VBox bookingsContainer;
    @FXML private Label noBookingsLabel;
    @FXML private VBox paymentDetailsContainer;
    @FXML private Label bookingInfoLabel;
    @FXML private Label trainInfoLabel;
    @FXML private Label routeInfoLabel;
    @FXML private Label dateInfoLabel;
    @FXML private Label seatsInfoLabel;
    @FXML private Label amountInfoLabel;
    @FXML private RadioButton cashOnDeliveryRadio;
    @FXML private RadioButton cardRadio;
    @FXML private VBox cardDetailsContainer;
    @FXML private TextField cardNumberField;
    @FXML private TextField cardNameField;
    @FXML private TextField expiryField;
    @FXML private TextField cvvField;
    @FXML private Button proceedPaymentButton;
    @FXML private Button cancelButton;

    private final BackendService backend = BackendService.getInstance();
    private final AppSession session = AppSession.getInstance();
    private Booking selectedBooking;
    private final ToggleGroup paymentMethodGroup = new ToggleGroup();

    @FXML
    private void initialize() {
        cashOnDeliveryRadio.setToggleGroup(paymentMethodGroup);
        cardRadio.setToggleGroup(paymentMethodGroup);
        cashOnDeliveryRadio.setSelected(true);
        
        cardRadio.selectedProperty().addListener((obs, old, selected) -> {
            cardDetailsContainer.setVisible(selected);
            cardDetailsContainer.setManaged(selected);
        });
        cardDetailsContainer.setVisible(false);
        cardDetailsContainer.setManaged(false);

        loadPendingBookings();
    }

    private void loadPendingBookings() {
        Optional<String> userIdOpt = session.getCurrentUser().map(u -> u.getId());
        if (userIdOpt.isEmpty()) {
            showError("Please log in to view payments");
            return;
        }

        List<Booking> pendingBookings = backend.getPendingPaymentsForUser(userIdOpt.get());
        
        if (pendingBookings.isEmpty()) {
            noBookingsLabel.setVisible(true);
            bookingsContainer.setVisible(false);
            paymentDetailsContainer.setVisible(false);
            return;
        }

        noBookingsLabel.setVisible(false);
        bookingsContainer.setVisible(false);
        bookingsContainer.getChildren().clear();

        for (Booking booking : pendingBookings) {
            VBox bookingCard = createBookingCard(booking);
            bookingsContainer.getChildren().add(bookingCard);
        }

        bookingsContainer.setVisible(true);
        
        // Auto-select first booking if available
        if (!pendingBookings.isEmpty()) {
            selectBooking(pendingBookings.get(0));
        }
    }

    private VBox createBookingCard(Booking booking) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15px; " +
                "-fx-border-color: #e5e7eb; -fx-border-width: 1px; " +
                "-fx-border-radius: 8px; -fx-background-radius: 8px; " +
                "-fx-cursor: hand;");
        card.setOnMouseClicked(e -> selectBooking(booking));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label pnrLabel = new Label("PNR: " + booking.getId());
        pnrLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label trainLabel = new Label(booking.getTrainNumber() + " - " + booking.getTrainName());
        trainLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label amountLabel = new Label("PKR " + String.format("%,.0f", booking.getTotalAmount()));
        amountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1e6b47;");

        header.getChildren().addAll(pnrLabel, trainLabel, spacer, amountLabel);

        Label routeLabel = new Label(booking.getFromStation() + " → " + booking.getToStation());
        routeLabel.setStyle("-fx-font-size: 12px;");

        Label dateLabel = new Label("Travel Date: " + booking.getTravelDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        card.getChildren().addAll(header, new Separator(), routeLabel, dateLabel);
        return card;
    }

    private void selectBooking(Booking booking) {
        selectedBooking = booking;
        paymentDetailsContainer.setVisible(true);

        bookingInfoLabel.setText("PNR: " + booking.getId());
        trainInfoLabel.setText(booking.getTrainNumber() + " - " + booking.getTrainName());
        routeInfoLabel.setText(booking.getFromStation() + " → " + booking.getToStation());
        dateInfoLabel.setText(booking.getTravelDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        seatsInfoLabel.setText(booking.getNumberOfSeats() + " seat(s) - " + booking.getSeatClass());
        amountInfoLabel.setText("PKR " + String.format("%,.0f", booking.getTotalAmount()));

        // Reset payment method
        cashOnDeliveryRadio.setSelected(true);
        cardDetailsContainer.setVisible(false);
        cardDetailsContainer.setManaged(false);
        clearCardFields();
    }

    @FXML
    private void handleProceedPayment() {
        if (selectedBooking == null) {
            showError("Please select a booking to pay");
            return;
        }

        RadioButton selected = (RadioButton) paymentMethodGroup.getSelectedToggle();
        if (selected == null) {
            showError("Please select a payment method");
            return;
        }

        String paymentMethod = selected == cashOnDeliveryRadio ? "Cash on Delivery" : "Card";

        if ("Card".equals(paymentMethod)) {
            if (!validateCardDetails()) {
                return;
            }
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Payment");
        confirm.setHeaderText("Proceed with payment?");
        confirm.setContentText(
                "Booking: " + selectedBooking.getId() + "\n" +
                "Amount: PKR " + String.format("%,.0f", selectedBooking.getTotalAmount()) + "\n" +
                "Payment Method: " + paymentMethod
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = backend.processPayment(selectedBooking.getId(), paymentMethod);
                if (success) {
                    showSuccess("Payment processed successfully!\n\n" +
                            "PNR: " + selectedBooking.getId() + "\n" +
                            "Your ticket is now confirmed.");
                    clearCardFields();
                    loadPendingBookings();
                } else {
                    showError("Payment failed. Please try again.");
                }
            }
        });
    }

    private boolean validateCardDetails() {
        if (cardNumberField.getText().trim().length() < 16) {
            showError("Please enter a valid 16-digit card number");
            return false;
        }
        if (cardNameField.getText().trim().isEmpty()) {
            showError("Please enter cardholder name");
            return false;
        }
        if (expiryField.getText().trim().length() < 5) {
            showError("Please enter expiry date (MM/YY)");
            return false;
        }
        if (cvvField.getText().trim().length() < 3) {
            showError("Please enter CVV");
            return false;
        }
        return true;
    }

    private void clearCardFields() {
        cardNumberField.clear();
        cardNameField.clear();
        expiryField.clear();
        cvvField.clear();
    }

    @FXML
    private void handleCancel() {
        selectedBooking = null;
        paymentDetailsContainer.setVisible(false);
        clearCardFields();
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

