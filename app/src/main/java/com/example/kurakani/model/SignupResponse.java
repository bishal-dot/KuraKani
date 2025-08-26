package com.example.kurakani.model;

public class SignupResponse {
    private boolean error;
    private String message;
    private User user;

    public boolean isError() { return error; }
    public String getMessage() { return message; }
    public User getUser() { return user; }
}
