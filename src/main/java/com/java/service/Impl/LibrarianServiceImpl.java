package com.java.service.Impl;

import com.java.model.Reservation;
import com.java.model.Borrow;
import com.java.model.User;
import com.java.model.Book;
import com.java.repository.ReservationRepository;
import com.java.repository.BorrowRepository;
import com.java.repository.BookRepository;
import com.java.service.ILibrarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibrarianServiceImpl implements ILibrarianService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void approveReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new RuntimeException("Reservation not found"));
        Book book = reservation.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        reservationRepository.delete(reservation);
    }

    @Override
    public void manageOverdueFines(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId).orElseThrow(() -> new RuntimeException("Borrow record not found"));
        if (borrow.getDueDate().isBefore(LocalDate.now())) {
            // Apply overdue fine logic
            // You can add fine amount to user account or simply log it
            System.out.println("Overdue fine applied to borrow record " + borrowId);
        }
    }

    @Override
    public List<Reservation> getAllReservedBooks() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Borrow> getAllBorrowedBooks() {
        return borrowRepository.findAll();
    }

    @Override
    public List<Borrow> getOverdueBooks() {
        return borrowRepository.findAll().stream()
                .filter(borrow -> borrow.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }
}
