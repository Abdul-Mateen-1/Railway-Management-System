package com.example.railwaymanagementsystem.dao;

import com.example.railwaymanagementsystem.models.Schedule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScheduleDAO {
    private Connection connection;

    public ScheduleDAO() {
        this.connection = ConnectionManager.getConnection();
    }

    public List<Schedule> getAllSchedules() throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM schedules";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                schedules.add(mapScheduleFromResultSet(rs));
            }
        }
        return schedules;
    }

    public Optional<Schedule> findScheduleByTrainNumber(String trainNumber) throws SQLException {
        String sql = "SELECT * FROM schedules WHERE train_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, trainNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapScheduleFromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Schedule addSchedule(Schedule schedule) throws SQLException {
        String sql = "INSERT INTO schedules (id, train_number, train_name, departure_time, arrival_time, route, days, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, schedule.getId());
            pstmt.setString(2, schedule.getTrainNumber());
            pstmt.setString(3, schedule.getTrainName());
            pstmt.setString(4, schedule.getDepartureTime());
            pstmt.setString(5, schedule.getArrivalTime());
            pstmt.setString(6, schedule.getRoute());
            pstmt.setString(7, schedule.getDays());
            pstmt.setString(8, schedule.getStatus());
            pstmt.executeUpdate();
        }
        return schedule;
    }

    public boolean updateSchedule(Schedule schedule) throws SQLException {
        String sql = "UPDATE schedules SET train_number = ?, train_name = ?, departure_time = ?, arrival_time = ?, route = ?, days = ?, status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, schedule.getTrainNumber());
            pstmt.setString(2, schedule.getTrainName());
            pstmt.setString(3, schedule.getDepartureTime());
            pstmt.setString(4, schedule.getArrivalTime());
            pstmt.setString(5, schedule.getRoute());
            pstmt.setString(6, schedule.getDays());
            pstmt.setString(7, schedule.getStatus());
            pstmt.setString(8, schedule.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean removeSchedule(String id) throws SQLException {
        String sql = "DELETE FROM schedules WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public String getNextScheduleId() throws SQLException {
        return String.valueOf(getMaxId("schedules", "id") + 1);
    }

    private Schedule mapScheduleFromResultSet(ResultSet rs) throws SQLException {
        return new Schedule(
            rs.getString("id"),
            rs.getString("train_number"),
            rs.getString("train_name"),
            rs.getString("departure_time"),
            rs.getString("arrival_time"),
            rs.getString("route"),
            rs.getString("days"),
            rs.getString("status")
        );
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
