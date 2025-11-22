package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.models.Schedule;
import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.models.User;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Facade over the other services that provides a single point of access for the UI.
 */
public final class BackendService {
    private static final BackendService INSTANCE = new BackendService();
    private final UserService userService = new UserService();
    private final TrainService trainService = new TrainService();
    private final ScheduleService scheduleService = new ScheduleService();
    private final BookingService bookingService = new BookingService();

    private BackendService() {}

    public static BackendService getInstance() {
        return INSTANCE;
    }

    // User Service Methods
    public Optional<User> authenticate(String email, String password, String role) {
        return userService.authenticate(email, password, role);
    }

    public Optional<User> register(User user) {
        return userService.register(user);
    }

    public boolean emailExists(String email) {
        return userService.emailExists(email);
    }

    public Optional<User> getUserById(String userId) {
        return userService.getUserById(userId);
    }

    public Optional<User> updateUser(User updatedUser) {
        return userService.updateUser(updatedUser);
    }

    public ObservableList<User> getUsers() {
        return userService.getUsers();
    }

    // Train Service Methods
    public ObservableList<Train> getTrains() {
        return trainService.getTrains();
    }

    public List<Train> searchTrains(String from, String to) {
        return trainService.searchTrains(from, to);
    }

    public Optional<Train> getTrainByNumber(String trainNumber) {
        return trainService.getTrainByNumber(trainNumber);
    }

    public Train createTrain(String trainNumber, String trainName, String type, String route, String status) {
        return trainService.createTrain(trainNumber, trainName, type, route, status);
    }

    public void deleteTrain(Train train) {
        trainService.deleteTrain(train);
    }

    public Optional<Train> updateTrain(Train train) {
        return trainService.updateTrain(train);
    }

    public String nextTrainId() {
        return trainService.nextTrainId();
    }

    // Schedule Service Methods
    public ObservableList<Schedule> getSchedules() {
        return scheduleService.getSchedules();
    }

    public Optional<Schedule> getScheduleForTrain(String trainNumber) {
        return scheduleService.getScheduleForTrain(trainNumber);
    }

    public Schedule createSchedule(String trainNumber, String trainName, String departureTime,
                                   String arrivalTime, String route, String days, String status) {
        return scheduleService.createSchedule(trainNumber, trainName, departureTime, arrivalTime, route, days, status);
    }

    public void removeSchedule(Schedule schedule) {
        scheduleService.removeSchedule(schedule);
    }

    public boolean updateSchedule(Schedule schedule) {
        return scheduleService.updateSchedule(schedule);
    }

    // Booking Service Methods
    public Booking bookTicket(User user, Train train, String from, String to,
                              LocalDate date, int seats, String seatClass, double totalAmount) {
        return bookingService.bookTicket(user, train, from, to, date, seats, seatClass, totalAmount);
    }

    public boolean processPayment(String bookingId, String paymentMethod) {
        return bookingService.processPayment(bookingId, paymentMethod);
    }

    public List<Booking> getPendingPaymentsForUser(String userId) {
        return bookingService.getPendingPaymentsForUser(userId);
    }

    public List<Booking> getBookingsForUser(String userId) {
        return bookingService.getBookingsForUser(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    public Optional<Booking> getBookingById(String bookingId) {
        return bookingService.getBookingById(bookingId);
    }
}
