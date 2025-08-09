package com.java.service.Impl;

import com.java.model.Reservation;
import com.java.model.Book;
import com.java.model.User;
import com.java.repository.BookRepository;
import com.java.repository.ReservationRepository;
import com.java.service.IReservationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class ReservationServiceImpl implements IReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void reserveBook(User user, Book book) {
        if (book.getAvailableCopies() > 0) {
            throw new RuntimeException("Book is already available, borrow it directly.");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDate.now());
        reservationRepository.save(reservation);
    }

    @Override
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservationRepository.delete(reservation);
    }

    @Override
    public List<Reservation> getAllReservations(User user) {
        return reservationRepository.findByUser(user);
    }

    @Override
    public List<Reservation> getAllReservedBooks() {
        return reservationRepository.findAll();
    }

    @Override
    public void approveReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new RuntimeException("Reservation not found"));
        Book book = reservation.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        reservationRepository.delete(reservation);  // Mark reservation as completed
    }
}
