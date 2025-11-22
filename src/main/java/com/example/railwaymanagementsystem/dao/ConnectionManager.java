package com.example.railwaymanagementsystem.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConnectionManager {
    private static final String DB_URL = "jdbc:sqlite:railway_management.db";
    private static Connection connection;

    public static synchronized Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL);
                initializeDatabase();
            } catch (SQLException e) {
                System.err.println("Error initializing database: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }

    private static void initializeDatabase() throws SQLException {
        createTables();
        seedInitialData();
    }

    private static void createTables() throws SQLException {
        // Users table
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                phone TEXT,
                role TEXT NOT NULL,
                password TEXT NOT NULL,
                cnic TEXT,
                date_of_birth DATE,
                gender TEXT,
                address TEXT,
                city TEXT,
                postal_code TEXT
            )
        """;

        // Trains table
        String createTrainsTable = """
            CREATE TABLE IF NOT EXISTS trains (
                id TEXT PRIMARY KEY,
                train_number TEXT UNIQUE NOT NULL,
                train_name TEXT NOT NULL,
                type TEXT,
                route TEXT,
                status TEXT
            )
        """;

        // Schedules table
        String createSchedulesTable = """
            CREATE TABLE IF NOT EXISTS schedules (
                id TEXT PRIMARY KEY,
                train_number TEXT NOT NULL,
                train_name TEXT NOT NULL,
                departure_time TEXT,
                arrival_time TEXT,
                route TEXT,
                days TEXT,
                status TEXT,
                FOREIGN KEY (train_number) REFERENCES trains(train_number)
            )
        """;

        // Bookings table
        String createBookingsTable = """
            CREATE TABLE IF NOT EXISTS bookings (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                train_id TEXT NOT NULL,
                train_number TEXT NOT NULL,
                train_name TEXT NOT NULL,
                from_station TEXT NOT NULL,
                to_station TEXT NOT NULL,
                travel_date DATE NOT NULL,
                number_of_seats INTEGER NOT NULL,
                seat_class TEXT,
                total_amount REAL NOT NULL,
                status TEXT NOT NULL,
                booking_date_time TIMESTAMP NOT NULL,
                payment_method TEXT,
                payment_status TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (train_id) REFERENCES trains(id)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createTrainsTable);
            stmt.execute(createSchedulesTable);
            stmt.execute(createBookingsTable);
        }
    }

    private static void seedInitialData() throws SQLException {
        // Check if data already exists
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Data already seeded
            }
        }

        // Seed users
        seedUsers();
        seedTrains();
        seedSchedules();
        seedBookings();
    }

    private static void seedUsers() throws SQLException {
        String sql = "INSERT OR IGNORE INTO users (id, name, email, phone, role, password, cnic, date_of_birth, gender, address, city, postal_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Admin user
            pstmt.setString(1, "100");
            pstmt.setString(2, "System Admin");
            pstmt.setString(3, "admin@railsafar.com");
            pstmt.setString(4, "0300-0000000");
            pstmt.setString(5, "admin");
            pstmt.setString(6, "admin123");
            pstmt.setString(7, "35202-1234567-1");
            pstmt.setDate(8, Date.valueOf(LocalDate.of(1985, 5, 12)));
            pstmt.setString(9, "Male");
            pstmt.setString(10, "HQ, Rail Safar Building");
            pstmt.setString(11, "Karachi");
            pstmt.setString(12, "75500");
            pstmt.executeUpdate();

            // Sample passenger
            pstmt.setString(1, "101");
            pstmt.setString(2, "Sarah Khan");
            pstmt.setString(3, "sarah.khan@example.com");
            pstmt.setString(4, "0300-1111111");
            pstmt.setString(5, "passenger");
            pstmt.setString(6, "password1");
            pstmt.setString(7, "35201-9876543-2");
            pstmt.setDate(8, Date.valueOf(LocalDate.of(1995, 8, 20)));
            pstmt.setString(9, "Female");
            pstmt.setString(10, "123 Main Street");
            pstmt.setString(11, "Lahore");
            pstmt.setString(12, "54000");
            pstmt.executeUpdate();
        }
    }

    private static void seedTrains() throws SQLException {
        String sql = "INSERT OR IGNORE INTO trains (id, train_number, train_name, type, route, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String[][] trains = {
                {"1", "1UP", "Karachi Express", "Express", "Karachi - Lahore", "On-time"},
                {"2", "2DN", "Lahore Express", "Express", "Lahore - Karachi", "Delayed"},
                {"3", "3UP", "Green Line", "Passenger", "Islamabad - Multan", "On-time"},
                {"4", "4DN", "Freight Express", "Freight", "Port Qasim - Faisalabad", "Cancelled"},
                {"5", "5UP", "Business Express", "Express", "Rawalpindi - Quetta", "On-time"},
                {"6", "6DN", "Peshawar Mail", "Passenger", "Peshawar - Karachi", "Delayed"}
            };

            for (String[] train : trains) {
                pstmt.setString(1, train[0]);
                pstmt.setString(2, train[1]);
                pstmt.setString(3, train[2]);
                pstmt.setString(4, train[3]);
                pstmt.setString(5, train[4]);
                pstmt.setString(6, train[5]);
                pstmt.executeUpdate();
            }
        }
    }

    private static void seedSchedules() throws SQLException {
        String sql = "INSERT OR IGNORE INTO schedules (id, train_number, train_name, departure_time, arrival_time, route, days, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String[][] schedules = {
                {"1", "1UP", "Karachi Express", "08:00 AM", "08:00 PM", "Karachi - Lahore", "Daily", "Active"},
                {"2", "2DN", "Lahore Express", "09:00 AM", "09:00 PM", "Lahore - Karachi", "Daily", "Active"},
                {"3", "3UP", "Green Line", "10:30 AM", "06:30 PM", "Islamabad - Multan", "Mon-Fri", "Active"},
                {"4", "5UP", "Business Express", "07:00 AM", "05:00 PM", "Rawalpindi - Quetta", "Daily", "Active"},
                {"5", "6DN", "Peshawar Mail", "11:00 AM", "11:00 PM", "Peshawar - Karachi", "Daily", "Active"}
            };

            for (String[] schedule : schedules) {
                pstmt.setString(1, schedule[0]);
                pstmt.setString(2, schedule[1]);
                pstmt.setString(3, schedule[2]);
                pstmt.setString(4, schedule[3]);
                pstmt.setString(5, schedule[4]);
                pstmt.setString(6, schedule[5]);
                pstmt.setString(7, schedule[6]);
                pstmt.setString(8, schedule[7]);
                pstmt.executeUpdate();
            }
        }
    }

    private static void seedBookings() throws SQLException {
        // Only seed if no bookings exist
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM bookings")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        String sql = "INSERT INTO bookings (id, user_id, train_id, train_number, train_name, from_station, to_station, travel_date, number_of_seats, seat_class, total_amount, status, booking_date_time, payment_method, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Sample paid booking
            pstmt.setString(1, "400");
            pstmt.setString(2, "101");
            pstmt.setString(3, "1");
            pstmt.setString(4, "1UP");
            pstmt.setString(5, "Karachi Express");
            pstmt.setString(6, "Karachi");
            pstmt.setString(7, "Lahore");
            pstmt.setDate(8, Date.valueOf(LocalDate.now().plusDays(2)));
            pstmt.setInt(9, 2);
            pstmt.setString(10, "Economy");
            pstmt.setDouble(11, 5000);
            pstmt.setString(12, "Confirmed");
            pstmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
            pstmt.setString(14, "Card");
            pstmt.setString(15, "Paid");
            pstmt.executeUpdate();
        }
    }

    public static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }
}
