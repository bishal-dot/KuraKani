package com.example.kurakani.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("error")
    private boolean error;

    @SerializedName("token")
    private String token;

    // rename message to reason to match backend response key
    @SerializedName("reason")
    private String reason;

    // Optional: you can include user data here if you want to access it
     @SerializedName("message")
     private String message;

    @SerializedName("user_id")
    private int userId;

    public boolean isError() {
        return error;
    }

    public int getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getReason() {
        return reason;
    }

    // Optional getter for user if included
     public String  getMessage() {
         return message;
     }
}
