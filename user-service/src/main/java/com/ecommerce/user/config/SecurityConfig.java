package com.ecommerce.user.config;

import com.ecommerce.user.util.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
		private final JwtAuthenticationFilter JwtFilter;
		@Bean
		public PasswordEncoder PasswordEncoder() {
			return new BCryptPasswordEncoder();
		}
		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
			http.csrf(csrf -> csrf.disable())
			.sessionManagement(sess ->sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth.requestMatchers("/api/users/register", "/api/users/login", "/actuator/**").permitAll()
					.anyRequest().authenticated())
			.addFilterBefore(JwtFilter, UsernamePasswordAuthenticationFilter.class);
			return http.build();
		}
}
