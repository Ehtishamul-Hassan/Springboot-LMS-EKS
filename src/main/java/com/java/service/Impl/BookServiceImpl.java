package com.java.service.Impl;

import com.java.model.Book;
import com.java.dtos.BookDto;
import com.java.repository.BookRepository;
import com.java.service.IBookService;
import com.java.service.mapper.BookMapper;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements IBookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    @Override
    public void addBook(BookDto bookDto) {
        Book book = bookMapper.toEntity(bookDto);
        bookRepository.save(book);
    }

    @Override
    public void updateBook(Long bookId, BookDto bookDto) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        bookMapper.updateEntityFromDto(bookDto, book);
        bookRepository.save(book);
    }

    @Override
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookDto getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> searchBooks(String query) {
        return bookRepository.findByTitleContainingOrAutherContaining(query, query).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }
}
