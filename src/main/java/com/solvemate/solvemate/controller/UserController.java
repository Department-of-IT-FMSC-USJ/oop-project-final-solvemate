package com.solvemate.solvemate.controller;

import com.solvemate.solvemate.dto.LoginRequest;
import com.solvemate.solvemate.dto.RegisterRequest;
import com.solvemate.solvemate.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Map<String, String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        return Map.of("message", "User registered successfully");
    }

    @PostMapping("/login")
    public Map<String, String> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        String result = userService.loginUser(loginRequest);
        return Map.of("message", result);
    }

    @PostMapping("/logout")
    public Map<String, String> logoutUser() {
        String result = userService.logoutUser();
        return Map.of("message", result);
    }
}