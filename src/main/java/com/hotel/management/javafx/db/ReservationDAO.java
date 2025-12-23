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
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            Guest guest = reservation.getGuest();
            stmt.setString(1, guest.getSsn());
            stmt.setString(2, guest.getName());
            stmt.setString(3, guest.getPhoneNumber());
            stmt.setString(4, guest.getEmail());
            stmt.setString(5, reservation.getRoom().getRoomNumber());
            stmt.setDate(6, Date.valueOf(reservation.getCheckInDate()));
            stmt.setDate(7, Date.valueOf(reservation.getCheckOutDate()));
            stmt.setDouble(8, reservation.getTotalPrice());
            stmt.setString(9, reservation.getRoom().getStatus());  // Fixed: use room status
            stmt.setBoolean(10, reservation.isPaid());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                roomDAO.updateRoomStatus(reservation.getRoom().getRoomNumber(), "Reserved");
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reservation.setReservationId(rs.getString(1));
                    }
                }
            }
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY check_in DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Reservation res = createReservationFromResultSet(rs);
                if (res != null) {
                    reservations.add(res);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }
    
    public boolean deleteReservation(String reservationId) {
        String sql = "DELETE FROM reservations WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservationId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                String roomNumber = getRoomNumberByReservationId(reservationId);
                if (roomNumber != null) {
                    roomDAO.updateRoomStatus(roomNumber, "Available");
                }
            }
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean cancelReservation(String reservationId) {  // ADDED
        return deleteReservation(reservationId);
    }
    
    private String getRoomNumberByReservationId(String reservationId) {
        String sql = "SELECT room_id FROM reservations WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("room_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
    
    Room room = roomDAO.getRoomByNumber(roomId);
    if (room == null) {
        System.err.println("⚠️ Skipping reservation " + reservationId + " - Room not found: " + roomId);
        return null;
    }
    
    LocalDate checkIn = rs.getDate("check_in").toLocalDate();
    LocalDate checkOut = rs.getDate("check_out").toLocalDate();
    
    // TEMPORARILY make room available for constructor
    String originalStatus = room.getStatus();
    room.setStatus("Available");
    
    Guest guest = new Guest(guestSsn, guestName, guestPhone, guestEmail);
    Reservation reservation = new Reservation(reservationId, guest, room, checkIn, checkOut, true);

    
    // Restore original room status
    room.setStatus(originalStatus);
    
    return reservation;
}

}
