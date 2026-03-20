package com.solvemate.dto;

public class LoginResponse {

    private String message;
    private String role;
    private String fullName;

    public LoginResponse() {
    }

    public LoginResponse(String message, String role, String fullName) {
        this.message = message;
        this.role = role;
        this.fullName = fullName;
    }

    public String getMessage() {
        return message;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}