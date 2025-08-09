package com.java.service;

import java.security.Principal;

import com.java.dtos.ChangePasswordRequest;

public interface IUserService {
	
	void changePassword(ChangePasswordRequest request , Principal connectedUser);

}
