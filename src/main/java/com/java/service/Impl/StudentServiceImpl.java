package com.java.service.Impl;

import com.java.model.Book;
import com.java.model.User;
import com.java.model.Reservation;
import com.java.model.Borrow;
import com.java.repository.BookRepository;
import com.java.repository.BorrowRepository;
import com.java.repository.ReservationRepository;
import com.java.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentServiceImpl implements IStudentService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public List<Book> searchBooks(String query) {
        return bookRepository.findByTitleContainingOrAutherContaining(query, query);
    }

    @Override
    public void reserveBook(User user, Book book) {
        if (book.getAvailableCopies() > 0) {
            throw new RuntimeException("Book is available for direct borrowing, reserve it only if unavailable.");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDate.now());
        reservationRepository.save(reservation);
    }

    @Override
    public void borrowBook(User user, Book book) {
    	
        if (book.getAvailableCopies() > 0) {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);

            Borrow borrow = new Borrow();
            borrow.setUser(user);
            borrow.setBook(book);
            borrow.setBorrowDate(LocalDate.now());
            borrow.setDueDate(LocalDate.now().plusWeeks(2));  // Borrow for 2 weeks
            borrowRepository.save(borrow);
        } else {
            throw new RuntimeException("Book is not available for borrowing.");
        }
    }

    @Override
    public List<Borrow> getAllBorrowedBooks(User user) {
        return borrowRepository.findByUser(user);
    }

    @Override
    public List<Reservation> getAllReservations(User user) {
        return reservationRepository.findByUser(user);
    }
}
