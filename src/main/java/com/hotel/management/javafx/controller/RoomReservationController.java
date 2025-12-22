package com.hotel.management.javafx.controller;

import java.util.UUID;
import com.hotel.management.javafx.App;
import com.hotel.management.javafx.db.RoomDAO;
import com.hotel.management.javafx.db.ReservationDAO;
import com.hotel.management.javafx.model.Room;
import com.hotel.management.javafx.model.Reservation;
import com.hotel.management.javafx.model.Guest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
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
    private ReservationDAO reservationDAO;
    private ObservableList<Room> roomsList;
    private String selectedRoomType;
    
    @FXML
    public void initialize() {
        roomDAO = new RoomDAO();
        reservationDAO = new ReservationDAO();
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
        showInfo("Room Unavailable", "This room is not available. Status: " + selectedRoom.getStatus());
        return;
    }

    // Open the Reservation Form Dialog
    showReservationDialog(selectedRoom);
}
    @FXML
    private void showReservationDialog(Room room) {
    // 1. Create the Dialog
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("New Reservation");
    dialog.setHeaderText("Reserve Room " + room.getRoomNumber() + " (" + room.getRoomType() + ")");

    // 2. Set the buttons (Save and Cancel)
    ButtonType loginButtonType = new ButtonType("Confirm Reservation", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

    // 3. Create the Form Layout
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    // grid.setPadding(new Insets(20, 150, 10, 10)); // Add padding import if needed

    // 4. Create UI Controls
    TextField ssnField = new TextField();
    ssnField.setPromptText("Guest SSN (Min 14 chars)");

    TextField nameField = new TextField();
    nameField.setPromptText("Full Name");

    TextField phoneField = new TextField();
    phoneField.setPromptText("Phone Number");

    TextField emailField = new TextField();
    emailField.setPromptText("Email Address");

    DatePicker checkInDate = new DatePicker();
    DatePicker checkOutDate = new DatePicker();

    Label totalPriceLabel = new Label("$" + room.getPrice()); // Default to base price

    // 5. Add controls to Grid
    grid.add(new Label("Guest SSN:"), 0, 0);
    grid.add(ssnField, 1, 0);
    grid.add(new Label("Guest Name:"), 0, 1);
    grid.add(nameField, 1, 1);
    grid.add(new Label("Phone:"), 0, 2);
    grid.add(phoneField, 1, 2);
    grid.add(new Label("Email:"), 0, 3);
    grid.add(emailField, 1, 3);
    grid.add(new Label("Check-In:"), 0, 4);
    grid.add(checkInDate, 1, 4);
    grid.add(new Label("Check-Out:"), 0, 5);
    grid.add(checkOutDate, 1, 5);
    grid.add(new Label("Total Price:"), 0, 6);
    grid.add(totalPriceLabel, 1, 6);

    dialog.getDialogPane().setContent(grid);

    // --- LOGIC: Auto-calculate Total Price when dates change ---
    Runnable updatePrice = () -> {
        if (checkInDate.getValue() != null && checkOutDate.getValue() != null) {
            long days = ChronoUnit.DAYS.between(checkInDate.getValue(), checkOutDate.getValue());
            if (days > 0) {
                double total = days * room.getPrice();
                totalPriceLabel.setText("$" + String.format("%.2f", total));
            } else {
                totalPriceLabel.setText("Invalid Dates");
            }
        }
    };
    checkInDate.valueProperty().addListener((obs, oldVal, newVal) -> updatePrice.run());
    checkOutDate.valueProperty().addListener((obs, oldVal, newVal) -> updatePrice.run());

    // 6. Show Dialog and wait for result
    Optional<ButtonType> result = dialog.showAndWait();

    if (result.isPresent() && result.get() == loginButtonType) {
    // Basic empty checks
    if (nameField.getText().isEmpty() ||
        ssnField.getText().isEmpty() ||
        checkInDate.getValue() == null ||
        checkOutDate.getValue() == null) {
        showInfo("Error", "Please fill in all required fields.");
        return;
    }

    // Extra SSN validation to match Guest rules
    String ssn = ssnField.getText().trim();
    if (ssn.length() < 14) {
        showInfo("Invalid SSN", "SSN must be at least 14 characters.");
        return;
    }

    // Optional: phone / email checks before constructing Guest

    try {
        Guest guest = new Guest(
            ssn,
            nameField.getText().trim(),
            phoneField.getText().trim(),
            emailField.getText().trim()
        );

        long days = ChronoUnit.DAYS.between(checkInDate.getValue(), checkOutDate.getValue());
        if (days <= 0) {
            showInfo("Invalid dates", "Check-out must be after check-in.");
            return;
        }
        double finalTotal = days * room.getPrice();

        Reservation reservation = new Reservation(
            "",
            guest,
            room,
            checkInDate.getValue(),
            checkOutDate.getValue()
        );

        reservationDAO.addReservation(reservation);

    } catch (IllegalArgumentException ex) {
        // Catch any validation coming from Guest / Reservation
        showInfo("Validation error", ex.getMessage());
    }
}

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