package com.secure_user_management.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private int status;

    private String jwt;

    private String message;
}
