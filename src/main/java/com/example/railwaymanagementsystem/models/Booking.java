package com.example.railwaymanagementsystem.models;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Booking Model - Represents a ticket booking
 */
public class Booking {
    private final StringProperty id;
    private final StringProperty userId;
    private final StringProperty trainId;
    private final StringProperty trainNumber;
    private final StringProperty trainName;
    private final StringProperty fromStation;
    private final StringProperty toStation;
    private final ObjectProperty<LocalDate> travelDate;
    private final IntegerProperty numberOfSeats;
    private final StringProperty seatClass; // Economy, Business, First Class
    private final DoubleProperty totalAmount;
    private final StringProperty status; // Confirmed, Cancelled, Pending
    private final ObjectProperty<LocalDateTime> bookingDateTime;
    private final StringProperty paymentMethod; // Cash on Delivery, Card, etc.
    private final StringProperty paymentStatus; // Pending, Paid, Failed

    public Booking() {
        this("", "", "", "", "", "", "",
                LocalDate.now(), 1, "Economy", 0.0, "Pending", LocalDateTime.now(), "", "Pending");
    }

    public Booking(String id, String userId, String trainId, String trainNumber,
                   String trainName, String fromStation, String toStation,
                   LocalDate travelDate, int numberOfSeats, String seatClass,
                   double totalAmount, String status, LocalDateTime bookingDateTime) {
        this(id, userId, trainId, trainNumber, trainName, fromStation, toStation,
                travelDate, numberOfSeats, seatClass, totalAmount, status, bookingDateTime, "", "Pending");
    }

    public Booking(String id, String userId, String trainId, String trainNumber,
                   String trainName, String fromStation, String toStation,
                   LocalDate travelDate, int numberOfSeats, String seatClass,
                   double totalAmount, String status, LocalDateTime bookingDateTime,
                   String paymentMethod, String paymentStatus) {
        this.id = new SimpleStringProperty(id);
        this.userId = new SimpleStringProperty(userId);
        this.trainId = new SimpleStringProperty(trainId);
        this.trainNumber = new SimpleStringProperty(trainNumber);
        this.trainName = new SimpleStringProperty(trainName);
        this.fromStation = new SimpleStringProperty(fromStation);
        this.toStation = new SimpleStringProperty(toStation);
        this.travelDate = new SimpleObjectProperty<>(travelDate);
        this.numberOfSeats = new SimpleIntegerProperty(numberOfSeats);
        this.seatClass = new SimpleStringProperty(seatClass);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.status = new SimpleStringProperty(status);
        this.bookingDateTime = new SimpleObjectProperty<>(bookingDateTime);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
        this.paymentStatus = new SimpleStringProperty(paymentStatus);
    }

    // Getters and setters
    public String getId() { return id.get(); }
    public void setId(String value) { id.set(value); }
    public StringProperty idProperty() { return id; }

    public String getUserId() { return userId.get(); }
    public void setUserId(String value) { userId.set(value); }
    public StringProperty userIdProperty() { return userId; }

    public String getTrainId() { return trainId.get(); }
    public void setTrainId(String value) { trainId.set(value); }
    public StringProperty trainIdProperty() { return trainId; }

    public String getTrainNumber() { return trainNumber.get(); }
    public void setTrainNumber(String value) { trainNumber.set(value); }
    public StringProperty trainNumberProperty() { return trainNumber; }

    public String getTrainName() { return trainName.get(); }
    public void setTrainName(String value) { trainName.set(value); }
    public StringProperty trainNameProperty() { return trainName; }

    public String getFromStation() { return fromStation.get(); }
    public void setFromStation(String value) { fromStation.set(value); }
    public StringProperty fromStationProperty() { return fromStation; }

    public String getToStation() { return toStation.get(); }
    public void setToStation(String value) { toStation.set(value); }
    public StringProperty toStationProperty() { return toStation; }

    public LocalDate getTravelDate() { return travelDate.get(); }
    public void setTravelDate(LocalDate value) { travelDate.set(value); }
    public ObjectProperty<LocalDate> travelDateProperty() { return travelDate; }

    public int getNumberOfSeats() { return numberOfSeats.get(); }
    public void setNumberOfSeats(int value) { numberOfSeats.set(value); }
    public IntegerProperty numberOfSeatsProperty() { return numberOfSeats; }

    public String getSeatClass() { return seatClass.get(); }
    public void setSeatClass(String value) { seatClass.set(value); }
    public StringProperty seatClassProperty() { return seatClass; }

    public double getTotalAmount() { return totalAmount.get(); }
    public void setTotalAmount(double value) { totalAmount.set(value); }
    public DoubleProperty totalAmountProperty() { return totalAmount; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }

    public LocalDateTime getBookingDateTime() { return bookingDateTime.get(); }
    public void setBookingDateTime(LocalDateTime value) { bookingDateTime.set(value); }
    public ObjectProperty<LocalDateTime> bookingDateTimeProperty() { return bookingDateTime; }

    // Payment Method
    public String getPaymentMethod() { return paymentMethod.get(); }
    public void setPaymentMethod(String value) { paymentMethod.set(value); }
    public StringProperty paymentMethodProperty() { return paymentMethod; }

    // Payment Status
    public String getPaymentStatus() { return paymentStatus.get(); }
    public void setPaymentStatus(String value) { paymentStatus.set(value); }
    public StringProperty paymentStatusProperty() { return paymentStatus; }
}
