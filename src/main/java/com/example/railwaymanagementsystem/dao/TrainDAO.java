package com.example.railwaymanagementsystem.dao;

import com.example.railwaymanagementsystem.models.Train;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainDAO {
    private Connection connection;

    public TrainDAO() {
        this.connection = ConnectionManager.getConnection();
    }

    public List<Train> getAllTrains() throws SQLException {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT * FROM trains";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                trains.add(mapTrainFromResultSet(rs));
            }
        }
        return trains;
    }

    public Optional<Train> findTrainById(String id) throws SQLException {
        String sql = "SELECT * FROM trains WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapTrainFromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Train> findTrainByNumber(String trainNumber) throws SQLException {
        String sql = "SELECT * FROM trains WHERE train_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, trainNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapTrainFromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Train addTrain(Train train) throws SQLException {
        String sql = "INSERT INTO trains (id, train_number, train_name, type, route, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, train.getId());
            pstmt.setString(2, train.getTrainNumber());
            pstmt.setString(3, train.getTrainName());
            pstmt.setString(4, train.getType());
            pstmt.setString(5, train.getRoute());
            pstmt.setString(6, train.getStatus());
            pstmt.executeUpdate();
        }
        return train;
    }

    public boolean updateTrain(Train train) throws SQLException {
        String sql = "UPDATE trains SET train_number = ?, train_name = ?, type = ?, route = ?, status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, train.getTrainNumber());
            pstmt.setString(2, train.getTrainName());
            pstmt.setString(3, train.getType());
            pstmt.setString(4, train.getRoute());
            pstmt.setString(5, train.getStatus());
            pstmt.setString(6, train.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean removeTrain(String id) throws SQLException {
        String sql = "DELETE FROM trains WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public String getNextTrainId() throws SQLException {
        return String.valueOf(getMaxId("trains", "id") + 1);
    }

    private Train mapTrainFromResultSet(ResultSet rs) throws SQLException {
        return new Train(
            rs.getString("id"),
            rs.getString("train_number"),
            rs.getString("train_name"),
            rs.getString("type"),
            rs.getString("route"),
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
