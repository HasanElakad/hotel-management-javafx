package com.hotel.management.javafx.controller;

import com.hotel.management.javafx.App;
import com.hotel.management.javafx.db.ReservationDAO;
import com.hotel.management.javafx.model.Reservation;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.time.LocalDate;
import java.util.List;

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
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> reservationIdColumn;
    @FXML private TableColumn<Reservation, String> roomIdColumn;
    @FXML private TableColumn<Reservation, String> guestNameColumn;
    @FXML private TableColumn<Reservation, String> guestPhoneColumn;
    @FXML private TableColumn<Reservation, LocalDate> checkInColumn;
    @FXML private TableColumn<Reservation, LocalDate> checkOutColumn;
    @FXML private TableColumn<Reservation, Double> totalPriceColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private TableColumn<Reservation, Void> actionsColumn;
    
    private ReservationDAO reservationDAO;
    private ObservableList<Reservation> reservationsList;
    
    @FXML
    public void initialize() {
        System.out.println("Reservations View Loaded!");
        
        reservationDAO = new ReservationDAO();
        reservationsList = FXCollections.observableArrayList();
        
        // Set up button actions
        setupNavigationButtons();
        
        // Set up table columns
        setupTableColumns();
        
        // Load reservations from database
        loadReservations();
        
        // Update stats
        updateStats();
    }
    
    private void setupNavigationButtons() {
        // Dashboard button - goes back to dashboard
        if (dashboardBtn != null) {
            dashboardBtn.setOnAction(event -> goDashboard());
        }
        
        // Reservations button - stays on current page
        if (reservationsBtn != null) {
            reservationsBtn.setOnAction(event -> {
                // Already on reservations page, refresh data
                loadReservations();
                updateStats();
            });
        }
        
        // Logout button - returns to login
        if (logoutBtn != null) {
            logoutBtn.setOnAction(event -> logout());
        }
    }
    
    private void setupTableColumns() {
        // Set up cell value factories
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        roomIdColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getRoom().getRoomNumber()
            )
        );
        guestNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getGuest().getName()
            )
        );
        guestPhoneColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getGuest().getPhoneNumber()
            )
        );
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getRoom().getStatus()
            )
        );
        
        // Format price column
        totalPriceColumn.setCellFactory(col -> new TableCell<Reservation, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });
        
        // Set up actions column with buttons
        actionsColumn.setCellFactory(col -> new TableCell<Reservation, Void>() {
            private final Button extendBtn = new Button("Extend");
            private final Button cancelBtn = new Button("Cancel");
            private final HBox pane = new HBox(5, extendBtn, cancelBtn);
            
            {
                pane.setAlignment(Pos.CENTER);
                extendBtn.setOnAction(e -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleExtend(reservation);
                });
                cancelBtn.setOnAction(e -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleCancel(reservation);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }
    
    private void loadReservations() {
        List<Reservation> reservations = reservationDAO.getAllReservations();
        reservationsList.setAll(reservations);
        reservationsTable.setItems(reservationsList);
    }
    
    private void updateStats() {
        if (totalReservationsLabel != null) {
            totalReservationsLabel.setText(String.valueOf(reservationsList.size()));
        }
        
        if (activeReservationsLabel != null) {
            long activeCount = reservationsList.stream()
                .filter(r -> "Reserved".equals(r.getRoom().getStatus()) || 
                            "Occupied".equals(r.getRoom().getStatus()))
                .count();
            activeReservationsLabel.setText(String.valueOf(activeCount));
        }
        
        if (checkedOutLabel != null) {
            long checkedOutCount = reservationsList.stream()
                .filter(r -> "Cleaning".equals(r.getRoom().getStatus()) || 
                            "Available".equals(r.getRoom().getStatus()))
                .count();
            checkedOutLabel.setText(String.valueOf(checkedOutCount));
        }
        
        if (cancelledLabel != null) {
            // You might want to add a cancelled status to track this
            cancelledLabel.setText("0");
        }
    }
    
    private void handleExtend(Reservation reservation) {
        // TODO: Implement extend reservation functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Extend Reservation");
        alert.setHeaderText("Extend Reservation for " + reservation.getGuest().getName());
        alert.setContentText("Extend functionality will be implemented here.");
        alert.showAndWait();
    }
    
    private void handleCancel(Reservation reservation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel Reservation");
        confirmation.setHeaderText("Cancel reservation for " + reservation.getGuest().getName() + "?");
        confirmation.setContentText("This action cannot be undone.");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean cancelled = reservationDAO.cancelReservation(reservation.getReservationId());
                if (cancelled) {
                    loadReservations();
                    updateStats();
                    showSuccess("Reservation cancelled successfully!");
                } else {
                    showError("Failed to cancel reservation");
                }
            }
        });
    }
    
    @FXML
    private void refreshData() {
        loadReservations();
        updateStats();
    }
    
    @FXML
    private void goDashboard() {
        try {
            App.setRoot("dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to navigate to Dashboard");
        }
    }
    
    @FXML
    private void goReservations() {
        // Already on reservations page
        loadReservations();
        updateStats();
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
    
    @FXML
    private void handleBack() {
        goDashboard();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}