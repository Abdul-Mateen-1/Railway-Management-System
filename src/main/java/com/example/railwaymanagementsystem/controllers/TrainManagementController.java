package com.example.railwaymanagementsystem.controllers;

import com.example.railwaymanagementsystem.RailSafarApp;
import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.services.BackendService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

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

    @FXML
    private void initialize() {
        // Initialize data
        initializeTrainData();

        // Setup table
        setupTable();

        // Setup filters
        setupFilters();

        // Set default filter values
        statusFilterCombo.setValue("All Status");
        typeFilterCombo.setValue("All Types");
        routeFilterCombo.setValue("All Routes");
    }

    /**
     * Initialize data from backend
     */
    private void initializeTrainData() {
        trainData = FXCollections.observableArrayList(backend.getTrains());

        filteredData = new FilteredList<>(trainData, p -> true);
        trainTable.setItems(filteredData);
        updateCountLabel();
    }

    /**
     * Setup table with action buttons
     */
    private void setupTable() {
        // Use the actions column from FXML
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

    /**
     * Setup filter listeners
     */
    private void setupFilters() {
        // Search field listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        // Status filter listener
        statusFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        // Type filter listener
        typeFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        // Route filter listener
        routeFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    /**
     * Apply all filters
     */
    private void applyFilters() {
        filteredData.setPredicate(train -> {
            // Search filter
            String searchText = searchField.getText().toLowerCase();
            boolean matchesSearch = searchText.isEmpty() ||
                    train.getTrainNumber().toLowerCase().contains(searchText) ||
                    train.getTrainName().toLowerCase().contains(searchText) ||
                    train.getRoute().toLowerCase().contains(searchText);

            // Status filter
            String statusFilter = statusFilterCombo.getValue();
            boolean matchesStatus = "All Status".equals(statusFilter) ||
                    train.getStatus().equals(statusFilter);

            // Type filter
            String typeFilter = typeFilterCombo.getValue();
            boolean matchesType = "All Types".equals(typeFilter) ||
                    train.getType().equals(typeFilter);

            // Route filter
            String routeFilter = routeFilterCombo.getValue();
            boolean matchesRoute = "All Routes".equals(routeFilter) ||
                    train.getRoute().contains(routeFilter);

            return matchesSearch && matchesStatus && matchesType && matchesRoute;
        });

        updateCountLabel();
    }

    /**
     * Update the count label
     */
    private void updateCountLabel() {
        countLabel.setText(String.format("Showing %d of %d trains",
                filteredData.size(), trainData.size()));
    }

    @FXML
    private void handleAddTrain() {
        // Show add train dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Train");
        dialog.setHeaderText("Add a new train to the system");
        dialog.setContentText("Train Name:");

        dialog.showAndWait().ifPresent(name -> {
            String number = "TN" + (trainData.size() + 1);
            Train newTrain = backend.createTrain(number, name, "Express", "Karachi - Lahore", "On-time");
            trainData.add(newTrain);
            updateCountLabel();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Train added successfully!");
            alert.showAndWait();
        });
    }

    private void handleEditTrain(Train train) {
        // Show edit dialog
        TextInputDialog dialog = new TextInputDialog(train.getTrainName());
        dialog.setTitle("Edit Train");
        dialog.setHeaderText("Edit train: " + train.getTrainNumber());
        dialog.setContentText("Train Name:");

        dialog.showAndWait().ifPresent(name -> {
            train.setTrainName(name);
            trainTable.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Train updated successfully!");
            alert.showAndWait();
        });
    }

    private void handleDeleteTrain(Train train) {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Train");
        alert.setHeaderText("Delete train " + train.getTrainNumber() + " - " + train.getTrainName() + "?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                backend.deleteTrain(train);
                trainData.remove(train);
                updateCountLabel();

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Train deleted successfully!");
                success.showAndWait();
            }
        });
    }
}
