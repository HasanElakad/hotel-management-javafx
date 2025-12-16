package com.hotel.management.javafx.db;

import com.hotel.management.javafx.model.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    
    /**
     * Get all rooms from the database
     */
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Database connection failed");
            return rooms;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Room room = createRoomFromResultSet(rs);
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rooms;
    }
    
    /**
     * Get rooms by type (Single, Double, Triple, Suite)
     */
    public List<Room> getRoomsByType(String roomType) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE room_type = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Database connection failed");
            return rooms;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomType);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Room room = createRoomFromResultSet(rs);
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rooms;
    }
    
    /**
     * Get rooms by status (Available, Occupied, Cleaning, Maintenance, Reserved)
     */
    public List<Room> getRoomsByStatus(String status) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE status = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Database connection failed");
            return rooms;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Room room = createRoomFromResultSet(rs);
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rooms;
    }
    
    /**
     * Get available rooms only
     */
    public List<Room> getAvailableRooms() {
        return getRoomsByStatus("Available");
    }
    
    /**
     * Get a specific room by room number
     */
    public Room getRoomByNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Database connection failed");
            return null;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return createRoomFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Update room status
     */
    public boolean updateRoomStatus(String roomNumber, String newStatus) {
        String sql = "UPDATE rooms SET status = ? WHERE room_number = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Database connection failed");
            return false;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, roomNumber);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Add a new room to the database
     */
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, capacity, room_type, price, floor, extra_bed, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Database connection failed");
            return false;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getCapacity());
            stmt.setString(3, room.getRoomType());
            stmt.setDouble(4, room.getPrice());
            stmt.setInt(5, room.getFloor());
            stmt.setBoolean(6, room.isExtraBed());
            stmt.setString(7, room.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Helper method to create Room object from ResultSet
     */
    private Room createRoomFromResultSet(ResultSet rs) throws SQLException {
        String roomNumber = rs.getString("room_number");
        int capacity = rs.getInt("capacity");
        String roomType = rs.getString("room_type");
        double price = rs.getDouble("price");
        int floor = rs.getInt("floor");
        boolean extraBed = rs.getBoolean("extra_bed");
        String status = rs.getString("status");
        
        Room room = new Room(roomNumber, capacity, roomType, price, floor, extraBed);
        room.setStatus(status);
        
        return room;
    }
}