package com.ecommerce.user.mapper;

import com.ecommerce.user.dto.UserCreateRequestDto;
import com.ecommerce.user.dto.UserResponseDto;
import com.ecommerce.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
		 private PasswordEncoder passwordEncoder;
		 public UserMapper(PasswordEncoder passwordEncoder) {
			 this.passwordEncoder = passwordEncoder;
		 }
		 public User toEntity(UserCreateRequestDto dto) {
			return User.builder().name(dto.getName()).email(dto.getEmail())
			 .password(passwordEncoder.encode(dto.getPassword()))
			 .phone(dto.getPhone())
			 .roles("ROLE_USER").build();
		 }
		 public UserResponseDto toDto(User user) {
			return UserResponseDto.builder().id(user.getId())
					.name(user.getName()).email(user.getEmail())
					.phone(user.getPhone()).roles(user.getRoles())
					.createdAt(user.getCreatedAt()).updatedAt(user.getUpdatedAt())
					.build();
		 }
}
