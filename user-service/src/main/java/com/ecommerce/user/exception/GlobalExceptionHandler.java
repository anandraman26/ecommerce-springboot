package com.ecommerce.user.exception;

import java.util.HashMap;
import java.util.Map;

import com.ecommerce.user.util.JwtAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    GlobalExceptionHandler(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
		@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<Map<String, Object>> handleExists(ResourceAlreadyExistsException ex){
			Map<String, Object> m = Map.of("error","Conflict","message",ex.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(m);
		}
		@ExceptionHandler(ResourceNotFoundException.class)
		public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex){
			Map<String, Object> m  = Map.of("Error","Not Found","message",ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(m);
		}
		@ExceptionHandler(MethodArgumentNotValidException.class)
		public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex){
			Map<String, Object> error = new HashMap<>();
			ex.getBindingResult().getFieldErrors().forEach(err -> error.put(err.getField(), err.getDefaultMessage()));
			Map<String, Object> body = Map.of("error","Validation Failed", "details",error);
			return ResponseEntity.badRequest().body(body);
		}
		
		@ExceptionHandler(Exception.class)
		public ResponseEntity<Map<String, Object>> handleAll(Exception ex){
			Map<String, Object> m = Map.of("error","InternalServer Error","message",ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(m);
		}
}
