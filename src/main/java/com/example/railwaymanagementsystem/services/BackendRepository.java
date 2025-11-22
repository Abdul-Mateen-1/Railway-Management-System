package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.models.Schedule;
import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
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

    private final ObservableList<User> users;
    private final ObservableList<Train> trains;
    private final ObservableList<Schedule> schedules;

    private BackendRepository() {
        users = FXCollections.observableArrayList();
        trains = FXCollections.observableArrayList();
        schedules = FXCollections.observableArrayList();
        loadInitialData();
    }

    private void loadInitialData() {
        try {
            users.setAll(db.getAllUsers());
            trains.setAll(db.getAllTrains());
            schedules.setAll(db.getAllSchedules());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading initial data", e);
        }
    }

    public static BackendRepository getInstance() {
        return INSTANCE;
    }

    // User operations
    public Optional<User> findUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Optional<User> findUserById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public User addUser(User user) {
        try {
            User newUser = db.addUser(user);
            users.add(newUser);
            return newUser;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding user", e);
            return user;
        }
    }

    public boolean updateUser(User user) {
        try {
            if (db.updateUser(user)) {
                int index = -1;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getId().equals(user.getId())) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    users.set(index, user);
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user", e);
            return false;
        }
    }

    public boolean emailExists(String email, String excludeUserId) {
        return users.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email) && !user.getId().equals(excludeUserId));
    }

    public String nextUserId() {
        try {
            return db.getNextUserId();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next user id", e);
            return String.valueOf(users.size() + 101); // Fallback
        }
    }

    // Train operations
    public ObservableList<Train> getTrains() {
        return trains;
    }

    public Optional<Train> findTrainById(String id) {
        return trains.stream()
                .filter(train -> train.getId().equals(id))
                .findFirst();
    }

    public Optional<Train> findTrainByNumber(String trainNumber) {
        return trains.stream()
                .filter(train -> train.getTrainNumber().equalsIgnoreCase(trainNumber))
                .findFirst();
    }

    public Train addTrain(Train train) {
        try {
            Train newTrain = db.addTrain(train);
            trains.add(newTrain);
            return newTrain;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding train", e);
            return train;
        }
    }

    public boolean updateTrain(Train train) {
        try {
            if (db.updateTrain(train)) {
                int index = -1;
                for (int i = 0; i < trains.size(); i++) {
                    if (trains.get(i).getId().equals(train.getId())) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    trains.set(index, train);
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating train", e);
            return false;
        }
    }

    public void removeTrain(String id) {
        try {
            if (db.removeTrain(id)) {
                trains.removeIf(train -> train.getId().equals(id));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing train", e);
        }
    }

    public String nextTrainId() {
        try {
            return db.getNextTrainId();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next train id", e);
            return String.valueOf(trains.size() + 1); // Fallback
        }
    }

    // Schedule operations
    public ObservableList<Schedule> getSchedules() {
        return schedules;
    }

    public Optional<Schedule> findScheduleByTrainNumber(String trainNumber) {
        return schedules.stream()
                .filter(schedule -> schedule.getTrainNumber().equalsIgnoreCase(trainNumber))
                .findFirst();
    }

    public Schedule addSchedule(Schedule schedule) {
        try {
            Schedule newSchedule = db.addSchedule(schedule);
            schedules.add(newSchedule);
            return newSchedule;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding schedule", e);
            return schedule;
        }
    }

    public boolean updateSchedule(Schedule schedule) {
        try {
            if (db.updateSchedule(schedule)) {
                int index = -1;
                for (int i = 0; i < schedules.size(); i++) {
                    if (schedules.get(i).getId().equals(schedule.getId())) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    schedules.set(index, schedule);
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating schedule", e);
            return false;
        }
    }

    public void removeSchedule(Schedule schedule) {
        try {
            if (db.removeSchedule(schedule.getId())) {
                schedules.removeIf(s -> s.getId().equals(schedule.getId()));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing schedule", e);
        }
    }

    public String nextScheduleId() {
        try {
            return db.getNextScheduleId();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next schedule id", e);
            return String.valueOf(schedules.size() + 1); // Fallback
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
