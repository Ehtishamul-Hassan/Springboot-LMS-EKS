package com.java.service;

import com.java.model.Reservation;
import com.java.model.Borrow;

import java.util.List;

public interface ILibrarianService {

    // Approve a reservation (when a book becomes available)
    void approveReservation(Long reservationId);

    // Manage overdue fines (apply fines to overdue books)
    void manageOverdueFines(Long borrowId);

    // Get all reserved books
    List<Reservation> getAllReservedBooks();

    // Get all borrowed books
    List<Borrow> getAllBorrowedBooks();

    // Get overdue books
    List<Borrow> getOverdueBooks();
}
