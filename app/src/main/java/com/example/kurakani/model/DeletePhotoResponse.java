package com.example.kurakani.model;

public class DeletePhotoResponse {

    public boolean error;   // true/false
    private String message;

    // ✅ Getters
    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    // ✅ Setters
    public void setError(boolean error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
