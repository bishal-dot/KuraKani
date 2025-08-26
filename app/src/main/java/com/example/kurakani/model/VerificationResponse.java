package com.example.kurakani.model;

import com.google.gson.annotations.SerializedName;

public class VerificationResponse {

    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("attempts")
    private int attempts; // number of failed attempts returned by backend

    // Getters and setters
    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
}
