package com.example.railwaymanagementsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for Settings Screen
 */
public class SettingsController {

    @FXML private TextField systemNameField;
    @FXML private ComboBox<String> languageCombo;
    @FXML private ComboBox<String> timezoneCombo;
    @FXML private ComboBox<String> dateFormatCombo;
    @FXML private Spinner<Integer> maxBookingDaysSpinner;
    @FXML private Spinner<Integer> maxSeatsSpinner;
    @FXML private Spinner<Integer> cancellationDeadlineSpinner;
    @FXML private ComboBox<String> defaultSeatClassCombo;
    @FXML private CheckBox emailNotificationsCheck;
    @FXML private CheckBox smsNotificationsCheck;
    @FXML private CheckBox delayNotificationsCheck;
    @FXML private CheckBox cancellationNotificationsCheck;
    @FXML private TextField dbHostField;
    @FXML private TextField dbPortField;
    @FXML private TextField dbNameField;

    @FXML
    private void initialize() {
        // Set default values
        languageCombo.setValue("English");
        timezoneCombo.setValue("PKT (UTC+5)");
        dateFormatCombo.setValue("DD/MM/YYYY");
        defaultSeatClassCombo.setValue("Economy");

        // Setup spinners
        setupSpinner(maxBookingDaysSpinner, 1, 365, 60);
        setupSpinner(maxSeatsSpinner, 1, 20, 6);
        setupSpinner(cancellationDeadlineSpinner, 1, 72, 24);
    }

    private void setupSpinner(Spinner<Integer> spinner, int min, int max, int initial) {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initial);
        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true);
    }

    @FXML
    private void handleSaveSettings() {
        // In production, save to database or configuration file
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings Saved");
        alert.setHeaderText(null);
        alert.setContentText("All settings have been saved successfully!\n\n" +
                "System Name: " + systemNameField.getText() + "\n" +
                "Language: " + languageCombo.getValue() + "\n" +
                "Max Booking Days: " + maxBookingDaysSpinner.getValue() + "\n" +
                "Email Notifications: " + (emailNotificationsCheck.isSelected() ? "Enabled" : "Disabled"));
        alert.showAndWait();
    }

    @FXML
    private void handleResetSettings() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset Settings");
        confirm.setHeaderText("Reset all settings to default values?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                systemNameField.setText("Rail Safar");
                languageCombo.setValue("English");
                timezoneCombo.setValue("PKT (UTC+5)");
                dateFormatCombo.setValue("DD/MM/YYYY");
                maxBookingDaysSpinner.getValueFactory().setValue(60);
                maxSeatsSpinner.getValueFactory().setValue(6);
                cancellationDeadlineSpinner.getValueFactory().setValue(24);
                defaultSeatClassCombo.setValue("Economy");
                emailNotificationsCheck.setSelected(true);
                smsNotificationsCheck.setSelected(true);
                delayNotificationsCheck.setSelected(true);
                cancellationNotificationsCheck.setSelected(true);
                dbHostField.setText("localhost");
                dbPortField.setText("5432");
                dbNameField.setText("railsafar");

                showSuccess("Settings reset to defaults successfully!");
            }
        });
    }

    @FXML
    private void handleTestConnection() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Database Connection");
        alert.setHeaderText("Testing connection...");
        alert.setContentText("Connection to " + dbHostField.getText() + ":" +
                dbPortField.getText() + "/" + dbNameField.getText() +
                "\n\nStatus: ✅ Connection successful!\n\n" +
                "(In production, this would test actual database connectivity)");
        alert.showAndWait();
    }

    @FXML
    private void handleBackupDatabase() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Database Backup");
        alert.setHeaderText("Creating backup...");
        alert.setContentText("Database backup created successfully!\n\n" +
                "Backup file: railsafar_backup_" + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql\n\n" +
                "(In production, this would create an actual backup file)");
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
