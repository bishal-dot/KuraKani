package com.example.kurakani.model;

public class SignupRequest {
    private String username;
    private String email;
    private String password;

    public SignupRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
