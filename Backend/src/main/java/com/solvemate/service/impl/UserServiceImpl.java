package com.solvemate.service.impl;

import com.solvemate.dto.*;
import com.solvemate.model.User;
import com.solvemate.repository.UserRepository;
import com.solvemate.service.UserService;
import com.solvemate.exception.BadRequestException;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // reg part
    @Override
    public ApiResponse register(RegisterRequest request) {

        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        // Hash password using BCrypt
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        user.setRole(request.getRole());

        // Save to DB
        userRepository.save(user);

        return new ApiResponse("User registered successfully");
    }

    // login
    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email"));

        // Compare password with hashed password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid password");
        }

        LoginResponse response = new LoginResponse();
        response.setMessage("Login successful");
        response.setRole(user.getRole());
        response.setFullName(user.getFullName());

        return response;
    }

    // logout
    @Override
    public ApiResponse logout() {
        return new ApiResponse("Logged out successfully");
    }
}