package com.java.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.dtos.ChangePasswordRequest;
import com.java.service.IUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final IUserService service;

	@PatchMapping
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
			Principal connectedUser) {

		service.changePassword(request, connectedUser);

		return ResponseEntity.ok().build();

	}
	
	

}
