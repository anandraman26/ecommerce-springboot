package com.ecommerce.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
		private String token;
		private String tokenType = "Bearer";
		private Long userId;
		private String email;
		private String name;
}
