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
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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
    
    // Table and columns
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
                refreshData();
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
        
        roomIdColumn.setCellValueFactory(cellData -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getRoom().getRoomNumber()
                );
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });
        
        guestNameColumn.setCellValueFactory(cellData -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getGuest().getName()
                );
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });
        
        guestPhoneColumn.setCellValueFactory(cellData -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getGuest().getPhoneNumber()
                );
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });
        
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        
        statusColumn.setCellValueFactory(cellData -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getRoom().getStatus()
                );
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            }
        });
        
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
                
                // Style the buttons
                extendBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                                  "-fx-background-radius: 6; -fx-font-size: 11px; -fx-padding: 4 10;");
                cancelBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                                  "-fx-background-radius: 6; -fx-font-size: 11px; -fx-padding: 4 10;");
                
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
        try {
            System.out.println("Loading reservations from database...");
            List<Reservation> reservations = reservationDAO.getAllReservations();
            
            reservationsList.clear();
            reservationsList.addAll(reservations);
            
            if (reservationsTable != null) {
                reservationsTable.setItems(reservationsList);
            }
            
            System.out.println("✓ Loaded " + reservations.size() + " reservations into table");
            
        } catch (Exception e) {
            System.err.println("Error loading reservations: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to load reservations: " + e.getMessage());
        }
    }
    
    private void updateStats() {
        try {
            if (totalReservationsLabel != null) {
                totalReservationsLabel.setText(String.valueOf(reservationsList.size()));
            }
            
            if (activeReservationsLabel != null) {
                long activeCount = reservationsList.stream()
                    .filter(r -> {
                        try {
                            String status = r.getRoom().getStatus();
                            return "Reserved".equals(status) || "Occupied".equals(status);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .count();
                activeReservationsLabel.setText(String.valueOf(activeCount));
            }
            
            if (checkedOutLabel != null) {
                long checkedOutCount = reservationsList.stream()
                    .filter(r -> {
                        try {
                            String status = r.getRoom().getStatus();
                            return "Cleaning".equals(status) || "Available".equals(status);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .count();
                checkedOutLabel.setText(String.valueOf(checkedOutCount));
            }
            
            if (cancelledLabel != null) {
                // You might want to add a cancelled status to track this
                cancelledLabel.setText("0");
            }
        } catch (Exception e) {
            System.err.println("Error updating stats: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleExtend(Reservation reservation) {
        // Create custom dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Extend Reservation");
        dialog.setHeaderText("Extend Reservation for " + reservation.getGuest().getName());
        
        // Set button types
        ButtonType extendButtonType = new ButtonType("Extend Reservation", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(extendButtonType, ButtonType.CANCEL);
        
        // Create content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20, 20, 20, 20));
        
        // Display current reservation info
        Label infoLabel = new Label("Current Reservation Details:");
        infoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        
        Label roomLabel = new Label("Room: " + reservation.getRoom().getRoomNumber() + 
                                    " (" + reservation.getRoom().getRoomType() + ")");
        Label checkInLabel = new Label("Check-In: " + reservation.getCheckInDate());
        Label currentCheckOutLabel = new Label("Current Check-Out: " + reservation.getCheckOutDate());
        Label currentPriceLabel = new Label("Current Total: $" + String.format("%.2f", reservation.getTotalPrice()));
        
        // Separator
        Label separator = new Label("────────────────────────────");
        separator.setStyle("-fx-text-fill: #6b7280;");
        
        // New check-out date picker
        Label newDateLabel = new Label("Select New Check-Out Date:");
        newDateLabel.setStyle("-fx-font-weight: bold;");
        
        DatePicker newCheckOutDate = new DatePicker();
        newCheckOutDate.setValue(reservation.getCheckOutDate().plusDays(1)); // Default to 1 day extension
        newCheckOutDate.setPromptText("Select new check-out date");
        
        // Price calculation label
        Label newPriceLabel = new Label("New Total: $" + String.format("%.2f", reservation.getTotalPrice()));
        newPriceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #22c55e;");
        
        // Update price when date changes
        newCheckOutDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isAfter(reservation.getCheckInDate())) {
                long totalNights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), newVal);
                double pricePerNight = reservation.getRoom().getPrice();
                double newTotal = totalNights * pricePerNight;
                newPriceLabel.setText("New Total: $" + String.format("%.2f", newTotal) + 
                                     " (" + totalNights + " nights)");
            }
        });
        
        content.getChildren().addAll(
            infoLabel, roomLabel, checkInLabel, currentCheckOutLabel, currentPriceLabel,
            separator, newDateLabel, newCheckOutDate, newPriceLabel
        );
        
        dialog.getDialogPane().setContent(content);
        
        // Show dialog and handle result
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == extendButtonType) {
            LocalDate newDate = newCheckOutDate.getValue();
            
            // Validate new date
            if (newDate == null) {
                showError("Please select a new check-out date.");
                return;
            }
            
            if (!newDate.isAfter(reservation.getCheckOutDate())) {
                showError("New check-out date must be after the current check-out date (" + 
                         reservation.getCheckOutDate() + ")");
                return;
            }
            
            // Calculate new total price
            long totalNights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), newDate);
            double pricePerNight = reservation.getRoom().getPrice();
            double newTotal = totalNights * pricePerNight;
            
            // Update reservation in database
            boolean success = reservationDAO.extendReservation(
                reservation.getReservationId(), 
                newDate, 
                newTotal
            );
            
            if (success) {
                showSuccess("Reservation Extended!", 
                    "Check-out date extended to " + newDate + "\n" +
                    "New total: $" + String.format("%.2f", newTotal));
                refreshData();
            } else {
                showError("Failed to extend reservation. Please try again.");
            }
        }
    }
    
    private void handleCancel(Reservation reservation) {
        // Create confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel Reservation");
        confirmation.setHeaderText("Cancel Reservation");
        
        // Build detailed confirmation message
        String message = "Are you sure you want to cancel this reservation?\n\n" +
                        "Guest: " + reservation.getGuest().getName() + "\n" +
                        "Room: " + reservation.getRoom().getRoomNumber() + "\n" +
                        "Check-In: " + reservation.getCheckInDate() + "\n" +
                        "Check-Out: " + reservation.getCheckOutDate() + "\n" +
                        "Total: $" + String.format("%.2f", reservation.getTotalPrice()) + "\n\n" +
                        "This action cannot be undone.";
        
        confirmation.setContentText(message);
        
        // Style the buttons
        ButtonType yesButton = new ButtonType("Yes, Cancel Reservation", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("No, Keep Reservation", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmation.getButtonTypes().setAll(yesButton, noButton);
        
        Optional<ButtonType> result = confirmation.showAndWait();
        
        if (result.isPresent() && result.get() == yesButton) {
            boolean cancelled = reservationDAO.cancelReservation(reservation.getReservationId());
            
            if (cancelled) {
                showSuccess("Reservation Cancelled!", 
                    "Reservation for " + reservation.getGuest().getName() + " has been cancelled.\n" +
                    "Room " + reservation.getRoom().getRoomNumber() + " is now available.");
                refreshData();
            } else {
                showError("Failed to cancel reservation. Please try again.");
            }
        }
    }
    
    @FXML
    private void refreshData() {
        System.out.println("Refreshing reservations data...");
        loadReservations();
        updateStats();
        System.out.println("✓ Reservations refreshed");
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
        refreshData();
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
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}