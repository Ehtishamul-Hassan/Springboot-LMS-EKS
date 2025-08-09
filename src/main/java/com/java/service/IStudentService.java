package com.java.service;

import com.java.dtos.BookDto;
import com.java.model.Book;
import com.java.model.User;
import com.java.model.Reservation;
import com.java.model.Borrow;
import java.util.List;
import java.util.Optional;

public interface IStudentService {

    // Search books by title or author
    List<Book> searchBooks(String query);

    // Reserve a book
    void reserveBook(User user, Book book);

    // Borrow a book
    void borrowBook(User user, Book book);

    // Get all borrowed books for the student
    List<Borrow> getAllBorrowedBooks(User user);

    // Get all reservations for the student
    List<Reservation> getAllReservations(User user);
}
