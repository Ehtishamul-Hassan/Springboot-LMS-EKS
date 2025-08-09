package com.java.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.dtos.AuthRequest;
import com.java.dtos.AuthResponse;
import com.java.dtos.RegisterRequest;
import com.java.dtos.VerificationRequest;
import com.java.service.IAuthService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/auth")

public class AuthController {
	
	//for login and register
	
	private IAuthService service;
	
	public AuthController(IAuthService service) {
		
		this.service = service;
	}
	
//	register 
	@PostMapping("/register")
	  public ResponseEntity<?> register(
	      @RequestBody RegisterRequest request
	  ) {
		var response = service.register(request);
		
		if(request.isMfaEnabled()) {
			return ResponseEntity.ok(response);
		}
		
//	    return ResponseEntity.accepted().build();
		return ResponseEntity.status(HttpStatus.CREATED).body(
		        AuthResponse.builder()
		            .accessToken(response.getAccessToken())
		            .refreshToken(response.getRefreshToken())
		      
		            .message("Registration successful")
		            .build()
		    );
	  }
	
//	login
	  @PostMapping("/authenticate")
	  public ResponseEntity<AuthResponse> authenticate(
	      @RequestBody AuthRequest request
	  ) {
	    return ResponseEntity.ok(service.authenticate(request));
	  }
	  
//	  logout
	  @PostMapping("/logout")
	    public ResponseEntity<?> logout(@RequestHeader("Authorization")
	    String authHeader) {
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            return ResponseEntity.badRequest().body("Invalid token.");
	        }

	        String token = authHeader.substring(7); // Remove "Bearer " prefix
	        service.revokeToken(token); // Call service method to revoke the token

	        return ResponseEntity.ok("Successfully logged out.");
	    }
	
	  

//	  refresh token 
	  @PostMapping("/refresh-token")
	  public void refreshToken(
	      HttpServletRequest request,
	      HttpServletResponse response
	  ) throws IOException {
	    service.refreshToken(request, response);
	  }
	  
	  
	  @PostMapping("/verify")
	  public ResponseEntity<?> verifyCode
	  (@RequestBody VerificationRequest verificationRequest ) {
		  
		  
		  return ResponseEntity.ok(service.verifyCode(verificationRequest));
		  
	  }
	  
	  
	  @GetMapping("/activate-account")
	    public void confirm(
	            @RequestParam String token
	    ) throws MessagingException {
	        service.activateAccount(token);
	    }
	  
	   
	  
	   
	
	

}
