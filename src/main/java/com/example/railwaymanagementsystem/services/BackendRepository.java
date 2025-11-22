package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.models.Schedule;
import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.models.User;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository layer that delegates to DatabaseService for persistent storage
 */
public final class BackendRepository {
    private static final BackendRepository INSTANCE = new BackendRepository();
    private final DatabaseService db = new DatabaseService();
    private static final Logger LOGGER = Logger.getLogger(BackendRepository.class.getName());


    private BackendRepository() {}

    public static BackendRepository getInstance() {
        return INSTANCE;
    }

    // User operations
    public Optional<User> findUserByEmail(String email) {
        try {
            return db.findUserByEmail(email);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by email", e);
            return Optional.empty();
        }
    }

    public Optional<User> findUserById(String id) {
        try {
            return db.findUserById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by id", e);
            return Optional.empty();
        }
    }

    public Collection<User> getUsers() {
        try {
            return db.getAllUsers();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting users", e);
            return Collections.emptyList();
        }
    }

    public User addUser(User user) {
        try {
            return db.addUser(user);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding user", e);
            return user;
        }
    }

    public boolean updateUser(User user) {
        try {
            return db.updateUser(user);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user", e);
            return false;
        }
    }

    public boolean emailExists(String email, String excludeUserId) {
        try {
            return db.emailExists(email, excludeUserId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking email existence", e);
            return false;
        }
    }

    public String nextUserId() {
        try {
            return db.getNextUserId();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next user id", e);
            return "1";
        }
    }

    // Train operations
    public List<Train> getTrains() {
        try {
            return db.getAllTrains();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting trains", e);
            return Collections.emptyList();
        }
    }

    public Optional<Train> findTrainById(String id) {
        try {
            return db.findTrainById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding train by id", e);
            return Optional.empty();
        }
    }

    public Optional<Train> findTrainByNumber(String trainNumber) {
        try {
            return db.findTrainByNumber(trainNumber);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding train by number", e);
            return Optional.empty();
        }
    }

    public Train addTrain(Train train) {
        try {
            return db.addTrain(train);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding train", e);
            return train;
        }
    }

    public boolean updateTrain(Train train) {
        try {
            return db.updateTrain(train);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating train", e);
            return false;
        }
    }

    public void removeTrain(String id) {
        try {
            db.removeTrain(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing train", e);
        }
    }

    public String nextTrainId() {
        try {
            return db.getNextTrainId();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next train id", e);
            return "1";
        }
    }

    // Schedule operations
    public List<Schedule> getSchedules() {
        try {
            return db.getAllSchedules();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting schedules", e);
            return Collections.emptyList();
        }
    }

    public Optional<Schedule> findScheduleByTrainNumber(String trainNumber) {
        try {
            return db.findScheduleByTrainNumber(trainNumber);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding schedule", e);
            return Optional.empty();
        }
    }

    public Schedule addSchedule(Schedule schedule) {
        try {
            return db.addSchedule(schedule);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding schedule", e);
            return schedule;
        }
    }

    public boolean updateSchedule(Schedule schedule) {
        try {
            return db.updateSchedule(schedule);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating schedule", e);
            return false;
        }
    }

    public void removeSchedule(Schedule schedule) {
        try {
            db.removeSchedule(schedule.getId());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing schedule", e);
        }
    }

    public String nextScheduleId() {
        try {
            return db.getNextScheduleId();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next schedule id", e);
            return "1";
        }
    }

    // Booking operations
    public List<Booking> getBookings() {
        try {
            return db.getAllBookings();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting bookings", e);
            return Collections.emptyList();
        }
    }

    public Optional<Booking> findBookingById(String id) {
        try {
            return db.findBookingById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding booking", e);
            return Optional.empty();
        }
    }

    public Booking addBooking(Booking booking) {
        try {
            return db.addBooking(booking);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding booking", e);
            return booking;
        }
    }

    public boolean updateBooking(Booking booking) {
        try {
            return db.updateBooking(booking);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating booking", e);
            return false;
        }
    }

    public String nextBookingId() {
        try {
            return db.getNextBookingId();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next booking id", e);
            return "1";
        }
    }
}
