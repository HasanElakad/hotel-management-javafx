package com.hotel.management.javafx.db;

import com.hotel.management.javafx.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Returns a User if username/password are valid, otherwise null.
     */
    public User findByCredentials(String username, String password) {
        String sql = "SELECT user_id, username, role FROM users WHERE username = ? AND password = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("user_id");
                String uname = rs.getString("username");
                String role = rs.getString("role");
                return new User(id, uname, role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}