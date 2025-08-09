package com.java.dtos;



import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuthResponse {
	
	
	private String accessToken;
	
	
	private String refreshToken;
	
	private boolean mfaEnabled;
	
	private String secretImageUri;
	
	private String message;

}
