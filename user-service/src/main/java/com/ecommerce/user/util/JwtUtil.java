package com.ecommerce.user.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ecommerce.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final UserRepository userRepository;
	private final SecretKey key;
	private final long jwtExpirationMs;
	public JwtUtil( @Value("${app.jwt.secret}") String Secret,@Value("${app.jwt.expiration-ms}") long jwtExpirationMs, UserRepository userRepository) {
		this.key = Keys.hmacShaKeyFor(Secret.getBytes());
		this.jwtExpirationMs =jwtExpirationMs;
		this.userRepository = userRepository;
	}
	public String generateToken(Long userId, String email, String role) {
		long now = System.currentTimeMillis();
		return Jwts.builder()
		.subject(String.valueOf(userId))
		.claim("email", email)
		.claim("role", role)
		.issuedAt(new Date(now))
		.expiration(new Date(now + jwtExpirationMs)).signWith(key)
		.compact();
	}
	public Jws<Claims> validateToken(String token){
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
	}
	
	public Long getUserIdFromToken(String token) {
		Claims claims = validateToken(token).getBody();
		return Long.valueOf(claims.getSubject()); 
	}
}
