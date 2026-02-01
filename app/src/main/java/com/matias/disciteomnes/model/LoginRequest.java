package com.matias.disciteomnes.model;

// Request model for user login
public class LoginRequest {
    public String email;       // User's email address
    public String password;    // User's password

    // Constructor to initialize login credentials
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
