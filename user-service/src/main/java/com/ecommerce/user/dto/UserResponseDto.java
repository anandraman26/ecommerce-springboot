package com.ecommerce.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
		private Long id;
		private String name;
		private String email;
		private String phone;
		private String roles;
		private Instant createdAt;
		private Instant updatedAt;
}
