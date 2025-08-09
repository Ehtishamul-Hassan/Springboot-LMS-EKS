package com.java.service.Impl;

import java.security.Principal;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.dtos.ChangePasswordRequest;
import com.java.model.User;
import com.java.repository.UserRepository;
import com.java.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {

	private PasswordEncoder passwordEncoder;
	private UserRepository repository;

	public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository repository) {

		this.passwordEncoder = passwordEncoder;
		this.repository = repository;
	}

	@Override
	public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

		// check if the current password is correct
		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {

			throw new BadCredentialsException("Current password is incorrect");

		}

		// check if the two passwords are the same
		if (!request.getNewPassword().equals(request.getConfirmationPassword())) {

			throw new BadCredentialsException("New password and confirmation password do not match.");

		}

		// update the password
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));

		// save the new password
		repository.save(user);

	}

}
