package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.RailSafarApp;
import com.example.railwaymanagementsystem.models.User;
import com.example.railwaymanagementsystem.services.AppSession;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the Sign Up Screen
 */
public class SignUpController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;

    @FXML
    private void initialize() {
        // Setup enter key navigation
        nameField.setOnAction(e -> emailField.requestFocus());
        emailField.setOnAction(e -> phoneField.requestFocus());
        phoneField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> handleSignUp());
    }

    private final BackendService backend = BackendService.getInstance();
    private final AppSession session = AppSession.getInstance();

    @FXML
    private void handleSignUp() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (!email.contains("@")) {
            showError("Please enter a valid email address");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        User candidate = new User("", name, email, phone, "passenger", password);
        backend.register(candidate).ifPresentOrElse(user -> {
            //session.setCurrentUser(user);
            showSuccess("Account created. Welcome " + user.getName() + "!");
            try {
                RailSafarApp.showLoginScreen("Passenger");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error opening passenger panel: " + e.getMessage());
            }
        }, () -> showError("Email already registered"));
    }

    @FXML
    private void handleLogin() {
        try {
            RailSafarApp.showLoginScreen("passenger");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error navigating to login: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        handleLogin();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Sign Up Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign Up");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
