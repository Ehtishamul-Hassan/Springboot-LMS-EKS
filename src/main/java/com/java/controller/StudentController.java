package com.java.controller;

import com.java.dtos.BookDto;
import com.java.model.Book;
import com.java.model.User;
import com.java.repository.UserRepository;
import com.java.model.Reservation;
import com.java.model.Borrow;
import com.java.service.IBookService;
import com.java.service.IStudentService;
import com.java.service.mapper.BookMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private IStudentService studentService;
    
    @Autowired
    private IBookService bookService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookMapper bookMapper;

    
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String query) {
        List<Book> books = studentService.searchBooks(query);
        return ResponseEntity.ok(books);
    }

    
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveBook(@RequestParam Long bookId) {
        
String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	
    	Optional<User> userOptional = userRepository.findByEmail(username);
    	
    	User user = userOptional.get();
         
    	BookDto book = bookService.getBookById(bookId);
    	
    	Book entity = bookMapper.toEntity(book);

        studentService.reserveBook(user, entity);
        return ResponseEntity.ok("Book reserved successfully.");
    }

   
    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(
    		@RequestParam Long bookId) {
    	
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	
    	Optional<User> userOptional = userRepository.findByEmail(username);
    	
    	User user = userOptional.get();
         
    	BookDto book = bookService.getBookById(bookId);
    	
    	Book entity = bookMapper.toEntity(book);
    	
        studentService.borrowBook(user, entity);
        return ResponseEntity.ok("Book borrowed successfully.");
    }

    
    @GetMapping("/borrowed")
    public ResponseEntity<List<Borrow>> getAllBorrowedBooks() {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	
    	Optional<User> userOptional = userRepository.findByEmail(username);
    	
    	User user = userOptional.get();
    	

        List<Borrow> borrowedBooks = studentService.getAllBorrowedBooks(user);
        return ResponseEntity.ok(borrowedBooks);
    }

    
    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getAllReservations(@RequestParam Long userId) {
        User user = new User();
        user.setId(userId); // Retrieve User based on ID (you might want to fetch user from DB)

        List<Reservation> reservations = studentService.getAllReservations(user);
        return ResponseEntity.ok(reservations);
    }
}
