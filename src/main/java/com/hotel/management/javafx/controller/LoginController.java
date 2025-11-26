package com.hotel.management.javafx.controller;

import com.hotel.management.javafx.db.UserDAO;
import com.hotel.management.javafx.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private final UserDAO userDAO = new UserDAO();

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

        User user = userDAO.findByCredentials(username, password);

        if (user != null) {
            errorLabel.setText("Login successful! Role: " + user.getRole());
            // TODO: App.setRoot("dashboard");
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }
}
