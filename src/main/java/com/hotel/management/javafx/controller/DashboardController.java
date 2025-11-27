package com.hotel.management.javafx.controller;

import com.hotel.management.javafx.App;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label signedInLabel;

    // Optional: called from LoginController
    public void initUser(String username, String role) {
        if (signedInLabel != null) {
            signedInLabel.setText("Signed in as " + role);
        }
    }

    @FXML
    private void goDashboard() {
        try {
            App.setRoot("dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goReservations() {
        try {
            App.setRoot("reservation");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
        try {
            App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}