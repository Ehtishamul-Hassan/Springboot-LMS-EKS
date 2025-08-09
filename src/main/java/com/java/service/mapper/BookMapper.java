package com.java.service.mapper;

import com.java.dtos.BookDto;
import com.java.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    // Convert BookDto to Book entity
    public Book toEntity(BookDto bookDto) {
        return new Book(
                bookDto.getId(),
                bookDto.getTitle(),
                bookDto.getAuther(),  // Fix typo here
                bookDto.getIsbn(),
                bookDto.getTotalCopies(),
                bookDto.getAvailableCopies(),
                null,
                null
        );
    }

    // Convert Book entity to BookDto
    public BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuther(),  
                book.getIsbn(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }

    // Update Book entity with values from BookDto
    public void updateEntityFromDto(BookDto bookDto, Book book) {
        book.setTitle(bookDto.getTitle());
        book.setAuther(bookDto.getAuther());  
        book.setIsbn(bookDto.getIsbn());
        book.setTotalCopies(bookDto.getTotalCopies());
        book.setAvailableCopies(bookDto.getAvailableCopies());
    }
}
