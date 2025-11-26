package com.hotel.management.javafx.controller;

import com.hotel.management.javafx.db.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password");
            return;
        }

        String role = checkLogin(username, password);
        if (role != null) {
            errorLabel.setText("Login successful! Role: " + role);
            // TODO: App.setRoot("dashboard");
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    private String checkLogin(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            errorLabel.setText("DB connection is null");
            return null;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("DB error: " + e.getMessage());
        }
        return null;
    }
}
