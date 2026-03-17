package com.solvemate.solvemate.service;

import com.solvemate.solvemate.dto.LoginRequest;
import com.solvemate.solvemate.dto.RegisterRequest;
import com.solvemate.solvemate.model.User;

public interface UserService {

    User registerUser(RegisterRequest registerRequest);

    String loginUser(LoginRequest loginRequest);

    String logoutUser();
}