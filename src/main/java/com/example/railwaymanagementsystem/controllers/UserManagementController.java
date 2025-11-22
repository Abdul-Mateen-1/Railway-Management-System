package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

/**
 * Controller for User Management Screen
 */
public class UserManagementController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private TableView<User> userTable;
    @FXML private Label countLabel;

    private ObservableList<User> userData;
    private FilteredList<User> filteredData;

    @FXML
    private void initialize() {
        initializeData();
        setupTable();
        setupFilters();

        roleFilterCombo.setValue("All Roles");
        statusFilterCombo.setValue("All Status");
    }

    private void initializeData() {
        userData = FXCollections.observableArrayList(
                createUser("1", "Ahmed Khan", "ahmed.khan@example.com", "+92 300 1234567", "Admin"),
                createUser("2", "Fatima Ali", "fatima.ali@example.com", "+92 301 2345678", "Passenger"),
                createUser("3", "Hassan Raza", "hassan.raza@example.com", "+92 302 3456789", "Passenger"),
                createUser("4", "Ayesha Malik", "ayesha.malik@example.com", "+92 303 4567890", "Passenger"),
                createUser("5", "Usman Sheikh", "usman.sheikh@example.com", "+92 304 5678901", "Admin"),
                createUser("6", "Zainab Hussain", "zainab.hussain@example.com", "+92 305 6789012", "Passenger"),
                createUser("7", "Ali Akbar", "ali.akbar@example.com", "+92 306 7890123", "Passenger"),
                createUser("8", "Mariam Shahid", "mariam.shahid@example.com", "+92 307 8901234", "Passenger"),
                createUser("9", "Bilal Ahmad", "bilal.ahmad@example.com", "+92 308 9012345", "Admin"),
                createUser("10", "Sana Tariq", "sana.tariq@example.com", "+92 309 0123456", "Passenger")
        );

        filteredData = new FilteredList<>(userData, p -> true);
        userTable.setItems(filteredData);
        updateCountLabel();
    }

    private User createUser(String id, String name, String email, String phone, String role) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        return user;
    }

    private void setupTable() {
        // Add status column manually
        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty("Active");
        });

        // Add actions column
        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(120);

        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);

            {
                pane.setAlignment(Pos.CENTER);
                editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #dc2626;");

                editBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleEditUser(user);
                });

                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        userTable.getColumns().add(statusCol);
        userTable.getColumns().add(actionsCol);
    }

    private void setupFilters() {
        searchField.textProperty().addListener((obs, old, newVal) -> applyFilters());
        roleFilterCombo.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        statusFilterCombo.valueProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private void applyFilters() {
        filteredData.setPredicate(user -> {
            String searchText = searchField.getText().toLowerCase();
            boolean matchesSearch = searchText.isEmpty() ||
                    user.getName().toLowerCase().contains(searchText) ||
                    user.getEmail().toLowerCase().contains(searchText);

            String roleFilter = roleFilterCombo.getValue();
            boolean matchesRole = "All Roles".equals(roleFilter) ||
                    user.getRole().equals(roleFilter);

            return matchesSearch && matchesRole;
        });

        updateCountLabel();
    }

    private void updateCountLabel() {
        countLabel.setText("Showing " + filteredData.size() + " users");
    }

    @FXML
    private void handleAddUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add User");
        dialog.setHeaderText("Add a new user");
        dialog.setContentText("User Name:");

        dialog.showAndWait().ifPresent(name -> {
            String id = String.valueOf(userData.size() + 1);
            User newUser = createUser(id, name, name.toLowerCase().replace(" ", ".") + "@example.com",
                    "+92 300 0000000", "Passenger");
            userData.add(newUser);
            updateCountLabel();
            showSuccess("User added successfully!");
        });
    }

    private void handleEditUser(User user) {
        TextInputDialog dialog = new TextInputDialog(user.getName());
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user: " + user.getEmail());
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            user.setName(name);
            userTable.refresh();
            showSuccess("User updated successfully!");
        });
    }

    private void handleDeleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Delete user " + user.getName() + "?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userData.remove(user);
                updateCountLabel();
                showSuccess("User deleted successfully!");
            }
        });
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
