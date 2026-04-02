package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
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

        User user = new User();
        user.setUsername(request.username);
        user.setEmail(request.email);

        // encrypt password
        user.setPassword(passwordEncoder.encode(request.password));

        userService.saveUser(user);

        return "User registered successfully";
    }

    // LOGIN
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        User user = userService.findByUsername(request.username);

        if (user == null) {
            return "User not found";
        }

        if (passwordEncoder.matches(request.password, user.getPassword())) {
            return "Login successful";
        } else {
            return "Invalid credentials";
        }
    }
}