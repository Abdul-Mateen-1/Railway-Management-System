package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Schedule;
import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.Optional;

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

        trainFilterCombo.setValue("All Trains");
        dayFilterCombo.setValue("All Days");
        statusFilterCombo.setValue("All Status");
    }

    private void initializeData() {
        scheduleData = backend.getSchedules();
        filteredData = new FilteredList<>(scheduleData, p -> true);
        scheduleTable.setItems(filteredData);
        updateCountLabel();

        scheduleData.addListener((javafx.collections.ListChangeListener.Change<? extends Schedule> c) -> {
            updateCountLabel();
        });
    }

    private void setupTable() {
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

        if (scheduleTable.getColumns().size() <= 7) {
            scheduleTable.getColumns().add(actionsCol);
        }
    }

    private void setupFilters() {
        searchField.textProperty().addListener((obs, old, newVal) -> applyFilters());
        trainFilterCombo.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        dayFilterCombo.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        statusFilterCombo.valueProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private void applyFilters() {
        final String searchText = searchField.getText();
        final String trainFilter = trainFilterCombo.getValue();
        final String dayFilter = dayFilterCombo.getValue();
        final String statusFilter = statusFilterCombo.getValue();

        filteredData.setPredicate(schedule -> {
            final String lowerCaseSearchText = (searchText == null) ? "" : searchText.toLowerCase();
            boolean matchesSearch = lowerCaseSearchText.isEmpty() ||
                    (schedule.getTrainNumber() != null && schedule.getTrainNumber().toLowerCase().contains(lowerCaseSearchText)) ||
                    (schedule.getRoute() != null && schedule.getRoute().toLowerCase().contains(lowerCaseSearchText));

            boolean matchesTrain = (trainFilter == null) || "All Trains".equals(trainFilter) ||
                    (schedule.getTrainName() != null && schedule.getTrainName().equals(trainFilter));

            boolean matchesDay = (dayFilter == null) || "All Days".equals(dayFilter) ||
                    (schedule.getDays() != null && dayFilter.length() >= 3 && schedule.getDays().contains(dayFilter.substring(0, 3)));

            boolean matchesStatus = (statusFilter == null) || "All Status".equals(statusFilter) ||
                    (schedule.getStatus() != null && schedule.getStatus().equals(statusFilter));

            return matchesSearch && matchesTrain && matchesDay && matchesStatus;
        });

        updateCountLabel();
    }

    private void updateCountLabel() {
        countLabel.setText(String.format("Showing %d of %d schedules",
                filteredData.size(), scheduleData.size()));
    }

    @FXML
    private void handleAddSchedule() {
        Dialog<Schedule> dialog = createScheduleDialog(null);
        dialog.setTitle("Add New Schedule");
        dialog.setHeaderText("Enter the details for the new schedule.");

        Optional<Schedule> result = dialog.showAndWait();
        result.ifPresent(newSchedule -> {
            backend.createSchedule(
                    newSchedule.getTrainNumber(),
                    newSchedule.getTrainName(),
                    newSchedule.getDepartureTime(),
                    newSchedule.getArrivalTime(),
                    newSchedule.getRoute(),
                    newSchedule.getDays(),
                    newSchedule.getStatus()
            );
            showSuccess("Schedule added successfully!");
        });
    }

    private void handleEditSchedule(Schedule schedule) {
        Dialog<Schedule> dialog = createScheduleDialog(schedule);
        dialog.setTitle("Edit Schedule");
        dialog.setHeaderText("Edit the details for schedule: " + schedule.getTrainNumber());

        Optional<Schedule> result = dialog.showAndWait();
        result.ifPresent(editedSchedule -> {
            schedule.setDepartureTime(editedSchedule.getDepartureTime());
            schedule.setArrivalTime(editedSchedule.getArrivalTime());
            schedule.setDays(editedSchedule.getDays());
            schedule.setStatus(editedSchedule.getStatus());
            backend.updateSchedule(schedule);
            showSuccess("Schedule updated successfully!");
        });
    }

    private Dialog<Schedule> createScheduleDialog(Schedule schedule) {
        Dialog<Schedule> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField trainNumberField = new TextField();
        trainNumberField.setPromptText("Enter Train Number and Press Enter");

        Label trainNameLabel = new Label("Train Name will appear here");
        Label routeLabel = new Label("Route will appear here");

        ComboBox<Integer> depHour = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        ComboBox<String> depAmPm = new ComboBox<>(FXCollections.observableArrayList("AM", "PM"));
        HBox departureBox = new HBox(5, depHour, depAmPm);

        ComboBox<Integer> arrHour = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        ComboBox<String> arrAmPm = new ComboBox<>(FXCollections.observableArrayList("AM", "PM"));
        HBox arrivalBox = new HBox(5, arrHour, arrAmPm);

        ComboBox<String> daysCombo = new ComboBox<>(FXCollections.observableArrayList("Daily", "Mon-Fri", "Sat-Sun"));
        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("Active", "Inactive"));

        if (schedule != null) {
            trainNumberField.setText(schedule.getTrainNumber());
            trainNumberField.setEditable(false);
            trainNameLabel.setText(schedule.getTrainName());
            routeLabel.setText(schedule.getRoute());

            // Pre-fill time
            String[] depParts = schedule.getDepartureTime().split("[: ]");
            depHour.setValue(Integer.parseInt(depParts[0]));
            depAmPm.setValue(depParts[2]);

            String[] arrParts = schedule.getArrivalTime().split("[: ]");
            arrHour.setValue(Integer.parseInt(arrParts[0]));
            arrAmPm.setValue(arrParts[2]);

            daysCombo.setValue(schedule.getDays());
            statusCombo.setValue(schedule.getStatus());
        }

        grid.add(new Label("Train Number:"), 0, 0);
        grid.add(trainNumberField, 1, 0);
        grid.add(new Label("Train Name:"), 0, 1);
        grid.add(trainNameLabel, 1, 1);
        grid.add(new Label("Route:"), 0, 2);
        grid.add(routeLabel, 1, 2);
        grid.add(new Label("Departure Time:"), 0, 3);
        grid.add(departureBox, 1, 3);
        grid.add(new Label("Arrival Time:"), 0, 4);
        grid.add(arrivalBox, 1, 4);
        grid.add(new Label("Days:"), 0, 5);
        grid.add(daysCombo, 1, 5);
        grid.add(new Label("Status:"), 0, 6);
        grid.add(statusCombo, 1, 6);

        dialog.getDialogPane().setContent(grid);

        final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(schedule == null); // Disable OK if adding new until train is validated

        if (schedule == null) { // Only set this listener for adding new schedules
            trainNumberField.setOnAction(event -> {
                String trainNumber = trainNumberField.getText();
                Optional<Train> trainOpt = backend.getTrainByNumber(trainNumber);
                if (trainOpt.isEmpty()) {
                    showError("Train number not found.");
                    okButton.setDisable(true);
                } else if (backend.getScheduleForTrain(trainNumber).isPresent()) {
                    showError("A schedule for this train already exists.");
                    okButton.setDisable(true);
                } else {
                    Train train = trainOpt.get();
                    trainNameLabel.setText(train.getTrainName());
                    routeLabel.setText(train.getRoute());
                    okButton.setDisable(false);
                }
            });
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String departureTime = String.format("%02d:00 %s", depHour.getValue(), depAmPm.getValue());
                String arrivalTime = String.format("%02d:00 %s", arrHour.getValue(), arrAmPm.getValue());
                Schedule resultSchedule = (schedule != null) ? schedule : new Schedule();

                resultSchedule.setTrainNumber(trainNumberField.getText());
                resultSchedule.setTrainName(trainNameLabel.getText());
                resultSchedule.setRoute(routeLabel.getText());
                resultSchedule.setDepartureTime(departureTime);
                resultSchedule.setArrivalTime(arrivalTime);
                resultSchedule.setDays(daysCombo.getValue());
                resultSchedule.setStatus(statusCombo.getValue());
                
                if (schedule == null) {
                    resultSchedule.setId("0"); // Let backend generate ID
                }
                return resultSchedule;
            }
            return null;
        });

        return dialog;
    }

    private void handleDeleteSchedule(Schedule schedule) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Schedule");
        alert.setHeaderText("Delete schedule for " + schedule.getTrainNumber() + "?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                backend.removeSchedule(schedule);
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
