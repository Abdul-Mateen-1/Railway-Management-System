package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.User;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.Optional;

/**
 * Controller for User Management Screen
 */
public class UserManagementController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private TableView<User> userTable;
    @FXML private Label countLabel;

    private final BackendService backend = BackendService.getInstance();
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
        userData = backend.getUsers();
        filteredData = new FilteredList<>(userData, p -> true);
        userTable.setItems(filteredData);
        updateCountLabel();

        userData.addListener((javafx.collections.ListChangeListener.Change<? extends User> c) -> {
            updateCountLabel();
        });
    }

    private void setupTable() {
        userTable.getColumns().clear();

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty("Active")); // Placeholder

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);

            {
                pane.setAlignment(Pos.CENTER);
                editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #dc2626;");

                editBtn.setOnAction(e -> handleEditUser(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> handleDeleteUser(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        userTable.getColumns().addAll(nameCol, emailCol, phoneCol, roleCol, statusCol, actionsCol);
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
        Dialog<User> dialog = createUserDialog(null);
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter the details for the new user.");

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(newUser -> {
            backend.register(newUser);
            showSuccess("User added successfully!");
        });
    }

    private void handleEditUser(User user) {
        Dialog<User> dialog = createUserDialog(user);
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit the details for user: " + user.getName());

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(editedUser -> {
            backend.updateUser(editedUser);
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
                backend.removeUser(user.getId());
                showSuccess("User deleted successfully!");
            }
        });
    }

    private Dialog<User> createUserDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");
        TextField phoneField = new TextField();
        phoneField.setPromptText("+92 300 1234567");
        ComboBox<String> roleComboBox = new ComboBox<>(FXCollections.observableArrayList("Admin", "Passenger"));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter new password");

        if (user != null) {
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone());
            roleComboBox.setValue(user.getRole());
            passwordField.setPromptText("Leave blank to keep current password");
        } else {
            roleComboBox.setValue("Passenger");
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleComboBox, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(passwordField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String password = passwordField.getText();
                if (user != null && (password == null || password.isEmpty())) {
                    password = user.getPassword(); // Keep old password if field is blank
                }
                
                User resultUser = (user != null) ? user : new User();
                resultUser.setName(nameField.getText());
                resultUser.setEmail(emailField.getText());
                resultUser.setPhone(phoneField.getText());
                resultUser.setRole(roleComboBox.getValue());
                resultUser.setPassword(password);

                return resultUser;
            }
            return null;
        });

        return dialog;
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
