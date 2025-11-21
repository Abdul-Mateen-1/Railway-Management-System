package com.example.railwaymanagementsystem.models;

import javafx.beans.property.*;

/**
 * Schedule Model - Represents a train schedule
 */
public class Schedule {
    private final StringProperty id;
    private final StringProperty trainNumber;
    private final StringProperty trainName;
    private final StringProperty departureTime;
    private final StringProperty arrivalTime;
    private final StringProperty route;
    private final StringProperty days;
    private final StringProperty status;

    public Schedule() {
        this("", "", "", "", "", "", "", "Active");
    }

    public Schedule(String id, String trainNumber, String trainName,
                    String departureTime, String arrivalTime, String route,
                    String days, String status) {
        this.id = new SimpleStringProperty(id);
        this.trainNumber = new SimpleStringProperty(trainNumber);
        this.trainName = new SimpleStringProperty(trainName);
        this.departureTime = new SimpleStringProperty(departureTime);
        this.arrivalTime = new SimpleStringProperty(arrivalTime);
        this.route = new SimpleStringProperty(route);
        this.days = new SimpleStringProperty(days);
        this.status = new SimpleStringProperty(status);
    }

    // Getters and setters
    public String getId() { return id.get(); }
    public void setId(String value) { id.set(value); }
    public StringProperty idProperty() { return id; }

    public String getTrainNumber() { return trainNumber.get(); }
    public void setTrainNumber(String value) { trainNumber.set(value); }
    public StringProperty trainNumberProperty() { return trainNumber; }

    public String getTrainName() { return trainName.get(); }
    public void setTrainName(String value) { trainName.set(value); }
    public StringProperty trainNameProperty() { return trainName; }

    public String getDepartureTime() { return departureTime.get(); }
    public void setDepartureTime(String value) { departureTime.set(value); }
    public StringProperty departureTimeProperty() { return departureTime; }

    public String getArrivalTime() { return arrivalTime.get(); }
    public void setArrivalTime(String value) { arrivalTime.set(value); }
    public StringProperty arrivalTimeProperty() { return arrivalTime; }

    public String getRoute() { return route.get(); }
    public void setRoute(String value) { route.set(value); }
    public StringProperty routeProperty() { return route; }

    public String getDays() { return days.get(); }
    public void setDays(String value) { days.set(value); }
    public StringProperty daysProperty() { return days; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }
}
