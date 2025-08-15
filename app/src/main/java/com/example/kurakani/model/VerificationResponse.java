package com.example.kurakani.model;

public class VerificationResponse {
    private boolean error;
    private String message;
    private String detected_gender;
    private String user_gender;

    // getters
    public boolean isError() { return error; }
    public String getMessage() { return message; }
    public String getDetected_gender() { return detected_gender; }
    public String getUser_gender() { return user_gender; }
}

