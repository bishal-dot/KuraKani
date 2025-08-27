package com.example.kurakani.model;

import com.google.gson.annotations.SerializedName;

public class SignupResponse {

    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;  // add token field

    @SerializedName("user")
    private User user;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public static class User {
        @SerializedName("id")
        private int id;

        @SerializedName("username")
        private String username;

        @SerializedName("email")
        private String email;

        @SerializedName("profile_complete")
        private Boolean profileComplete;

        @SerializedName("is_verified")
        private boolean isVerified;

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public Boolean isProfileComplete() {
            return profileComplete;
        }

        public boolean isVerified() {
            return isVerified;
        }
    }
}
