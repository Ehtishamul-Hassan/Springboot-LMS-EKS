package com.java.service;

import java.util.List;

import com.java.dtos.RegisterRequest;
import com.java.dtos.UserDto;

public interface IAdminService {
	
	
	
	
	UserDto addUser(RegisterRequest request);
	
	void removeUser(Long userId);
	
	List<UserDto> viewAllUsers();
	

}
