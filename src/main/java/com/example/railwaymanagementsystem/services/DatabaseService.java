package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.dao.BookingDAO;
import com.example.railwaymanagementsystem.dao.ScheduleDAO;
import com.example.railwaymanagementsystem.dao.TrainDAO;
import com.example.railwaymanagementsystem.dao.UserDAO;
import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.models.Schedule;
import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.models.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DatabaseService {
    private final UserDAO userDAO;
    private final TrainDAO trainDAO;
    private final ScheduleDAO scheduleDAO;
    private final BookingDAO bookingDAO;

    public DatabaseService() {
        this.userDAO = new UserDAO();
        this.trainDAO = new TrainDAO();
        this.scheduleDAO = new ScheduleDAO();
        this.bookingDAO = new BookingDAO();
    }

    // User operations
    public Optional<User> findUserByEmail(String email) throws SQLException {
        return userDAO.findUserByEmail(email);
    }

    public Optional<User> findUserById(String id) throws SQLException {
        return userDAO.findUserById(id);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public User addUser(User user) throws SQLException {
        return userDAO.addUser(user);
    }

    public boolean updateUser(User user) throws SQLException {
        return userDAO.updateUser(user);
    }

    public boolean emailExists(String email, String excludeUserId) throws SQLException {
        return userDAO.emailExists(email, excludeUserId);
    }

    public String getNextUserId() throws SQLException {
        return userDAO.getNextUserId();
    }

    // Train operations
    public List<Train> getAllTrains() throws SQLException {
        return trainDAO.getAllTrains();
    }

    public Optional<Train> findTrainById(String id) throws SQLException {
        return trainDAO.findTrainById(id);
    }

    public Optional<Train> findTrainByNumber(String trainNumber) throws SQLException {
        return trainDAO.findTrainByNumber(trainNumber);
    }

    public Train addTrain(Train train) throws SQLException {
        return trainDAO.addTrain(train);
    }

    public boolean updateTrain(Train train) throws SQLException {
        return trainDAO.updateTrain(train);
    }

    public boolean removeTrain(String id) throws SQLException {
        return trainDAO.removeTrain(id);
    }

    public String getNextTrainId() throws SQLException {
        return trainDAO.getNextTrainId();
    }

    // Schedule operations
    public List<Schedule> getAllSchedules() throws SQLException {
        return scheduleDAO.getAllSchedules();
    }

    public Optional<Schedule> findScheduleByTrainNumber(String trainNumber) throws SQLException {
        return scheduleDAO.findScheduleByTrainNumber(trainNumber);
    }

    public Schedule addSchedule(Schedule schedule) throws SQLException {
        return scheduleDAO.addSchedule(schedule);
    }

    public boolean updateSchedule(Schedule schedule) throws SQLException {
        return scheduleDAO.updateSchedule(schedule);
    }

    public boolean removeSchedule(String id) throws SQLException {
        return scheduleDAO.removeSchedule(id);
    }

    public String getNextScheduleId() throws SQLException {
        return scheduleDAO.getNextScheduleId();
    }

    // Booking operations
    public List<Booking> getAllBookings() throws SQLException {
        return bookingDAO.getAllBookings();
    }

    public Optional<Booking> findBookingById(String id) throws SQLException {
        return bookingDAO.findBookingById(id);
    }

    public Booking addBooking(Booking booking) throws SQLException {
        return bookingDAO.addBooking(booking);
    }

    public boolean updateBooking(Booking booking) throws SQLException {
        return bookingDAO.updateBooking(booking);
    }

    public String getNextBookingId() throws SQLException {
        return bookingDAO.getNextBookingId();
    }
}
