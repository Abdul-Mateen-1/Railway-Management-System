package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.models.Schedule;
import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * In-memory data storage for trains, schedules, users and bookings.
 */
public final class BackendRepository {
    private static final BackendRepository INSTANCE = new BackendRepository();

    private final Map<String, User> usersByEmail = new LinkedHashMap<>();
    private final Map<String, User> usersById = new LinkedHashMap<>();
    private final Map<String, Train> trainsById = new LinkedHashMap<>();
    private final Map<String, Schedule> schedulesById = new LinkedHashMap<>();
    private final List<Booking> bookings = new ArrayList<>();

    private int nextUserId = 100;
    private int nextTrainId = 200;
    private int nextScheduleId = 300;
    private int nextBookingId = 400;

    private BackendRepository() {
        seedUsers();
        seedTrains();
        seedSchedules();
        seedBookings();
    }

    public static BackendRepository getInstance() {
        return INSTANCE;
    }

    private void seedUsers() {
        User admin = new User(generateUserId(), "System Admin", "admin@railsafar.com", "0300-0000000", "admin", "admin123");
        admin.setCnic("35202-1234567-1");
        admin.setDateOfBirth(LocalDate.of(1985, 5, 12));
        admin.setGender("Male");
        admin.setAddress("HQ, Rail Safar Building");
        admin.setCity("Karachi");
        admin.setPostalCode("75500");
        addUser(admin);

        User sarah = new User(generateUserId(), "Sarah Khan", "sarah.khan@example.com", "0300-1111111", "passenger", "password1");
        sarah.setCnic("61101-9876543-2");
        sarah.setDateOfBirth(LocalDate.of(1994, 3, 8));
        sarah.setGender("Female");
        sarah.setAddress("12 Defence View");
        sarah.setCity("Karachi");
        sarah.setPostalCode("75400");
        addUser(sarah);

        User ali = new User(generateUserId(), "Ali Asghar", "ali.asghar@example.com", "0300-2222222", "passenger", "password2");
        ali.setCnic("37405-4567890-3");
        ali.setDateOfBirth(LocalDate.of(1990, 11, 21));
        ali.setGender("Male");
        ali.setAddress("44 Satellite Town");
        ali.setCity("Rawalpindi");
        ali.setPostalCode("46000");
        addUser(ali);
    }

    private void seedTrains() {
        addTrain(new Train(generateTrainId(), "1UP", "Karachi Express", "Express", "Karachi - Lahore", "On-time"));
        addTrain(new Train(generateTrainId(), "2DN", "Lahore Express", "Express", "Lahore - Karachi", "Delayed"));
        addTrain(new Train(generateTrainId(), "3UP", "Green Line", "Passenger", "Islamabad - Multan", "On-time"));
        addTrain(new Train(generateTrainId(), "5UP", "Business Express", "Express", "Rawalpindi - Quetta", "On-time"));
        addTrain(new Train(generateTrainId(), "6DN", "Peshawar Mail", "Passenger", "Peshawar - Karachi", "Delayed"));
        addTrain(new Train(generateTrainId(), "7UP", "Night Coach", "Express", "Karachi - Islamabad", "On-time"));
    }

    private void seedSchedules() {
        addSchedule(new Schedule(generateScheduleId(), "1UP", "Karachi Express", "08:00 AM", "08:00 PM", "Karachi - Lahore", "Daily", "Active"));
        addSchedule(new Schedule(generateScheduleId(), "2DN", "Lahore Express", "09:00 AM", "09:00 PM", "Lahore - Karachi", "Daily", "Active"));
        addSchedule(new Schedule(generateScheduleId(), "3UP", "Green Line", "10:30 AM", "06:30 PM", "Islamabad - Multan", "Mon-Fri", "Active"));
        addSchedule(new Schedule(generateScheduleId(), "5UP", "Business Express", "07:00 AM", "05:00 PM", "Rawalpindi - Quetta", "Daily", "Active"));
        addSchedule(new Schedule(generateScheduleId(), "6DN", "Peshawar Mail", "11:00 AM", "11:00 PM", "Peshawar - Karachi", "Daily", "Active"));
        addSchedule(new Schedule(generateScheduleId(), "7UP", "Night Coach", "11:30 PM", "09:30 AM", "Karachi - Islamabad", "Daily", "Active"));
    }

