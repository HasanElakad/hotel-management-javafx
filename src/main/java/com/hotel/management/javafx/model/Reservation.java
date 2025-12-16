package com.hotel.management.javafx.model;
import java.time.LocalDate;


public class Reservation {
    
    private String reservationId;
    private Guest guest;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;
    private boolean isPaid;
    private LocalDate paymentDate;
    
    public Reservation(String reservationId, Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate) {

        setReservationId(reservationId);
        setGuest(guest);
        setRoom(room);
        setCheckInDate(checkInDate);
        setCheckOutDate(checkOutDate);
         room.setStatus("Reserved");
         this.isPaid = false;
        calculateTotalPrice();
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        if (guest == null)
            throw new IllegalArgumentException("Guest cannot be null");
        this.guest = guest;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        if (room == null)
            throw new IllegalArgumentException("Room cannot be null");

        if (!room.isAvailable())
            throw new IllegalStateException("Room is not available");
        this.room = room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        if (checkInDate == null || checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        if (checkOutDate == null || checkOutDate.isBefore(checkInDate)) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }
        this.checkOutDate = checkOutDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
    public boolean isPaid() {
        return isPaid;
    }
    
    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    private void calculateTotalPrice() {
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        this.totalPrice = room.calculateTotalPrice(nights);
    }
     public void processPayment() {
        if (isPaid) {
            throw new IllegalStateException("Reservation is already paid");
        }
        this.isPaid = true;
        this.paymentDate = LocalDate.now();
    }
     public void checkIn() {
        if (!isPaid) {
            throw new IllegalStateException("Cannot check in, payment not received");
        }
       
        room.setStatus("Occupied");
    }
      public void checkOut() {
        room.setStatus("Cleaning");
    }
      
    @Override
    public String toString() {
        return "Reservation{" + "reservationId=" + reservationId +", guest=" + guest.getName() +", room=" + room.getRoomNumber() +", checkIn=" + checkInDate +", checkOut=" + checkOutDate +", totalPrice=" + totalPrice +'}';
    }
}
