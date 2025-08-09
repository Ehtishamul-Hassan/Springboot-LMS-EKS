package com.java.service;

import com.java.dtos.AuthRequest;
import com.java.dtos.AuthResponse;
import com.java.dtos.RegisterRequest;
import com.java.dtos.VerificationRequest;
import com.java.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
	
	AuthResponse register(RegisterRequest request);
	
	AuthResponse authenticate(AuthRequest request);
	
	void saveUserToken(User user, String jwtToken);
	
	void revokeAllUserTokens(User user);
	
	void refreshToken(HttpServletRequest request , HttpServletResponse response);

	AuthResponse verifyCode(VerificationRequest verificationRequest);
	
	void revokeToken(String token);
	
	void activateAccount(String token);
	
	String generateAndSaveActivationToken(User user);
	
	void sendValidationEmail(User user);
	
	String generateActivationCode(Integer length);
	
	
	
	
	

}
