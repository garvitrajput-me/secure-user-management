package com.secure_user_management.exceptions;

public class UserNotFoundException extends Exception{

	private static final long serialVersionUID = 1L;
	
	 public UserNotFoundException(String msg) {
		 super(msg);
	 }
}
