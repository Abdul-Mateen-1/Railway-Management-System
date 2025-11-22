package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingService {
    private final BackendRepository repo = BackendRepository.getInstance();

    public Booking bookTicket(User user, Train train, String from, String to,
                              LocalDate date, int seats, String seatClass, double totalAmount) {
        Booking booking = new Booking(
                generateBookingId(),
                user.getId(),
                train.getId(),
                train.getTrainNumber(),
                train.getTrainName(),
                from,
                to,
                date,
                seats,
                seatClass,
                totalAmount,
                "Pending",
                LocalDateTime.now(),
                "",
                "Pending"
        );
        return repo.addBooking(booking);
    }

    public boolean processPayment(String bookingId, String paymentMethod) {
        Optional<Booking> bookingOpt = repo.findBookingById(bookingId);
        if (bookingOpt.isEmpty()) {
            return false;
        }
        Booking booking = bookingOpt.get();
        booking.setPaymentMethod(paymentMethod);
        booking.setPaymentStatus("Paid");
        booking.setStatus("Confirmed");
        return repo.updateBooking(booking);
    }

    public boolean cancelBooking(String bookingId) {
        Optional<Booking> bookingOpt = repo.findBookingById(bookingId);
        if (bookingOpt.isEmpty()) {
            return false;
        }
        Booking booking = bookingOpt.get();
        booking.setStatus("Cancelled");
        return repo.updateBooking(booking);
    }

    public List<Booking> getPendingPaymentsForUser(String userId) {
        return repo.getBookings().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .filter(booking -> "Pending".equals(booking.getPaymentStatus()))
                .collect(Collectors.toList());
    }

    private String generateBookingId() {
        return repo.nextBookingId();
    }

    public List<Booking> getBookingsForUser(String userId) {
        return repo.getBookings().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Booking> getAllBookings() {
        return repo.getBookings();
    }

    public Optional<Booking> getBookingById(String bookingId) {
        return repo.findBookingById(bookingId);
    }
}
