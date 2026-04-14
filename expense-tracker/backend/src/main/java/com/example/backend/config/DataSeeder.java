package com.example.backend.config;

import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedAdminUser(UserRepository users, PasswordEncoder passwordEncoder) {
        return args -> {
            if (users.findByUsername("admin").isPresent()) {
                return;
            }
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@expensetracker.local");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            users.save(admin);
        };
    }
}
