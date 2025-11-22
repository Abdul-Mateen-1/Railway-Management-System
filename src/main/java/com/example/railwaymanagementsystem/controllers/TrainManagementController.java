package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.Optional;
import java.util.Random;

/**
 * Controller for Train Management Screen
 */
public class TrainManagementController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private ComboBox<String> routeFilterCombo;
    @FXML private TableView<Train> trainTable;
    @FXML private Label countLabel;
    @FXML private TableColumn<Train, Void> actionsColumn;

    private final BackendService backend = BackendService.getInstance();
    private ObservableList<Train> trainData;
    private FilteredList<Train> filteredData;
    private final Random random = new Random();

    @FXML
    private void initialize() {
        initializeTrainData();
        setupTable();
        setupFilters();

        statusFilterCombo.setValue("All Status");
        typeFilterCombo.setValue("All Types");
        routeFilterCombo.setValue("All Routes");
    }

    private void initializeTrainData() {
        trainData = backend.getTrains();
        filteredData = new FilteredList<>(trainData, p -> true);
        trainTable.setItems(filteredData);
        updateCountLabel();

        trainData.addListener((javafx.collections.ListChangeListener.Change<? extends Train> c) -> {
            updateCountLabel();
        });
    }

    private void setupTable() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);

            {
                pane.setAlignment(Pos.CENTER);
                editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #dc2626;");

                editBtn.setOnAction(event -> {
                    Train train = getTableView().getItems().get(getIndex());
                    handleEditTrain(train);
                });

                deleteBtn.setOnAction(event -> {
                    Train train = getTableView().getItems().get(getIndex());
                    handleDeleteTrain(train);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void setupFilters() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        statusFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        typeFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        routeFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        final String searchText = searchField.getText();
        final String statusFilter = statusFilterCombo.getValue();
        final String typeFilter = typeFilterCombo.getValue();
        final String routeFilter = routeFilterCombo.getValue();

        filteredData.setPredicate(train -> {
            final String lowerCaseSearchText = (searchText == null) ? "" : searchText.toLowerCase();
            boolean matchesSearch = lowerCaseSearchText.isEmpty() ||
                    (train.getTrainNumber() != null && train.getTrainNumber().toLowerCase().contains(lowerCaseSearchText)) ||
                    (train.getTrainName() != null && train.getTrainName().toLowerCase().contains(lowerCaseSearchText)) ||
                    (train.getRoute() != null && train.getRoute().toLowerCase().contains(lowerCaseSearchText));

            boolean matchesStatus = (statusFilter == null) || "All Status".equals(statusFilter) ||
                    (train.getStatus() != null && train.getStatus().equals(statusFilter));

            boolean matchesType = (typeFilter == null) || "All Types".equals(typeFilter) ||
                    (train.getType() != null && train.getType().equals(typeFilter));

            boolean matchesRoute = (routeFilter == null) || "All Routes".equals(routeFilter) ||
                    (train.getRoute() != null && train.getRoute().contains(routeFilter));

            return matchesSearch && matchesStatus && matchesType && matchesRoute;
        });

        updateCountLabel();
    }

    private void updateCountLabel() {
        countLabel.setText(String.format("Showing %d of %d trains",
                filteredData.size(), trainData.size()));
    }

    @FXML
    private void handleAddTrain() {
        Dialog<Train> dialog = createTrainDialog(null);
        dialog.setTitle("Add New Train");
        dialog.setHeaderText("Enter the details for the new train.");

        Optional<Train> result = dialog.showAndWait();
        result.ifPresent(newTrain -> {
            backend.createTrain(newTrain.getTrainNumber(), newTrain.getTrainName(), newTrain.getType(), newTrain.getRoute(), "On-time");
            showSuccess("Train added successfully!");
        });
    }

    private void handleEditTrain(Train train) {
        Dialog<Train> dialog = createTrainDialog(train);
        dialog.setTitle("Edit Train");
        dialog.setHeaderText("Edit the details for train: " + train.getTrainNumber());

        Optional<Train> result = dialog.showAndWait();
        result.ifPresent(editedTrain -> {
            train.setTrainName(editedTrain.getTrainName());
            train.setTrainNumber(editedTrain.getTrainNumber());
            train.setType(editedTrain.getType());
            train.setRoute(editedTrain.getRoute());
            backend.updateTrain(train);
            trainTable.refresh();
            showSuccess("Train updated successfully!");
        });
    }

    private void handleDeleteTrain(Train train) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Train");
        alert.setHeaderText("Delete train " + train.getTrainNumber() + " - " + train.getTrainName() + "?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                backend.deleteTrain(train);
                showSuccess("Train deleted successfully!");
            }
        });
    }

    private Dialog<Train> createTrainDialog(Train train) {
        Dialog<Train> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField trainNumberField = new TextField();
        trainNumberField.setPromptText("e.g., 1UP");
        TextField trainNameField = new TextField();
        trainNameField.setPromptText("e.g., Khyber Mail");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Express", "Passenger", "Freight");
        TextField routeField = new TextField();
        routeField.setPromptText("e.g., Karachi - Peshawar");

        if (train != null) {
            // Editing an existing train
            trainNumberField.setText(train.getTrainNumber());
            trainNameField.setText(train.getTrainName());
            typeComboBox.setValue(train.getType());
            routeField.setText(train.getRoute());
        } else {
            // Adding a new train
            String nextId = backend.nextTrainId();
            String suffix = random.nextBoolean() ? "UP" : "DN";
            trainNumberField.setText(nextId + suffix);
            trainNumberField.setEditable(false);
        }

        grid.add(new Label("Train Number:"), 0, 0);
        grid.add(trainNumberField, 1, 0);
        grid.add(new Label("Train Name:"), 0, 1);
        grid.add(trainNameField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeComboBox, 1, 2);
        grid.add(new Label("Route:"), 0, 3);
        grid.add(routeField, 1, 3);

        GridPane.setHgrow(trainNameField, Priority.ALWAYS);
        GridPane.setHgrow(routeField, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Train(
                        (train != null) ? train.getId() : backend.nextTrainId(),
                        trainNumberField.getText(),
                        trainNameField.getText(),
                        typeComboBox.getValue(),
                        routeField.getText(),
                        (train != null) ? train.getStatus() : "On-time"
                );
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
