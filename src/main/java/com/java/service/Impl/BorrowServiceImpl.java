package com.java.service.Impl;

import com.java.model.Borrow;
import com.java.model.Book;
import com.java.model.User;
import com.java.repository.BookRepository;
import com.java.repository.BorrowRepository;
import com.java.service.IBorrowService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowServiceImpl implements IBorrowService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

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
    public void returnBook(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId).orElseThrow(() -> new RuntimeException("Borrow record not found"));
        Book book = borrow.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        borrowRepository.delete(borrow);
    }

    @Override
    public List<Borrow> getAllBorrowedBooks(User user) {
        return borrowRepository.findByUser(user);
    }

    @Override
    public List<Borrow> getOverdueBooks() {
        return borrowRepository.findAll().stream()
                .filter(borrow -> borrow.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    @Override
    public LocalDate getDueDate(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId).orElseThrow(() -> new RuntimeException("Borrow record not found"));
        return borrow.getDueDate();
    }
}