    private void seedBookings() {
        findTrainByNumber("1UP").ifPresent(train -> bookings.add(new Booking(
                generateBookingId(),
                "101",
                train.getId(),
                train.getTrainNumber(),
                train.getTrainName(),
                "Karachi",
                "Lahore",
                LocalDate.now().plusDays(2),
                2,
                "Economy",
                5000,
                "Confirmed",
                LocalDateTime.now().minusDays(1),
                "Card",
                "Paid"
        )));
        findTrainByNumber("2DN").ifPresent(train -> bookings.add(new Booking(
                generateBookingId(),
                "102",
                train.getId(),
                train.getTrainNumber(),
                train.getTrainName(),
                "Lahore",
                "Karachi",
                LocalDate.now().plusDays(5),
                1,
                "Business",
                3000,
                "Confirmed",
                LocalDateTime.now().minusHours(5),
                "Cash on Delivery",
                "Paid"
        )));
    }

    private String generateUserId() {
        return String.valueOf(nextUserId++);
    }

    private String generateTrainId() {
        return String.valueOf(nextTrainId++);
    }

    private String generateScheduleId() {
        return String.valueOf(nextScheduleId++);
    }

    private String generateBookingId() {
        return String.valueOf(nextBookingId++);
    }

    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email.toLowerCase(Locale.ROOT)));
    }

    public Optional<User> findUserById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    public Collection<User> getUsers() {
        return Collections.unmodifiableCollection(usersById.values());
    }

    public User addUser(User user) {
        usersByEmail.put(user.getEmail().toLowerCase(Locale.ROOT), user);
        usersById.put(user.getId(), user);
        return user;
    }

    public void updateUser(User user, String previousEmail) {
        if (previousEmail != null && !previousEmail.equalsIgnoreCase(user.getEmail())) {
            usersByEmail.remove(previousEmail.toLowerCase(Locale.ROOT));
        }
        usersByEmail.put(user.getEmail().toLowerCase(Locale.ROOT), user);
        usersById.put(user.getId(), user);
    }

    public boolean emailExists(String email, String excludeUserId) {
        User existing = usersByEmail.get(email.toLowerCase(Locale.ROOT));
        return existing != null && !existing.getId().equals(excludeUserId);
    }

    public Collection<Train> getTrains() {
        return Collections.unmodifiableCollection(trainsById.values());
    }

    public Train addTrain(Train train) {
        trainsById.put(train.getId(), train);
        return train;
    }

    public void removeTrain(String id) {
        Train removed = trainsById.remove(id);
        if (removed != null) {
            schedulesById.values().removeIf(schedule -> schedule.getTrainNumber().equals(removed.getTrainNumber()));
        }
    }

    public Optional<Train> findTrainByNumber(String trainNumber) {
        return trainsById.values().stream()
                .filter(train -> train.getTrainNumber().equalsIgnoreCase(trainNumber))
                .findFirst();
    }

    public Collection<Schedule> getSchedules() {
        return Collections.unmodifiableCollection(schedulesById.values());
    }

    public Schedule addSchedule(Schedule schedule) {
        schedulesById.put(schedule.getId(), schedule);
        return schedule;
    }

    public void removeSchedule(Schedule schedule) {
        schedulesById.remove(schedule.getId());
    }

    public Optional<Schedule> findScheduleByTrainNumber(String trainNumber) {
        return schedulesById.values().stream()
                .filter(schedule -> schedule.getTrainNumber().equalsIgnoreCase(trainNumber))
                .findFirst();
    }

    public List<Booking> getBookings() {
        return Collections.unmodifiableList(bookings);
    }

    public Booking addBooking(Booking booking) {
        bookings.add(booking);
        return booking;
    }

    public String nextUserId() {
        return generateUserId();
    }

    public String nextTrainId() {
        return generateTrainId();
    }

    public String nextScheduleId() {
        return generateScheduleId();
    }

    public String nextBookingId() {
        return generateBookingId();
    }

    public Optional<Booking> findBookingById(String bookingId) {
        return bookings.stream().filter(b -> b.getId().equalsIgnoreCase(bookingId)).findFirst();
    }
}

