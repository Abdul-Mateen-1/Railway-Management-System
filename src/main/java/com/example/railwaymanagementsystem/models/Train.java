package com.example.railwaymanagementsystem.models;

import javafx.beans.property.*;

/**
 * Train Model - Represents a train in the railway system
 */
public class Train {
    private final StringProperty id;
    private final StringProperty trainNumber;
    private final StringProperty trainName;
    private final StringProperty type;
    private final StringProperty route;
    private final StringProperty status;

    public Train() {
        this("", "", "", "", "", "");
    }

    public Train(String id, String trainNumber, String trainName,
                 String type, String route, String status) {
        this.id = new SimpleStringProperty(id);
        this.trainNumber = new SimpleStringProperty(trainNumber);
        this.trainName = new SimpleStringProperty(trainName);
        this.type = new SimpleStringProperty(type);
        this.route = new SimpleStringProperty(route);
        this.status = new SimpleStringProperty(status);
    }

    // ID
    public String getId() { return id.get(); }
    public void setId(String value) { id.set(value); }
    public StringProperty idProperty() { return id; }

    // Train Number
    public String getTrainNumber() { return trainNumber.get(); }
    public void setTrainNumber(String value) { trainNumber.set(value); }
    public StringProperty trainNumberProperty() { return trainNumber; }

    // Train Name
    public String getTrainName() { return trainName.get(); }
    public void setTrainName(String value) { trainName.set(value); }
    public StringProperty trainNameProperty() { return trainName; }

    // Type
    public String getType() { return type.get(); }
    public void setType(String value) { type.set(value); }
    public StringProperty typeProperty() { return type; }

    // Route
    public String getRoute() { return route.get(); }
    public void setRoute(String value) { route.set(value); }
    public StringProperty routeProperty() { return route; }

    // Status
    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }
}
