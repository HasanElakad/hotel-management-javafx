package com.hotel.management.javafx.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ReservationsController {
    
    // Sidebar buttons
    @FXML private MFXButton dashboardBtn;
    @FXML private MFXButton reservationsBtn;
    @FXML private MFXButton logoutBtn;
    @FXML private Label userNameLabel;
    
    // Stats labels
    @FXML private Label totalReservationsLabel;
    @FXML private Label activeReservationsLabel;
    @FXML private Label checkedOutLabel;
    @FXML private Label cancelledLabel;
    
    // Table and columns - MATCHING YOUR DATABASE
    @FXML private TableView<?> reservationsTable;
    @FXML private TableColumn<?, ?> reservationIdColumn;  // reservation_id
    @FXML private TableColumn<?, ?> roomIdColumn;         // room_id
    @FXML private TableColumn<?, ?> guestNameColumn;      // guest_name
    @FXML private TableColumn<?, ?> guestPhoneColumn;     // guest_phone
    @FXML private TableColumn<?, ?> checkInColumn;        // check_in
    @FXML private TableColumn<?, ?> checkOutColumn;       // check_out
    @FXML private TableColumn<?, ?> totalPriceColumn;     // total_price
    @FXML private TableColumn<?, ?> statusColumn;         // status
    @FXML private TableColumn<?, ?> actionsColumn;        // For Extend/Cancel buttons
    
    @FXML
    public void initialize() {
        System.out.println("Reservations View Loaded!");
        
        // Backend team: Add your code here
    }
}
