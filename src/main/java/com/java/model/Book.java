package com.java.model;

import java.util.Set;


import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	
	private String auther;
	private String isbn;
	private Long totalCopies;
	private Long availableCopies;
	
	@OneToMany(mappedBy = "book")
	@JsonManagedReference
	private Set<Borrow> borrows;
	
	@OneToMany(mappedBy = "book")
	private Set<Reservation> reservations;
	
	
	
	

}
