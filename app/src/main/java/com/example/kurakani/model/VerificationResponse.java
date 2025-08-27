package com.example.kurakani.model;

import com.google.gson.annotations.SerializedName;
public class VerificationResponse {
    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("detected_gender")
    private String detectedGender;

    @SerializedName("user_gender")
    private String userGender;

    public boolean isError() { return error; }
    public String getMessage() { return message; }
    public String getDetectedGender() { return detectedGender; }
    public String getUserGender() { return userGender; }
}
