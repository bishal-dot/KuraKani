package com.example.kurakani.model;

public class LoginResponse {
    private boolean error;
    private String message;
    private User user;
    private String token;
    private boolean profile_complete;

    public boolean isError() { return error; }
    public String getMessage() { return message; }
    public User getUser() { return user; }
    public String getToken() { return token; }
    public boolean isProfileComplete() { return profile_complete; }
}
