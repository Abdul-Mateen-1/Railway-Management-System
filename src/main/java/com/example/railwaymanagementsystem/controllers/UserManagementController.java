package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.User;
import com.example.railwaymanagementsystem.services.BackendRepository;
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

    private final BackendRepository repo = BackendRepository.getInstance();
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
        userData = repo.getUsers();
        filteredData = new FilteredList<>(userData, p -> true);
        userTable.setItems(filteredData);
        updateCountLabel();

        // Add a listener to the underlying data to update the count
        userData.addListener((javafx.collections.ListChangeListener.Change<? extends User> c) -> {
            updateCountLabel();
        });
    }

    private void setupTable() {
        // Add status column manually
        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellValueFactory(cellData -> {
            // This is a placeholder. You might want a real status property on your User model.
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
            String searchText = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
            boolean matchesSearch = searchText.isEmpty() ||
                    (user.getName() != null && user.getName().toLowerCase().contains(searchText)) ||
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchText));

            String roleFilter = roleFilterCombo.getValue();
            boolean matchesRole = "All Roles".equals(roleFilter) ||
                    (user.getRole() != null && user.getRole().equals(roleFilter));

            // Placeholder for status filter
            // String statusFilter = statusFilterCombo.getValue();
            // boolean matchesStatus = "All Status".equals(statusFilter) || "Active".equals(statusFilter);

            return matchesSearch && matchesRole;
        });

        updateCountLabel();
    }

    private void updateCountLabel() {
        countLabel.setText(String.format("Showing %d of %d users",
                filteredData.size(), userData.size()));
    }

    @FXML
    private void handleAddUser() {
        // This is a placeholder. A proper "add user" dialog should be implemented.
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add User");
        dialog.setHeaderText("Add a new user (demo)");
        dialog.setContentText("User Name:");

        dialog.showAndWait().ifPresent(name -> {
            User newUser = new User(
                    repo.nextUserId(),
                    name,
                    name.toLowerCase().replace(" ", ".") + "@example.com",
                    "+92 300 0000000",
                    "Passenger",
                    "password" // Default password for demo
            );
            repo.addUser(newUser);
            showSuccess("User added successfully!");
        });
    }

    private void handleEditUser(User user) {
        // This is a placeholder. A proper "edit user" dialog should be implemented.
        TextInputDialog dialog = new TextInputDialog(user.getName());
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user: " + user.getEmail());
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            user.setName(name);
            repo.updateUser(user);
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
                // This needs a proper implementation in the backend
                // For now, we remove from the observable list
                userData.remove(user);
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
