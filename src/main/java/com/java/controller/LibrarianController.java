package com.java.controller;

import com.java.service.ILibrarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/librarian")
public class LibrarianController {

    @Autowired
    private ILibrarianService librarianService;

    // Approve a reservation
    @PostMapping("/approve-reservation/{reservationId}")
    public ResponseEntity<?> approveReservation(@PathVariable Long reservationId) {
        librarianService.approveReservation(reservationId);
        return ResponseEntity.ok("Reservation approved successfully");
    }

    // Manage overdue fines
    @PostMapping("/manage-overdue-fines/{borrowId}")
    public ResponseEntity<?> manageOverdueFines(@PathVariable Long borrowId) {
        librarianService.manageOverdueFines(borrowId);
        return ResponseEntity.ok("Overdue fines managed successfully");
    }

    // Get all reserved books
    @GetMapping("/reserved-books")
    public ResponseEntity<?> getAllReservedBooks() {
        return ResponseEntity.ok(librarianService.getAllReservedBooks());
    }

    // Get all borrowed books
    @GetMapping("/borrowed-books")
    public ResponseEntity<?> getAllBorrowedBooks() {
        return ResponseEntity.ok(librarianService.getAllBorrowedBooks());
    }
}
