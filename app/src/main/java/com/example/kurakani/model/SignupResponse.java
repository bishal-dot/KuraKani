package com.example.kurakani.model;

public class SignupResponse {

    private boolean error;
    private String reason;
    private User response;  // Your User model
    private String token;

    public boolean isError() { return error; }
    public String getReason() { return reason; }
    public User getResponse() { return response; }
    public String getToken() { return token; }
}
