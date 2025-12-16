package com.hotel.management.javafx.controller;

import com.hotel.management.javafx.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import io.github.palexdev.materialfx.controls.MFXButton;

public class DashboardController {

    @FXML private Label signedInLabel;
    @FXML private MFXButton dashboardBtn;
    @FXML private MFXButton reservationsBtn;
    @FXML private MFXButton logoutBtn;

    @FXML
    public void initialize() {
        System.out.println("Dashboard loaded successfully");
    }

    // Optional: called from LoginController
    public void initUser(String username, String role) {
        if (signedInLabel != null) {
            signedInLabel.setText("Signed in as " + role);
        }
    }

    @FXML
    private void goDashboard() {
        // Already on dashboard
        System.out.println("Already on Dashboard");
    }

    @FXML
    private void goReservations() {
        try {
            App.setRoot("reservations");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open Reservations page. Make sure 'reservations.fxml' exists.");
        }
    }

    @FXML
    private void logout() {
        try {
            App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to logout");
        }
    }
    
    // View room methods for each room type
    @FXML
    private void viewSingleRooms() {
        openRoomView("Single");
    }
    
    @FXML
    private void viewDoubleRooms() {
        openRoomView("Double");
    }
    
    @FXML
    private void viewTripleRooms() {
        openRoomView("Triple");
    }
    
    @FXML
    private void viewSuiteRooms() {
        openRoomView("Suite");
    }
    
    private void openRoomView(String roomType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/management/javafx/RoomReservationView.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the room type
            RoomReservationController controller = loader.getController();
            controller.setRoomType(roomType);
            
            // Set the new scene
            App.scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open room view: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Navigation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}