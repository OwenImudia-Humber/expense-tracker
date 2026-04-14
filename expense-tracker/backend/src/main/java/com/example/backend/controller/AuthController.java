package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // REGISTER
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            return "Username already taken";
        }
        if (userService.existsByEmail(request.getEmail())) {
            return "Email already registered";
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userService.saveUser(user);

        return "User registered successfully";
    }

    // LOGIN
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        User user = userService.findByUsername(request.getUsername());

        if (user == null) {
            return "User not found";
        }

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return "Login successful";
        } else {
            return "Invalid credentials";
        }
    }
}