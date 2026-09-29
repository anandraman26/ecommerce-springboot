package com.ecommerce.user.service.impl;

import com.ecommerce.user.dto.LoginRequestDto;
import com.ecommerce.user.dto.LoginResponseDto;
import com.ecommerce.user.dto.UserCreateRequestDto;
import com.ecommerce.user.dto.UserResponseDto;

public interface UserService {
	 	UserResponseDto register(UserCreateRequestDto dto);
	 	LoginResponseDto login(LoginRequestDto dto);
	 	UserResponseDto getById(Long id);
}
