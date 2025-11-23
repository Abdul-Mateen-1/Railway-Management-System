package com.example.railwaymanagementsystem.dao;

import com.example.railwaymanagementsystem.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = ConnectionManager.getConnection();
    }

    public Optional<User> findUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUserFromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> findUserById(String id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUserFromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapUserFromResultSet(rs));
            }
        }
        return users;
    }

    public User addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (id, name, email, phone, role, password, cnic, date_of_birth, gender, address, city, postal_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // This helper is safe for INSERT
            setUserParametersForInsert(pstmt, user);
            pstmt.executeUpdate();
        }
        return user;
    }

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, role = ?, password = ?, cnic = ?, date_of_birth = ?, gender = ?, address = ?, city = ?, postal_code = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Set parameters in the correct order for the UPDATE statement
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getCnic());
            if (user.getDateOfBirth() != null) {
                pstmt.setDate(7, Date.valueOf(user.getDateOfBirth()));
            } else {
                pstmt.setNull(7, Types.DATE);
            }
            pstmt.setString(8, user.getGender());
            pstmt.setString(9, user.getAddress());
            pstmt.setString(10, user.getCity());
            pstmt.setString(11, user.getPostalCode());
            // Set the ID for the WHERE clause
            pstmt.setString(12, user.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean removeUser(String id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean emailExists(String email, String excludeUserId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?) AND id != ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, excludeUserId != null ? excludeUserId : "");
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public String getNextUserId() throws SQLException {
        return String.valueOf(getMaxId("users", "id") + 1);
    }

    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("role"),
            rs.getString("password")
        );
        user.setCnic(rs.getString("cnic"));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            user.setDateOfBirth(dob.toLocalDate());
        }
        user.setGender(rs.getString("gender"));
        user.setAddress(rs.getString("address"));
        user.setCity(rs.getString("city"));
        user.setPostalCode(rs.getString("postal_code"));
        return user;
    }

    private void setUserParametersForInsert(PreparedStatement pstmt, User user) throws SQLException {
        pstmt.setString(1, user.getId());
        pstmt.setString(2, user.getName());
        pstmt.setString(3, user.getEmail());
        pstmt.setString(4, user.getPhone());
        pstmt.setString(5, user.getRole());
        pstmt.setString(6, user.getPassword());
        pstmt.setString(7, user.getCnic());
        if (user.getDateOfBirth() != null) {
            pstmt.setDate(8, Date.valueOf(user.getDateOfBirth()));
        } else {
            pstmt.setNull(8, Types.DATE);
        }
        pstmt.setString(9, user.getGender());
        pstmt.setString(10, user.getAddress());
        pstmt.setString(11, user.getCity());
        pstmt.setString(12, user.getPostalCode());
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
