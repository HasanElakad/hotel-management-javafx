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
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Database connection failed");
                return rooms;
            }
            
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Room room = createRoomFromResultSet(rs);
                rooms.add(room);
            }
            
            System.out.println("✓ Loaded " + rooms.size() + " rooms");
            
        } catch (SQLException e) {
            System.err.println("Error loading all rooms: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt);
        }
        
        return rooms;
    }
    
    /**
     * Get rooms by type (Single, Double, Triple, Suite)
     */
    public List<Room> getRoomsByType(String roomType) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE room_type = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Database connection failed");
                return rooms;
            }
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, roomType);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Room room = createRoomFromResultSet(rs);
                rooms.add(room);
            }
            
            System.out.println("✓ Loaded " + rooms.size() + " rooms of type: " + roomType);
            
        } catch (SQLException e) {
            System.err.println("Error loading rooms by type '" + roomType + "': " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt);
        }
        
        return rooms;
    }
    
    /**
     * Get rooms by status (Available, Occupied, Cleaning, Maintenance, Reserved)
     */
    public List<Room> getRoomsByStatus(String status) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE status = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Database connection failed");
                return rooms;
            }
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Room room = createRoomFromResultSet(rs);
                rooms.add(room);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading rooms by status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt);
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
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Database connection failed");
                return null;
            }
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, roomNumber);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Room room = createRoomFromResultSet(rs);
                System.out.println("✓ Found room: " + roomNumber);
                return room;
            } else {
                System.err.println("⚠️ Room not found: " + roomNumber);
            }
        } catch (SQLException e) {
            System.err.println("Error getting room by number: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt);
        }
        
        return null;
    }
    
    /**
     * Update room status
     */
    public boolean updateRoomStatus(String roomNumber, String newStatus) {
        String sql = "UPDATE rooms SET status = ? WHERE room_number = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Database connection failed");
                return false;
            }
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setString(2, roomNumber);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Room " + roomNumber + " status updated to: " + newStatus);
                return true;
            } else {
                System.err.println("⚠️ Failed to update room " + roomNumber + " - room not found");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating room status: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, stmt);
        }
    }
    
    /**
     * Add a new room to the database
     */
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, capacity, room_type, price, floor, extra_bed, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Database connection failed");
                return false;
            }
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getCapacity());
            stmt.setString(3, room.getRoomType());
            stmt.setDouble(4, room.getPrice());
            stmt.setInt(5, room.getFloor());
            stmt.setBoolean(6, room.isExtraBed());
            stmt.setString(7, room.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Room " + room.getRoomNumber() + " added successfully");
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, stmt);
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
    
    /**
     * Helper method to close database resources
     */
    private void closeResources(ResultSet rs, PreparedStatement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}