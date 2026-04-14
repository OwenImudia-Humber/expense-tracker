package com.example.backend.controller;

import com.example.backend.dto.UserSummary;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserSummary> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserController::toSummary)
                .toList();
    }

    private static UserSummary toSummary(User u) {
        return new UserSummary(u.getId(), u.getUsername(), u.getEmail(), u.getRole());
    }
}