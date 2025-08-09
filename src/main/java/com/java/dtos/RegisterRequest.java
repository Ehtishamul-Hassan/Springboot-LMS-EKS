package com.java.dtos;

import com.java.enums.Role;

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
public class RegisterRequest {

	private String firstname;
	private String lastname;
	private String email;
	private String password;
	private Role role;
	private boolean mfaEnabled;

}
