package com.ecommerce.user.exception;

public class ResourceAlreadyExistsException extends RuntimeException{
	public ResourceAlreadyExistsException(String msg) {
		super(msg);
	}
}
