package com.java.service;


import com.java.dtos.BookDto;

import java.util.List;

public interface IBookService {

    // Add a new book
    void addBook(BookDto bookDto);

    // Update book details
    void updateBook(Long bookId, BookDto bookDto);

    // Get a list of all books
    List<BookDto> getAllBooks();

    // Get details of a specific book
    BookDto getBookById(Long bookId);

    // Search books by title or author
    List<BookDto> searchBooks(String query);
}
