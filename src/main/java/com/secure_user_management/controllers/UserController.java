package com.secure_user_management.controllers;

import com.secure_user_management.dto.LoginRequestDTO;
import com.secure_user_management.dto.LoginResponse;
import com.secure_user_management.dto.UserRegistrationDTO;
import com.secure_user_management.entity.User;
import com.secure_user_management.exceptions.UserAlreadyExistsException;
import com.secure_user_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    @Autowired
    private UserService userService;

    // Register User
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User successfully registered");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // List Users
    @GetMapping("/list-users")
    public ResponseEntity<?> listUser() {
        List<UserRegistrationDTO> users = userService.listAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Users Found");
        }
        return ResponseEntity.ok(users);
    }

    // User Login By Username
    @PostMapping("/username-login")
    public ResponseEntity<LoginResponse> loginUserByUsername(@Valid @RequestBody LoginRequestDTO loginBody) {
        String jwt = userService.loginUserByUsername(loginBody);

        if (jwt != null) {
            LoginResponse response = new LoginResponse();
            response.setMessage("Login Success");
            response.setJwt(jwt);
            response.setStatus(HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } else {
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setMessage("Invalid Credentials");
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    // User Login By Email
    @PostMapping("/email-login")
    public ResponseEntity<LoginResponse> loginUserByEmail(@Valid @RequestBody LoginRequestDTO loginBody) {
        String jwt = userService.loginUserByEmail(loginBody);

        if (jwt != null) {
            LoginResponse response = new LoginResponse();
            response.setMessage("Login Success");
            response.setJwt(jwt);
            response.setStatus(HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } else {
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setMessage("Invalid Credentials");
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}