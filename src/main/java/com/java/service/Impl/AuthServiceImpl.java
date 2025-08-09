package com.java.service.Impl;



import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.config.JwtConfig;
import com.java.dtos.AuthRequest;
import com.java.dtos.AuthResponse;
import com.java.dtos.RegisterRequest;
import com.java.dtos.VerificationRequest;
import com.java.email.EmailService;
import com.java.email.EmailTemplateName;
import com.java.enums.Role;
import com.java.model.User;
import com.java.repository.TokenRepository;
import com.java.repository.UserRepository;
import com.java.service.IAuthService;
import com.java.tfa.TwoFactorAuthenticationService;
import com.java.token.Token;
import com.java.token.TokenType;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
	
	private final UserRepository repository;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtConfig config;
	private final AuthenticationManager authenticationManager;
	private final TwoFactorAuthenticationService tfa;
	private final EmailService emailService;
	
	@Value("${mailing.activation-url}")
    private String activationUrl;
	
	

	@Override
	public AuthResponse register(RegisterRequest request) {
		
		var user = User.builder()
		.firstname(request.getFirstname())
		.lastname(request.getLastname())
		.email(request.getEmail())
		.password(passwordEncoder.encode(request.getPassword()))
		.role(request.getRole())
		.enabled(false)
		.mfaEnabled(request.isMfaEnabled())
		.build();
		
		// if mfaEnabled ->> Generate secret
		
		if(request.isMfaEnabled()) {
			user.setSecret(tfa.generateNewSecret());
		}
		
		var savedUser = repository.save(user);
		sendValidationEmail(savedUser);
		var jwtToken = config.generateToken(user);
		var refreshToken = config.generateRefreshToken(user);
		
		revokeAllUserTokens(user);
		saveUserToken(savedUser, jwtToken);
		
		return AuthResponse.builder()
				.secretImageUri(tfa.generateQrCodeImageUri(user.getSecret()))
				.accessToken(jwtToken)
				.refreshToken(refreshToken)
				.mfaEnabled(user.isMfaEnabled())
				.build();
	
	}

	@Override
	public AuthResponse authenticate(AuthRequest request) {
		
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken
				(request.getEmail(), request.getPassword())
				);
		
		var user = repository.findByEmail(request.getEmail())
				.orElseThrow();
		
		if(user.isMfaEnabled()) {
			return AuthResponse.builder()
			        .accessToken("")
		            .refreshToken("")
		            .mfaEnabled(true)
		        .build();
		}
		
		var jwtToken = config.generateToken(user);
		
		var refreshToken = config.generateRefreshToken(user);
	    revokeAllUserTokens(user);
	    saveUserToken(user, jwtToken);
	    return AuthResponse.builder()
	        .accessToken(jwtToken)
	            .refreshToken(refreshToken)
	            .mfaEnabled(false)
	        .build();
	}
	
	

	@Override
	public void saveUserToken(User user, String jwtToken) {
		
		var token = Token.builder()
		        .user(user)
		        .token(jwtToken)
		        .tokenType(TokenType.BEARER)
		        .expired(false)
		        .revoked(false)
		        .build();
		    tokenRepository.save(token);
		
	}
 
	@Override
	public void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
	    if (validUserTokens.isEmpty())
	      return;
	    validUserTokens.forEach(token -> { 
	      token.setExpired(true);
	      token.setRevoked(true);
	    });
	    tokenRepository.saveAll(validUserTokens);
		
	}

	@Override
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
	
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
	    final String refreshToken;
	    final String userEmail;
	    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
	      return;
	    }
	    refreshToken = authHeader.substring(7);
	    userEmail = config.extractUsername(refreshToken);
	    if (userEmail != null) {
	      var user = this.repository.findByEmail(userEmail)
	              .orElseThrow();
	      if (config.validateToken(refreshToken, user)) {
	        var accessToken = config.generateToken(user);
	        revokeAllUserTokens(user);
	        saveUserToken(user, accessToken);
	        var authResponse = AuthResponse.builder()
	                .accessToken(accessToken)
	                .refreshToken(refreshToken)
	                .mfaEnabled(false)
	                .build();
	       
	        try {
	            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
	        } catch (IOException e) {
	            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        }

	        
	      }
	    }
		
	}

	@Override
	public AuthResponse verifyCode(VerificationRequest verificationRequest) {
		
		User user = repository
				.findByEmail(verificationRequest.getEmail())
				.orElseThrow( () -> new EntityNotFoundException(
						String.format("user not found with %s",
								verificationRequest.getEmail())) );  
		
		if(tfa.isOtpNotValid(user.getSecret(), verificationRequest.getCode())) {
			
			throw new BadCredentialsException("code is not correct ");
			
		}
		
		var jwtToken  = config.generateToken(user);
		
		return AuthResponse.builder()
				.accessToken(jwtToken)
				.mfaEnabled(user.isMfaEnabled())
				.build();
	}

//	logout 
	@Override
	public void revokeToken(String token) {
		
		var storedToken = tokenRepository.findByToken(token);
		
		if(storedToken.isPresent()) {
			Token tokenEntity = storedToken.get();
			tokenEntity.setRevoked(true);
			tokenEntity.setExpired(true);
			tokenRepository.save(tokenEntity);
		}
		
		
	}
	
	
	
	@Override
	public void sendValidationEmail(User user) {
		
		var newToken = generateAndSaveActivationToken(user);

	    try {
			emailService.sendEmail(
			        user.getEmail(),
			        user.getFirstname(),
			        EmailTemplateName.ACTIVATE_ACCOUNT,
			        activationUrl,
			        newToken,
			        "Account activation"
			        );
		} catch (MessagingException e) {
			
			e.printStackTrace();
		}

	}
	
	
	@Override
	public String generateAndSaveActivationToken(User user) {
		String generatedToken = generateActivationCode(6);
	    var token = Token.builder()
	            .token(generatedToken)
	            .createdAt(LocalDateTime.now())
	            .expiresAt(LocalDateTime.now().plusMinutes(15))
	            .user(user)
	            .build();
	    tokenRepository.save(token);

	    return generatedToken;
	}



	@Override
	public String generateActivationCode(Integer length) {
		
		String characters = "0123456789";
	    StringBuilder codeBuilder = new StringBuilder();

	    SecureRandom secureRandom = new SecureRandom();

	    for (int i = 0; i < length; i++) {
	        int randomIndex = secureRandom.nextInt(characters.length());
	        codeBuilder.append(characters.charAt(randomIndex));
	    }

	    return codeBuilder.toString();
		
		
	}
	
	

@Override
public void activateAccount(String token) {
	
	Token savedToken = tokenRepository.findByToken(token)
			
            
            .orElseThrow(() -> new RuntimeException("Invalid token"));
    if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
        sendValidationEmail(savedToken.getUser());
        throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
    }
    System.out.println(savedToken);

    var user = repository.findById(savedToken.getUser().getId())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    user.setEnabled(true);
    repository.save(user);

    savedToken.setValidatedAt(LocalDateTime.now());
    tokenRepository.save(savedToken);
	
}



}
