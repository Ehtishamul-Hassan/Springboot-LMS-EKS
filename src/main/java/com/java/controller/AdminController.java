package com.java.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.dtos.ApiResponse;
import com.java.dtos.ErrorResponse;
import com.java.dtos.RegisterRequest;
import com.java.dtos.UserDto;
import com.java.exception.UserNotFoundException;
import com.java.service.IAdminService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	
	@Autowired
	 private IAdminService service;
	
// get all the users 
	
	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/users")
	public ResponseEntity<?> viewAllUsers(HttpServletRequest httpRequest) {
	    try {
	        // Call the service to fetch all users
	        List<UserDto> users = service.viewAllUsers();

	        if (users.isEmpty()) {
	            // Return response if no users found
	            return ResponseEntity.ok(new ApiResponse(true, "No users found."));
	        }

	        // Return the list of users
	        return ResponseEntity.ok(users);

	    } catch (Exception ex) {
	        // Handle unexpected errors
	        ErrorResponse errorResponse = new ErrorResponse(
	            LocalDateTime.now(),
	            "An error occurred while fetching users: " + ex.getMessage(),
	            ex.getLocalizedMessage(),
	            HttpStatus.INTERNAL_SERVER_ERROR.value(),
	            httpRequest.getRequestURI()
	        );

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}

	
	
//	register 
    @PreAuthorize("hasAuthority('admin:create')")
	@PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request,
    		HttpServletRequest httpRequest) {
        try {
            // Add user through service
            var response = service.addUser(request);

            // Check if MFA is enabled and return the response accordingly
            if (request.isMfaEnabled()) {
                return ResponseEntity.ok(new ApiResponse(true, "User registered successfully with MFA", response));
            }

            // If no MFA, return accepted status with a success message
            return ResponseEntity.accepted()
            		.body(new ApiResponse(true, "User registered successfully without MFA"));

        } catch (Exception e) {
            // Handle any unexpected errors (like database issues, validation failure, etc.)
            ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(), 
                "An error occurred while registering the user: " + e.getMessage(), 
                e.getLocalizedMessage(), 
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                httpRequest.getRequestURI()
            );

            // Return error response with status 500 (Internal Server Error)
            return ResponseEntity
            		.status(HttpStatus.INTERNAL_SERVER_ERROR)
            		.body(errorResponse);
        }
    }

	 
    
    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> removeUser(@PathVariable Long userId,
    		HttpServletRequest httpRequest) {
        try {
            // Call the service to remove the user
            service.removeUser(userId);

            // Return success response
            return ResponseEntity
            		.ok(new ApiResponse(true,
            				"User with ID " + userId + " has been removed successfully"));

        } catch (UserNotFoundException ex) {
            // Handle specific exception for user not found
            ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                ex.getLocalizedMessage(),
                HttpStatus.NOT_FOUND.value(),
                httpRequest.getRequestURI()
            );

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception ex) {
            // Handle other exceptions
            ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "An error occurred while removing the user: " + ex.getMessage(),
                ex.getLocalizedMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                httpRequest.getRequestURI()
            );

            return ResponseEntity
            		.status(HttpStatus.INTERNAL_SERVER_ERROR)
            		.body(errorResponse);
        }
    }

    
    

}
