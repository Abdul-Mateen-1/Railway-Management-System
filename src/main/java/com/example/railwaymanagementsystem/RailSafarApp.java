package com.example.railwaymanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main Application Entry Point for Rail Safar
 * Railway Admin Panel - Dual Panel System
 */
public class RailSafarApp extends Application {

    private static Stage primaryStage;
    final static int WIDTH = 900;
    final static int HEIGHT = 650;
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Rail Safar - Railway Management System");

        try {
            showWelcomeScreen();
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading application: " + e.getMessage());
        }
    }

    /**
     * Show the welcome/role selection screen
     */
    public static void showWelcomeScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                RailSafarApp.class.getResource("WelcomeScreen.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(
                RailSafarApp.class.getResource("styles.css").toExternalForm()
        );
        primaryStage.setScene(scene);
    }

    /**
     * Show the login screen
     */
    public static void showLoginScreen(String role) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                RailSafarApp.class.getResource("LoginScreen.fxml")
        );
        Parent root = loader.load();

        // Pass role to controller
        com.example.railwaymanagementsystem.controllers.LoginController controller = loader.getController();
        controller.setRole(role);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(
                RailSafarApp.class.getResource("styles.css").toExternalForm()
        );
        primaryStage.setScene(scene);
    }

    /**
     * Show the signup screen
     */
    public static void showSignUpScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                RailSafarApp.class.getResource("SignUpScreen.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(
                RailSafarApp.class.getResource("styles.css").toExternalForm()
        );
        primaryStage.setScene(scene);
    }

    /**
     * Show the passenger panel
     */
    public static void showPassengerPanel() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                RailSafarApp.class.getResource("PassengerPanel.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(
                RailSafarApp.class.getResource("styles.css").toExternalForm()
        );
        primaryStage.setScene(scene);
    }

    /**
     * Show the admin panel
     */
    public static void showAdminPanel() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                RailSafarApp.class.getResource("AdminPanel.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(
                RailSafarApp.class.getResource("styles.css").toExternalForm()
        );
        primaryStage.setScene(scene);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
