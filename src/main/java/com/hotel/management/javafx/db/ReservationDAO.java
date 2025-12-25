package com.hotel.management.javafx.db;

import com.hotel.management.javafx.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private static RoomDAO roomDAO = new RoomDAO();
    
    public boolean addReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations " +
                     "(guest_ssn, guest_name, guest_phone, guest_email, " +
                     " room_id, check_in, check_out, total_price, status, is_paid) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            Guest guest = reservation.getGuest();
            Room room = reservation.getRoom();
            
            stmt.setString(1, guest.getSsn());
            stmt.setString(2, guest.getName());
            stmt.setString(3, guest.getPhoneNumber());
            stmt.setString(4, guest.getEmail());
            stmt.setString(5, room.getRoomNumber());
            stmt.setDate(6, Date.valueOf(reservation.getCheckInDate()));
            stmt.setDate(7, Date.valueOf(reservation.getCheckOutDate()));
            stmt.setDouble(8, reservation.getTotalPrice());
            stmt.setString(9, "Reserved");  // Status should be "Reserved" for new reservations
            stmt.setBoolean(10, reservation.isPaid());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update room status to Reserved
                boolean statusUpdated = roomDAO.updateRoomStatus(room.getRoomNumber(), "Reserved");
                
                if (statusUpdated) {
                    System.out.println("✓ Reservation saved and room status updated to Reserved");
                } else {
                    System.err.println("⚠ Reservation saved but failed to update room status");
                }
                
                // Get the generated reservation ID
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reservation.setReservationId(rs.getString(1));
                        System.out.println("✓ Reservation ID: " + reservation.getReservationId());
                    }
                }
                
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error adding reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY check_in DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return reservations;
            }
            
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Reservation res = createReservationFromResultSet(rs);
                if (res != null) {
                    reservations.add(res);
                }
            }
            
            System.out.println("✓ Loaded " + reservations.size() + " reservations");
            
        } catch (SQLException e) {
            System.err.println("Error loading reservations: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return reservations;
    }
    
    public boolean deleteReservation(String reservationId) {
        // First get the room number
        String roomNumber = getRoomNumberByReservationId(reservationId);
        
        String sql = "DELETE FROM reservations WHERE reservation_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, reservationId);
            int rows = stmt.executeUpdate();
            
            if (rows > 0 && roomNumber != null) {
                // Update room status back to Available
                roomDAO.updateRoomStatus(roomNumber, "Available");
                System.out.println("✓ Reservation deleted and room " + roomNumber + " set to Available");
                return true;
            }
            
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean cancelReservation(String reservationId) {
        return deleteReservation(reservationId);
    }
    
    /**
     * Extend a reservation by updating the check-out date and total price
     */
    public boolean extendReservation(String reservationId, LocalDate newCheckOutDate, double newTotalPrice) {
        String sql = "UPDATE reservations SET check_out = ?, total_price = ? WHERE reservation_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(newCheckOutDate));
            stmt.setDouble(2, newTotalPrice);
            stmt.setString(3, reservationId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Reservation " + reservationId + " extended to " + newCheckOutDate);
                System.out.println("✓ New total price: $" + String.format("%.2f", newTotalPrice));
                return true;
            } else {
                System.err.println("⚠️ Failed to extend reservation - reservation not found");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error extending reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private String getRoomNumberByReservationId(String reservationId) {
        String sql = "SELECT room_id FROM reservations WHERE reservation_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return null;
            }
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, reservationId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("room_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private Reservation createReservationFromResultSet(ResultSet rs) throws SQLException {
        String reservationId = rs.getString("reservation_id");
        String guestSsn = rs.getString("guest_ssn");
        String guestName = rs.getString("guest_name");
        String guestPhone = rs.getString("guest_phone");
        String guestEmail = rs.getString("guest_email");
        String roomId = rs.getString("room_id");
        
        // Get the actual room from database
        Room room = roomDAO.getRoomByNumber(roomId);
        if (room == null) {
            System.err.println("⚠️ Skipping reservation " + reservationId + " - Room not found: " + roomId);
            return null;
        }
        
        LocalDate checkIn = rs.getDate("check_in").toLocalDate();
        LocalDate checkOut = rs.getDate("check_out").toLocalDate();
        
        // Create guest
        Guest guest = new Guest(guestSsn, guestName, guestPhone, guestEmail);
        
        // Use the bypass constructor to avoid validation issues with past dates
        Reservation reservation = new Reservation(
            reservationId, 
            guest, 
            room, 
            checkIn, 
            checkOut, 
            true  // bypass validation
        );
        
        return reservation;
    }
}