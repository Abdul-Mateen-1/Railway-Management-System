package com.example.railwaymanagementsystem.dao;

import com.example.railwaymanagementsystem.models.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingDAO {
    private Connection connection;

    public BookingDAO() {
        this.connection = ConnectionManager.getConnection();
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookings.add(mapBookingFromResultSet(rs));
            }
        }
        return bookings;
    }

    public Optional<Booking> findBookingById(String id) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapBookingFromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Booking addBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (id, user_id, train_id, train_number, train_name, from_station, to_station, travel_date, number_of_seats, seat_class, total_amount, status, booking_date_time, payment_method, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setBookingParameters(pstmt, booking);
            pstmt.executeUpdate();
        }
        return booking;
    }

    public boolean updateBooking(Booking booking) throws SQLException {
        String sql = "UPDATE bookings SET user_id = ?, train_id = ?, train_number = ?, train_name = ?, from_station = ?, to_station = ?, travel_date = ?, number_of_seats = ?, seat_class = ?, total_amount = ?, status = ?, booking_date_time = ?, payment_method = ?, payment_status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, booking.getUserId());
            pstmt.setString(2, booking.getTrainId());
            pstmt.setString(3, booking.getTrainNumber());
            pstmt.setString(4, booking.getTrainName());
            pstmt.setString(5, booking.getFromStation());
            pstmt.setString(6, booking.getToStation());
            pstmt.setDate(7, Date.valueOf(booking.getTravelDate()));
            pstmt.setInt(8, booking.getNumberOfSeats());
            pstmt.setString(9, booking.getSeatClass());
            pstmt.setDouble(10, booking.getTotalAmount());
            pstmt.setString(11, booking.getStatus());
            pstmt.setTimestamp(12, Timestamp.valueOf(booking.getBookingDateTime()));
            pstmt.setString(13, booking.getPaymentMethod());
            pstmt.setString(14, booking.getPaymentStatus());
            pstmt.setString(15, booking.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public String getNextBookingId() throws SQLException {
        return String.valueOf(getMaxId("bookings", "id") + 1);
    }

    private Booking mapBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking(
            rs.getString("id"),
            rs.getString("user_id"),
            rs.getString("train_id"),
            rs.getString("train_number"),
            rs.getString("train_name"),
            rs.getString("from_station"),
            rs.getString("to_station"),
            rs.getDate("travel_date").toLocalDate(),
            rs.getInt("number_of_seats"),
            rs.getString("seat_class"),
            rs.getDouble("total_amount"),
            rs.getString("status"),
            rs.getTimestamp("booking_date_time").toLocalDateTime(),
            rs.getString("payment_method") != null ? rs.getString("payment_method") : "",
            rs.getString("payment_status") != null ? rs.getString("payment_status") : "Pending"
        );
        return booking;
    }

    private void setBookingParameters(PreparedStatement pstmt, Booking booking) throws SQLException {
        pstmt.setString(1, booking.getId());
        pstmt.setString(2, booking.getUserId());
        pstmt.setString(3, booking.getTrainId());
        pstmt.setString(4, booking.getTrainNumber());
        pstmt.setString(5, booking.getTrainName());
        pstmt.setString(6, booking.getFromStation());
        pstmt.setString(7, booking.getToStation());
        pstmt.setDate(8, Date.valueOf(booking.getTravelDate()));
        pstmt.setInt(9, booking.getNumberOfSeats());
        pstmt.setString(10, booking.getSeatClass());
        pstmt.setDouble(11, booking.getTotalAmount());
        pstmt.setString(12, booking.getStatus());
        pstmt.setTimestamp(13, Timestamp.valueOf(booking.getBookingDateTime()));
        pstmt.setString(14, booking.getPaymentMethod());
        pstmt.setString(15, booking.getPaymentStatus());
    }

    private int getMaxId(String table, String idColumn) throws SQLException {
        String sql = "SELECT MAX(CAST(" + idColumn + " AS INTEGER)) FROM " + table;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next() && rs.getObject(1) != null) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
