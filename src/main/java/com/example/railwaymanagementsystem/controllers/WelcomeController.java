package com.example.railwaymanagementsystem.controllers;
import com.example.railwaymanagementsystem.RailSafarApp;
import javafx.fxml.FXML;

/**
 * Controller for the Welcome/Role Selection Screen
 */
public class WelcomeController {

    @FXML
    private void handlePassengerLogin() {
        try {
            RailSafarApp.showLoginScreen("passenger");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error navigating to login: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdminLogin() {
        try {
            RailSafarApp.showLoginScreen("admin");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error navigating to login: " + e.getMessage());
        }
    }
}
