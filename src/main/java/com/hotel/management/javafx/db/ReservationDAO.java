package com.hotel.management.javafx.db;

import com.hotel.management.javafx.model.Guest;
import com.hotel.management.javafx.model.Reservation;
import com.hotel.management.javafx.model.Room;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    
    private RoomDAO roomDAO = new RoomDAO();
    
    /**
     * Get all reservations from the database
     */
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Database connection failed");
            return reservations;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Reservation reservation = createReservationFromResultSet(rs);
                if (reservation != null) {
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservations;
    }
    
    /**
     * Get reservations by status
     */
    public List<Reservation> getReservationsByStatus(String status) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE status = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return reservations;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Reservation reservation = createReservationFromResultSet(rs);
                if (reservation != null) {
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservations;
    }
    
    /**
     * Get a specific reservation by ID
     */
    public Reservation getReservationById(String reservationId) {
        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservationId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return createReservationFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Add a new reservation to the database
     */
    public boolean addReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (reservation_id, guest_ssn, guest_name, guest_phone, guest_email, room_id, check_in, check_out, total_price, status, is_paid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            Guest guest = reservation.getGuest();
            
            stmt.setString(1, reservation.getReservationId());
            stmt.setString(2, guest.getSsn());
            stmt.setString(3, guest.getName());
            stmt.setString(4, guest.getPhoneNumber());
            stmt.setString(5, guest.getEmail());
            stmt.setString(6, reservation.getRoom().getRoomNumber());
            stmt.setDate(7, Date.valueOf(reservation.getCheckInDate()));
            stmt.setDate(8, Date.valueOf(reservation.getCheckOutDate()));
            stmt.setDouble(9, reservation.getTotalPrice());
            stmt.setString(10, reservation.getRoom().getStatus());
            stmt.setBoolean(11, reservation.isPaid());
            
            int rowsAffected = stmt.executeUpdate();
            
            // Update room status to Reserved
            if (rowsAffected > 0) {
                roomDAO.updateRoomStatus(reservation.getRoom().getRoomNumber(), "Reserved");
            }
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update reservation status
     */
    public boolean updateReservationStatus(String reservationId, String newStatus) {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, reservationId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cancel a reservation
     */
    public boolean cancelReservation(String reservationId) {
        Reservation reservation = getReservationById(reservationId);
        if (reservation == null) {
            return false;
        }
        
        // Update reservation status to Cancelled
        boolean updated = updateReservationStatus(reservationId, "Cancelled");
        
        // Update room status to Available
        if (updated) {
            roomDAO.updateRoomStatus(reservation.getRoom().getRoomNumber(), "Available");
        }
        
        return updated;
    }
    
    /**
     * Process payment for a reservation
     */
    public boolean processPayment(String reservationId) {
        String sql = "UPDATE reservations SET is_paid = true, payment_date = ? WHERE reservation_id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setString(2, reservationId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check in a guest
     */
    public boolean checkIn(String reservationId) {
        Reservation reservation = getReservationById(reservationId);
        if (reservation == null || !reservation.isPaid()) {
            return false;
        }
        
        // Update room status to Occupied
        return roomDAO.updateRoomStatus(reservation.getRoom().getRoomNumber(), "Occupied");
    }
    
    /**
     * Check out a guest
     */
    public boolean checkOut(String reservationId) {
        Reservation reservation = getReservationById(reservationId);
        if (reservation == null) {
            return false;
        }
        
        // Update reservation status and room status
        updateReservationStatus(reservationId, "Completed");
        return roomDAO.updateRoomStatus(reservation.getRoom().getRoomNumber(), "Cleaning");
    }
    
    /**
     * Helper method to create Reservation object from ResultSet
     */
    private Reservation createReservationFromResultSet(ResultSet rs) throws SQLException {
        String reservationId = rs.getString("reservation_id");
        String guestSsn = rs.getString("guest_ssn");
        String guestName = rs.getString("guest_name");
        String guestPhone = rs.getString("guest_phone");
        String guestEmail = rs.getString("guest_email");
        String roomId = rs.getString("room_id");
        LocalDate checkIn = rs.getDate("check_in").toLocalDate();
        LocalDate checkOut = rs.getDate("check_out").toLocalDate();
        boolean isPaid = rs.getBoolean("is_paid");
        
        // Create Guest object
        Guest guest = new Guest(guestSsn, guestName, guestPhone, guestEmail);
        
        // Get Room from RoomDAO
        Room room = roomDAO.getRoomByNumber(roomId);
        if (room == null) {
            System.err.println("Room not found: " + roomId);
            return null;
        }
        
        // Create Reservation object
        Reservation reservation = new Reservation(reservationId, guest, room, checkIn, checkOut);
        if (isPaid) {
            reservation.processPayment();
        }
        
        return reservation;
    }
}