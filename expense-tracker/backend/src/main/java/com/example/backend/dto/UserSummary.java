package com.example.backend.dto;

import com.example.backend.entity.Role;

public record UserSummary(Long id, String username, String email, Role role) {}
