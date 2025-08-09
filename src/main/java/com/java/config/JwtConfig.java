package com.java.config;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtConfig {
	
	@Value("${jwt.secret-key}")
	private String secretKey;
	
	@Value("${jwt.expiration}")
	private Long jwtExpiration;
	  
	@Value("${jwt.refresh-token-expiration}")
	private Long refreshExpiration;
	
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = Jwts.parser()
				.verifyWith(getSigningKey()) // Use the signing key
				.build().parseSignedClaims(token)
				.getPayload();
		return claimsResolver.apply(claims);
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

//	public String generateToken(UserDetails userDetails) {
//		Map<String, Object> claims = new HashMap<>();
//		return Jwts.builder()
//				.claims(claims)
//				.subject(userDetails.getUsername()).issuedAt(new Date())
//				.expiration(new Date(System.currentTimeMillis() + jwtExpiration))
//				.signWith(getSigningKey())
//				.compact();
//
//	}
//	
//	public String generateRefreshToken(UserDetails userDetails) {
//        return Jwts.builder()
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + refreshExpiration)) // Use refreshExpiration
//                .signWith(getSigningKey())
//                .compact();
//    }
	
	
	public String generateToken(UserDetails userDetails) {
	    return createToken(new HashMap<>(), userDetails.getUsername(), jwtExpiration);
	}

	public String generateRefreshToken(UserDetails userDetails) {
	    return createToken(new HashMap<>(), userDetails.getUsername(), refreshExpiration);
	}

	private String createToken(Map<String, Object> claims, String subject, Long expirationTime) {
	    return Jwts.builder()
	            .claims(claims) 
	            .subject(subject) 
	            .issuedAt(new Date())
	            .expiration(new Date(System.currentTimeMillis() + expirationTime)) 
	            .signWith(getSigningKey()) 
	            .compact(); 
	}

	
	

	public Boolean validateToken(String token, UserDetails userDetails) {

		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

	}
	

	
	

}
