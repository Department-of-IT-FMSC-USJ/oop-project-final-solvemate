package com.solvemate.solvemate.service.impl;

import com.solvemate.solvemate.dto.LoginRequest;
import com.solvemate.solvemate.dto.RegisterRequest;
import com.solvemate.solvemate.model.User;
import com.solvemate.solvemate.repository.UserRepository;
import com.solvemate.solvemate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        user.setStatus("ACTIVE");

        return userRepository.save(user);
    }

    @Override
    public String loginUser(LoginRequest loginRequest) {

        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isEmpty()) {
            return "Invalid email";
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return "Invalid password";
        }

        return "Login successful. Role: " + user.getRole();
    }

    @Override
    public String logoutUser() {
        return "User logged out successfully";
    }
}