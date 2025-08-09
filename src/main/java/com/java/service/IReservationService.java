package com.java.service;

import com.java.model.Reservation;
import com.java.model.Book;
import com.java.model.User;

import java.util.List;

public interface IReservationService {

    // Create a reservation for a book
    void reserveBook(User user, Book book);

    // Cancel a reservation
    void cancelReservation(Long reservationId);

    // Get all reservations for a user
    List<Reservation> getAllReservations(User user);

    // Get all reserved books (for librarian/admin)
    List<Reservation> getAllReservedBooks();

    // Approve reservation (when book becomes available)
    void approveReservation(Long reservationId);
}
