package com.ecommerce.user.mapper;

import org.springframework.stereotype.Component;

import com.ecommerce.user.dto.LoginResponseDto;
import com.ecommerce.user.entity.User;

@Component
public class AuthMapper {
	public LoginResponseDto toLoginResponse(User user, String token) {
		return new LoginResponseDto(token,"Bearer", user.getId(),user.getEmail(),user.getName());
	}
}
