package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Schedule;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

/**
 * Controller for Schedule Management Screen
 */
public class ScheduleManagementController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> trainFilterCombo;
    @FXML private ComboBox<String> dayFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private TableView<Schedule> scheduleTable;
    @FXML private Label countLabel;

    private final BackendService backend = BackendService.getInstance();
    private ObservableList<Schedule> scheduleData;
    private FilteredList<Schedule> filteredData;

    @FXML
    private void initialize() {
        initializeData();
        setupTable();
        setupFilters();

        // Set default values
        trainFilterCombo.setValue("All Trains");
        dayFilterCombo.setValue("All Days");
        statusFilterCombo.setValue("All Status");
    }

    private void initializeData() {
        scheduleData = FXCollections.observableArrayList(backend.getSchedules());
        filteredData = new FilteredList<>(scheduleData, p -> true);
        scheduleTable.setItems(filteredData);
        updateCountLabel();
    }

    private void setupTable() {
        // Add action buttons column
        TableColumn<Schedule, Void> actionsCol = new TableColumn<>("Actions");
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
                    Schedule schedule = getTableView().getItems().get(getIndex());
                    handleEditSchedule(schedule);
                });

                deleteBtn.setOnAction(e -> {
                    Schedule schedule = getTableView().getItems().get(getIndex());
                    handleDeleteSchedule(schedule);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        scheduleTable.getColumns().add(actionsCol);
    }

    private void setupFilters() {
        searchField.textProperty().addListener((obs, old, newVal) -> applyFilters());
        trainFilterCombo.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        dayFilterCombo.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        statusFilterCombo.valueProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private void applyFilters() {
        filteredData.setPredicate(schedule -> {
            String searchText = searchField.getText().toLowerCase();
            boolean matchesSearch = searchText.isEmpty() ||
                    schedule.getTrainNumber().toLowerCase().contains(searchText) ||
                    schedule.getRoute().toLowerCase().contains(searchText);

            String trainFilter = trainFilterCombo.getValue();
            boolean matchesTrain = "All Trains".equals(trainFilter) ||
                    schedule.getTrainName().equals(trainFilter);

            String dayFilter = dayFilterCombo.getValue();
            boolean matchesDay = "All Days".equals(dayFilter) ||
                    schedule.getDays().contains(dayFilter.substring(0, 3));

            String statusFilter = statusFilterCombo.getValue();
            boolean matchesStatus = "All Status".equals(statusFilter) ||
                    schedule.getStatus().equals(statusFilter);

            return matchesSearch && matchesTrain && matchesDay && matchesStatus;
        });

        updateCountLabel();
    }

    private void updateCountLabel() {
        countLabel.setText("Showing " + filteredData.size() + " schedules");
    }

    @FXML
    private void handleAddSchedule() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Schedule");
        dialog.setHeaderText("Add a new train schedule");
        dialog.setContentText("Train Number:");

        dialog.showAndWait().ifPresent(trainNum -> {
            String id = String.valueOf(scheduleData.size() + 1);
            Schedule newSchedule = backend.createSchedule(trainNum, "New Train",
                    "09:00 AM", "05:00 PM", "New Route", "Daily", "Active");
            scheduleData.add(newSchedule);
            updateCountLabel();

            showSuccess("Schedule added successfully!");
        });
    }

    private void handleEditSchedule(Schedule schedule) {
        TextInputDialog dialog = new TextInputDialog(schedule.getDepartureTime());
        dialog.setTitle("Edit Schedule");
        dialog.setHeaderText("Edit schedule: " + schedule.getTrainNumber());
        dialog.setContentText("Departure Time:");

        dialog.showAndWait().ifPresent(time -> {
            schedule.setDepartureTime(time);
            scheduleTable.refresh();
            showSuccess("Schedule updated successfully!");
        });
    }

    private void handleDeleteSchedule(Schedule schedule) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Schedule");
        alert.setHeaderText("Delete schedule for " + schedule.getTrainNumber() + "?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                backend.removeSchedule(schedule);
                scheduleData.remove(schedule);
                updateCountLabel();
                showSuccess("Schedule deleted successfully!");
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
