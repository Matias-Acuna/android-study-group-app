package com.matias.disciteomnes.model;

// Response model returned after a successful login
public class LoginResponse {
    public boolean success;   // Indicates whether the login attempt was successful
    public String token;      // JWT token returned by the server for authenticated access
    public String userId;     // Unique identifier of the authenticated user
    public String name;       // Display name of the user
}
