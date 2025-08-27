package com.example.kurakani.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @SerializedName("error")
    public boolean error;

    @SerializedName("message")
    public String message;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
