package com.hotel.management.javafx.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Room {
    
    public static final List<String> VALID_STATUSES = Arrays.asList("Available", "Occupied", "Cleaning", "Maintenance", "Reserved");
    public static final List<String> VALID_ROOM_TYPES = Arrays.asList("Single", "Double", "Triple", "Suite");
    
    private static final double EXTRA_BED_MULTIPLIER = 1.1;
    
    private String roomNumber;
    private int capacity;
    private String roomType;
    private List<String> features;
    private String status;
    private double price;
    private int floor;
    private boolean extraBed;
  
    public Room(String roomNumber, int capacity, String roomType, double price, int floor, boolean extraBed) {
        // Initialize features list FIRST before anything else
        this.features = new ArrayList<>();
        
        validateRoomNumber(roomNumber);
        this.roomNumber = roomNumber;
        setCapacity(capacity);
        setRoomType(roomType);  // This will call assignFeatures()
        this.status = VALID_STATUSES.get(0); 
        setPrice(price);
        setFloor(floor);
        this.extraBed = extraBed;
    }
    
    private void validateRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be null or empty");
        }
    }
    
    private void validateRoomType(String roomType) {
        if (roomType == null || roomType.trim().isEmpty()) {
            throw new IllegalArgumentException("Room type cannot be null or empty");
        }
        
        boolean isValid = VALID_ROOM_TYPES.stream().anyMatch(type -> type.equalsIgnoreCase(roomType));
        
        if (!isValid) {
            throw new IllegalArgumentException(
                "Invalid room type: '" + roomType + "'. Must be one of: " + String.join(", ", VALID_ROOM_TYPES)
            );
        }
    }
    
    private void validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        
        boolean isValid = VALID_STATUSES.stream().anyMatch(s -> s.equalsIgnoreCase(status));
        
        if (!isValid) {
            throw new IllegalArgumentException(
                "Invalid status: '" + status + "'. Must be one of: " + String.join(", ", VALID_STATUSES)
            );
        }
    }
    
    public boolean isExtraBed() {
        return extraBed;
    }
    
    public void setExtraBed(boolean extraBed) {
        this.extraBed = extraBed;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }
    
    public void setRoomNumber(String roomNumber) {
        validateRoomNumber(roomNumber);
        this.roomNumber = roomNumber;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        if (capacity < 1 || capacity > 10) {
            throw new IllegalArgumentException("Capacity must be between 1 and 10");
        }
        this.capacity = capacity;
    }
    
    public String getRoomType() {
        return roomType;
    }
    
    public void setRoomType(String roomType) {
        validateRoomType(roomType);
        
        this.roomType = roomType.substring(0, 1).toUpperCase() + roomType.substring(1).toLowerCase();
        assignFeatures();
    }
    
    public List<String> getFeatures() {
        if (features == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(features);
    }
    
    public void setFeatures(List<String> features) {
        if (features == null) {
            this.features = new ArrayList<>();
        } else {
            this.features = new ArrayList<>(features);
        }
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        validateStatus(status);
        this.status = status;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }
    
    public int getFloor() {
        return floor;
    }
    
    public void setFloor(int floor) {
        if (floor < 0) {
            throw new IllegalArgumentException("Floor cannot be negative");
        }
        this.floor = floor;
    }
    
    public double calculateTotalPrice(int nights) {
        if (nights < 0) {
            throw new IllegalArgumentException("Number of nights cannot be negative");
        }
        
        if (extraBed) {
            return this.price * nights * EXTRA_BED_MULTIPLIER;
        }
        return this.price * nights;
    }
    
    public boolean isAvailable() {
        return VALID_STATUSES.get(0).equalsIgnoreCase(status);
    }
    
    public void makeAvailable() {
        this.status = "Available";
    }
    
    private void assignFeatures() {
        // Make absolutely sure features list exists
        if (this.features == null) {
            this.features = new ArrayList<>();
        }
        
        features.clear();
        features.add("TV");
        features.add("Kettle");
        
        if (VALID_ROOM_TYPES.get(1).equalsIgnoreCase(roomType)) {  // Double
            features.add("Mini Fridge");
            features.add("Balcony View");
        }
        
        if (VALID_ROOM_TYPES.get(2).equalsIgnoreCase(roomType)) {  // Triple
            features.add("Fridge");
            features.add("Balcony View");
            features.add("Two Bathrooms");
        }
        
        if (VALID_ROOM_TYPES.get(3).equalsIgnoreCase(roomType)) {  // Suite
            features.add("Smart TV");
            features.add("Mini Fridge");
            features.add("Coffee Machine");
            features.add("Sea View");
            features.add("VIP Bathroom");
        }
    }
    
    @Override
    public String toString() {
        return "Room{" +
               "roomNumber='" + roomNumber + '\'' +
               ", capacity=" + capacity +
               ", roomType='" + roomType + '\'' +
               ", status='" + status + '\'' +
               ", price=" + price +
               ", floor=" + floor +
               ", extraBed=" + extraBed +
               '}';
    }
}