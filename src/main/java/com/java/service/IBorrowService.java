package com.java.service;

import com.java.model.Borrow;
import com.java.model.Book;
import com.java.model.User;

import java.time.LocalDate;
import java.util.List;

public interface IBorrowService {

    // Borrow a book
    void borrowBook(User user, Book book);

    // Return a borrowed book
    void returnBook(Long borrowId);

    // Get all borrowed books by a user
    List<Borrow> getAllBorrowedBooks(User user);

    // Get overdue books
    List<Borrow> getOverdueBooks();

    // Get the due date for a borrowed book
    LocalDate getDueDate(Long borrowId);
}
