package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.User;
import com.example.railwaymanagementsystem.services.AppSession;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller for User Profile Screen
 */
public class UserProfileController {

    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    @FXML private Button editBtn;
    @FXML private Button saveBtn;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField cnicField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField postalCodeField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private final BackendService backend = BackendService.getInstance();
    private final AppSession session = AppSession.getInstance();
    private User currentUser;

    private boolean isEditing = false;

    @FXML
    private void initialize() {
        if (genderCombo != null) {
            genderCombo.getItems().setAll("Male", "Female", "Non-binary", "Unspecified");
        }
        loadUserFromSession();
    }

    private void loadUserFromSession() {
        Optional<User> userOpt = session.getCurrentUser();
        if (userOpt.isEmpty()) {
            setFieldsEditable(false);
            showError("No user session found. Please log in again.");
            return;
        }
        currentUser = userOpt.get();
        populateFields(currentUser);
    }

    private void populateFields(User user) {
        userNameLabel.setText(user.getName());
        userEmailLabel.setText(user.getEmail());
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        cnicField.setText(user.getCnic());
        dobPicker.setValue(user.getDateOfBirth());
        String genderValue = user.getGender() == null || user.getGender().isEmpty() ? "Unspecified" : user.getGender();
        genderCombo.setValue(genderValue);
        addressField.setText(user.getAddress());
        cityField.setText(user.getCity());
        postalCodeField.setText(user.getPostalCode());
        setFieldsEditable(false);
    }

    @FXML
    private void handleEdit() {
        if (currentUser == null) {
            showError("No user selected.");
            return;
        }
        isEditing = true;
        setFieldsEditable(true);
        editBtn.setVisible(false);
        editBtn.setManaged(false);
        saveBtn.setVisible(true);
        saveBtn.setManaged(true);
    }

    @FXML
    private void handleSave() {
        if (currentUser == null) {
            showError("No user session found.");
            return;
        }

        if (nameField.getText().trim().isEmpty()) {
            showError("Name cannot be empty");
            return;
        }

        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            showError("Please enter a valid email");
            return;
        }

        currentUser.setName(nameField.getText().trim());
        currentUser.setEmail(emailField.getText().trim());
        currentUser.setPhone(phoneField.getText().trim());
        currentUser.setCnic(cnicField.getText().trim());
        currentUser.setDateOfBirth(dobPicker.getValue());
        String genderValue = genderCombo.getValue() == null ? "Unspecified" : genderCombo.getValue();
        currentUser.setGender(genderValue);
        currentUser.setAddress(addressField.getText().trim());
        currentUser.setCity(cityField.getText().trim());
        currentUser.setPostalCode(postalCodeField.getText().trim());

        backend.updateUser(currentUser).ifPresentOrElse(updated -> {
            session.setCurrentUser(updated);
            isEditing = false;
            setFieldsEditable(false);
            editBtn.setVisible(true);
            editBtn.setManaged(true);
            saveBtn.setVisible(false);
            saveBtn.setManaged(false);
            userNameLabel.setText(updated.getName());
            userEmailLabel.setText(updated.getEmail());
            showSuccess("Profile updated successfully!");
        }, () -> showError("Email already in use by another account."));
    }

    private void setFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        emailField.setEditable(editable);
        phoneField.setEditable(editable);
        cnicField.setEditable(editable);
        dobPicker.setEditable(editable);
        dobPicker.setDisable(!editable);
        genderCombo.setDisable(!editable);
        addressField.setEditable(editable);
        cityField.setEditable(editable);
        postalCodeField.setEditable(editable);
    }

    @FXML
    private void handleChangePassword() {
        if (currentUser == null) {
            showError("No user session found.");
            return;
        }
        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showError("Please fill in all password fields");
            return;
        }

        if (!current.equals(currentUser.getPassword())) {
            showError("Current password is incorrect");
            return;
        }

        if (newPass.length() < 6) {
            showError("New password must be at least 6 characters");
            return;
        }

        if (!newPass.equals(confirm)) {
            showError("New passwords do not match");
            return;
        }

        currentUser.setPassword(newPass);
        backend.updateUser(currentUser);
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();

        showSuccess("Password changed successfully!");
    }

    @FXML
    private void handleDeleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Account");
        confirm.setHeaderText("Are you absolutely sure?");
        confirm.setContentText("This action cannot be undone. All your data including:\n" +
                "• Booking history\n" +
                "• Payment records\n" +
                "• Personal information\n\n" +
                "will be permanently deleted.\n\n" +
                "Type 'DELETE' to confirm:");

        TextField confirmField = new TextField();
        confirmField.setPromptText("Type DELETE");
        confirm.getDialogPane().setContent(new javafx.scene.layout.VBox(10,
                new Label(confirm.getContentText()), confirmField));

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if ("DELETE".equalsIgnoreCase(confirmField.getText().trim())) {
                    showSuccess("Account deletion initiated.\n\n" +
                            "You will be logged out and your account will be deleted within 24 hours.\n" +
                            "You can cancel this request by contacting support.");
                } else {
                    showError("Confirmation text did not match. Account not deleted.");
                }
            }
        });
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
