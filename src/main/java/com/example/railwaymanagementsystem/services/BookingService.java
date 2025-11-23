package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.Booking;
import com.example.railwaymanagementsystem.models.Train;
import com.example.railwaymanagementsystem.models.User;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingService {
    private final BackendRepository repo = BackendRepository.getInstance();
    private final NotificationService notificationService = new NotificationService();

    public Booking bookTicket(User user, Train train, String from, String to,
                              LocalDate date, int seats, String seatClass, double totalAmount) {
        Booking booking = new Booking(
                repo.nextBookingId(),
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
        Booking newBooking = repo.addBooking(booking);
        if (newBooking != null) {
            String message = String.format("Your ticket for %s from %s to %s has been booked with PNR %s. Please complete the payment.",
                    train.getTrainName(), from, to, newBooking.getId());
            notificationService.createNotification(user.getId(), message);
        }
        return newBooking;
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
        boolean success = repo.updateBooking(booking);
        if (success) {
            String message = String.format("Payment of PKR %.2f for PNR %s was successful. Your ticket is confirmed.",
                    booking.getTotalAmount(), booking.getId());
            notificationService.createNotification(booking.getUserId(), message);
        }
        return success;
    }

    public boolean updateBooking(Booking booking) {
        boolean success = repo.updateBooking(booking);
        if (success && "Cancelled".equalsIgnoreCase(booking.getStatus())) {
            String message = String.format("Your booking with PNR %s for train %s has been cancelled.",
                    booking.getId(), booking.getTrainName());
            notificationService.createNotification(booking.getUserId(), message);
        }
        return success;
    }

    public List<Booking> getPendingPaymentsForUser(String userId) {
        return repo.getBookings().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .filter(booking -> "Pending".equals(booking.getPaymentStatus()))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsForUser(String userId) {
        return repo.getBookings().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public ObservableList<Booking> getAllBookings() {
        return repo.getBookings();
    }

    public Optional<Booking> getBookingById(String bookingId) {
        return repo.findBookingById(bookingId);
    }
}
