package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.RailSafarApp;
import com.example.railwaymanagementsystem.models.User;
import com.example.railwaymanagementsystem.services.AppSession;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the Login Screen
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label roleLabel;
    @FXML private Label signupPrompt;
    @FXML private Button signupButton;
    @FXML private Label adminNote;

    private String currentRole = "passenger";
    private final BackendService backend = BackendService.getInstance();
    private final AppSession session = AppSession.getInstance();

    /**
     * Set the role for this login screen
     */
    public void setRole(String role) {
        this.currentRole = role;
        updateUI();
    }

    /**
     * Update UI based on role
     */
    private void updateUI() {
        if ("admin".equals(currentRole)) {
            roleLabel.setText("Sign in as Administrator");
            signupPrompt.setVisible(false);
            signupPrompt.setManaged(false);
            signupButton.setVisible(false);
            signupButton.setManaged(false);
            adminNote.setVisible(true);
            adminNote.setManaged(true);
        } else {
            roleLabel.setText("Sign in as Passenger");
            signupPrompt.setVisible(true);
            signupPrompt.setManaged(true);
            signupButton.setVisible(true);
            signupButton.setManaged(true);
            adminNote.setVisible(false);
            adminNote.setManaged(false);
        }
    }

    @FXML
    private void initialize() {
        // Setup enter key to submit
        emailField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        backend.authenticate(email, password, currentRole)
                .ifPresentOrElse(user -> {
                    session.setCurrentUser(user);
                    try {
                        if ("admin".equals(currentRole)) {
                            RailSafarApp.showAdminPanel();
                        } else {
                            RailSafarApp.showPassengerPanel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError("Error loading panel: " + e.getMessage());
                    }
                }, () -> showError("Invalid credentials for " + currentRole));
    }

    @FXML
    private void handleSignUp() {
        try {
            RailSafarApp.showSignUpScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading sign up: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            RailSafarApp.showWelcomeScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error going back: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
