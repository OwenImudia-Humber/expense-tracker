package com.example.backend.controller;

import com.example.backend.dto.RegisterRequest;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class RegisterPageController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public RegisterPageController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userService.existsByUsername(request.getUsername())) {
            bindingResult.rejectValue("username", "duplicate", "That username is already taken.");
            return "register";
        }
        if (userService.existsByEmail(request.getEmail())) {
            bindingResult.rejectValue("email", "duplicate", "That email is already registered.");
            return "register";
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("registered", true);
        return "redirect:/login";
    }
}
