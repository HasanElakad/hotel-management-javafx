package com.hotel.management.javafx.controller;

import com.hotel.management.javafx.App;
import com.hotel.management.javafx.db.RoomDAO;
import com.hotel.management.javafx.model.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class RoomReservationController {
    
    @FXML private Label roomTypeLabel;
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> roomNumberColumn;
    @FXML private TableColumn<Room, String> roomTypeColumn;
    @FXML private TableColumn<Room, Double> basePriceColumn;
    @FXML private TableColumn<Room, String> statusColumn;
    @FXML private Button backButton;
    
    private RoomDAO roomDAO;
    private ObservableList<Room> roomsList;
    private String selectedRoomType;
    
    @FXML
    public void initialize() {
        roomDAO = new RoomDAO();
        roomsList = FXCollections.observableArrayList();
        
        setupTableColumns();
        setupBackButton();
    }
    
    /**
     * Called from DashboardController to set which room type to display
     */
    public void setRoomType(String roomType) {
        this.selectedRoomType = roomType;
        
        // Update the label
        if (roomTypeLabel != null) {
            roomTypeLabel.setText(roomType + " Rooms");
        }
        
        // Load rooms of this type from database
        loadRoomsByType(roomType);
    }
    
    private void setupTableColumns() {
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        basePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Format price column
        basePriceColumn.setCellFactory(col -> new TableCell<Room, Double>() {
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
        
        // Format status column with colors
        statusColumn.setCellFactory(col -> new TableCell<Room, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    // Color code the status
                    switch (status) {
                        case "Available":
                            setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                            break;
                        case "Occupied":
                            setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                            break;
                        case "Reserved":
                            setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                            break;
                        case "Cleaning":
                            setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                            break;
                        case "Maintenance":
                            setStyle("-fx-text-fill: #8b5cf6; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #9ca3af;");
                    }
                }
            }
        });
    }
    
    private void setupBackButton() {
        // Find back button in scene and set action
        if (backButton != null) {
            backButton.setOnAction(e -> handleBack());
        }
    }
    
    private void loadRoomsByType(String roomType) {
        try {
            List<Room> rooms = roomDAO.getRoomsByType(roomType);
            roomsList.setAll(rooms);
            roomsTable.setItems(roomsList);
            
            System.out.println("Loaded " + rooms.size() + " rooms of type: " + roomType);
            
            if (rooms.isEmpty()) {
                showInfo("No Rooms Found", "No " + roomType + " rooms found in the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load rooms: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            App.setRoot("dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to return to dashboard: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleReserveRoom() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        
        if (selectedRoom == null) {
            showInfo("No Selection", "Please select a room to reserve.");
            return;
        }
        
        if (!selectedRoom.isAvailable()) {
            showInfo("Room Unavailable", "This room is not available for reservation. Status: " + selectedRoom.getStatus());
            return;
        }
        
        // TODO: Open reservation dialog/form
        showInfo("Reservation", "Reservation form for room " + selectedRoom.getRoomNumber() + " will be implemented here.\n\n" +
                "Room Details:\n" +
                "Type: " + selectedRoom.getRoomType() + "\n" +
                "Capacity: " + selectedRoom.getCapacity() + " guests\n" +
                "Price: $" + String.format("%.2f", selectedRoom.getPrice()) + "/night\n" +
                "Floor: " + selectedRoom.getFloor());
    }
    
    @FXML
    private void handleRefresh() {
        if (selectedRoomType != null) {
            loadRoomsByType(selectedRoomType);
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}