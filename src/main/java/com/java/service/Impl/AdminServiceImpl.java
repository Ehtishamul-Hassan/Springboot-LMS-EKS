package com.java.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.dtos.RegisterRequest;
import com.java.dtos.UserDto;
import com.java.enums.Role;
import com.java.exception.UserNotFoundException;
import com.java.model.User;
import com.java.repository.UserRepository;
import com.java.service.IAdminService;
import com.java.tfa.TwoFactorAuthenticationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final TwoFactorAuthenticationService tfa;

	public UserDto addUser(RegisterRequest request) {

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

		if (request.isMfaEnabled()) {
			user.setSecret(tfa.generateNewSecret());
		}

		var savedUser = repository.save(user);

		return UserDto.builder()
				.id(savedUser.getId())
				.firstname(savedUser.getFirstname())
				.lastname(savedUser.getLastname())
				.email(savedUser.getEmail())
				.role(savedUser.getRole())
				.mfaEnabled(user.isMfaEnabled())
				.build();

	}

	@Override
	public void removeUser(Long userId) {

		User user = repository.findById(userId)
				.orElseThrow(() -> new 
						UserNotFoundException("User with ID " + userId + " not found"));

		// Delete the user
		repository.delete(user);

	}

	@Override
	public List<UserDto> viewAllUsers() {

		// Fetch all users from the database
		List<User> users = repository.findAll();

		// Map each User to UserDto
		List<UserDto> userDtos = users.stream()
				.map(user -> UserDto.builder()
						.id(user.getId())
						.firstname(user.getFirstname())
						.lastname(user.getLastname())
						.email(user.getEmail())
						.role(user.getRole()) // Assuming role is
																									// Enum type in User
						.mfaEnabled(user.isMfaEnabled()).build())
				.collect(Collectors.toList());

		// Return the list of UserDto objects
		return userDtos;
	}

}
