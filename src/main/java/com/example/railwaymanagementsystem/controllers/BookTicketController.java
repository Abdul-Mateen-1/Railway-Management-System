package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.models.Schedule;
import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.services.AppSession;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for Book Ticket Screen
 */
public class BookTicketController {

    @FXML private ComboBox<String> fromStationCombo;
    @FXML private ComboBox<String> toStationCombo;
    @FXML private DatePicker journeyDatePicker;
    @FXML private Spinner<Integer> passengersSpinner;
    @FXML private VBox resultsContainer;
    @FXML private VBox trainsList;

    private final BackendService backend = BackendService.getInstance();
    private final AppSession session = AppSession.getInstance();

    @FXML
    private void initialize() {
        // Set default values
        fromStationCombo.setValue("Karachi");
        toStationCombo.setValue("Lahore");
        journeyDatePicker.setValue(LocalDate.now().plusDays(1));

        // Setup spinner
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 1);
        passengersSpinner.setValueFactory(valueFactory);
        passengersSpinner.setEditable(true);
    }

    @FXML
    private void handleSearchTrains() {
        String from = fromStationCombo.getValue();
        String to = toStationCombo.getValue();
        LocalDate date = journeyDatePicker.getValue();

        if (from == null || to == null || date == null) {
            showError("Please fill in all fields");
            return;
        }

        if (from.equals(to)) {
            showError("From and To stations cannot be the same");
            return;
        }

        if (date.isBefore(LocalDate.now())) {
            showError("Journey date must be in the future");
            return;
        }

        // Show results
        displaySearchResults(from, to, date);
    }

    private void displaySearchResults(String from, String to, LocalDate date) {
        trainsList.getChildren().clear();
        resultsContainer.setVisible(true);

        List<Train> trains = backend.searchTrains(from, to);
        if (trains.isEmpty()) {
            showError("No trains found between " + from + " and " + to);
            return;
        }

        for (Train train : trains) {
            Schedule schedule = backend.getScheduleForTrain(train.getTrainNumber()).orElse(null);
            VBox trainCard = createTrainCard(train, schedule, from, to);
            trainsList.getChildren().add(trainCard);
        }
    }

    private VBox createTrainCard(Train train, Schedule schedule, String from, String to) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15px; " +
                "-fx-border-color: #e5e7eb; -fx-border-width: 1px; " +
                "-fx-border-radius: 8px; -fx-background-radius: 8px;");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label trainNumber = new Label(train.getTrainNumber());
        trainNumber.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label trainName = new Label(train.getTrainName());
        trainName.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label seatClass = new Label(train.getType());
        seatClass.setStyle("-fx-background-color: #e8f5f0; -fx-padding: 4px 12px; " +
                "-fx-border-radius: 12px; -fx-background-radius: 12px; " +
                "-fx-font-size: 12px; -fx-font-weight: 600;");

        header.getChildren().addAll(trainNumber, trainName, spacer, seatClass);

        // Route and time
        GridPane details = new GridPane();
        details.setHgap(20);
        details.setVgap(8);

        Label fromLabel = new Label("From:");
        fromLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 12px;");
        String departure = schedule == null ? "08:00 AM" : schedule.getDepartureTime();
        String arrival = schedule == null ? "08:00 PM" : schedule.getArrivalTime();
        Label fromValue = new Label(from + " • " + departure);
        fromValue.setStyle("-fx-font-size: 12px;");

        Label toLabel = new Label("To:");
        toLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 12px;");
        Label toValue = new Label(to + " • " + arrival);
        toValue.setStyle("-fx-font-size: 12px;");

        details.add(fromLabel, 0, 0);
        details.add(fromValue, 1, 0);
        details.add(toLabel, 0, 1);
        details.add(toValue, 1, 1);

        // Footer
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER_LEFT);

        Label price = new Label("PKR " + String.format("%,d", getBaseFare(train)));
        price.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1e6b47;");

        Label perPerson = new Label("per person");
        perPerson.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Button bookButton = new Button("📝 Book Now");
        bookButton.setStyle("-fx-background-color: #1e6b47; -fx-text-fill: white; " +
                "-fx-padding: 8px 20px; -fx-background-radius: 6px; " +
                "-fx-cursor: hand;");
        bookButton.setOnAction(e -> handleBookTrain(train, schedule));

        footer.getChildren().addAll(price, perPerson, footerSpacer, bookButton);

        card.getChildren().addAll(header, new Separator(), details, footer);
        return card;
    }

    private void handleBookTrain(Train train, Schedule schedule) {
        int passengers = passengersSpinner.getValue();
        int baseFare = getBaseFare(train);
        int totalAmount = baseFare * passengers;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Booking");
        confirm.setHeaderText("Book ticket for " + train.getTrainNumber() + " - " + train.getTrainName());
        confirm.setContentText(
                "Train: " + train.getTrainNumber() + " - " + train.getTrainName() + "\n" +
                        "Route: " + fromStationCombo.getValue() + " → " + toStationCombo.getValue() + "\n" +
                        "Date: " + journeyDatePicker.getValue() + "\n" +
                        "Passengers: " + passengers + "\n" +
                        "Class: " + train.getType() + "\n\n" +
                        "Total Amount: PKR " + String.format("%,d", totalAmount)
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                session.getCurrentUser().ifPresentOrElse(user -> {
                    Booking booking = backend.bookTicket(user, train, fromStationCombo.getValue(),
                            toStationCombo.getValue(), journeyDatePicker.getValue(), passengers,
                            train.getType(), totalAmount);
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Booking Successful");
                    success.setHeaderText("Ticket booked successfully!");
                    success.setContentText(
                            "PNR: " + booking.getId() + "\n" +
                            "Total Amount: PKR " + String.format("%,d", totalAmount) + "\n\n" +
                            "Please proceed to payment to confirm your booking."
                    );
                    ButtonType proceedButton = new ButtonType("Proceed to Payment");
                    ButtonType laterButton = new ButtonType("Pay Later");
                    success.getButtonTypes().setAll(proceedButton, laterButton);
                    
                    success.showAndWait().ifPresent(buttonType -> {
                        if (buttonType == proceedButton) {
                            navigateToPayment();
                        }
                    });
                }, () -> showError("You need to log in to book a ticket."));
            }
        });
    }

    private int getBaseFare(Train train) {
        return switch (train.getType()) {
            case "Express" -> 3500;
            case "Passenger" -> 2200;
            case "Freight" -> 1500;
            default -> 2500;
        };
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

    private void navigateToPayment() {
        try {
            // Use AppSession to store navigation intent, or directly access PassengerPanelController
            // For now, show a message to navigate manually
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Navigate to Payment");
            info.setHeaderText("Please proceed to Payment");
            info.setContentText("Click on the '💳 Payment' menu item in the sidebar to complete your payment.");
            info.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Please use the Payment menu to complete payment.");
        }
    }
}
