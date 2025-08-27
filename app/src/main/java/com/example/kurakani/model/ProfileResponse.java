package com.example.kurakani.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProfileResponse {

    private boolean success;
    private String message;
    private String token;
    private User user;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public User getUser() { return user; }

    // Make User Serializable
    public static class User implements Serializable {
        @SerializedName("id")
        public int id;

        @SerializedName("fullname")
        public String fullname;

        @SerializedName("username")
        public String username;

        @SerializedName("email")
        public String email;

        @SerializedName("age")
        public Integer age;

        @SerializedName("gender")
        public String gender;

        @SerializedName("purpose")
        public String purpose;

        @SerializedName("about")
        public String about;

        @SerializedName("job")
        public String job;

        @SerializedName("education")
        public String education;

        @SerializedName("profile")
        public String profile;

        @SerializedName("interests")
        public List<String> interests;

        @SerializedName("photos")
        public List<UserPhoto> photos;

        @SerializedName("matches_count")
        public Integer matches_count;

        @SerializedName("is_verified")
        public Boolean is_verified;

        // Make nested UserPhoto Serializable too
        public static class UserPhoto implements Serializable {
            @SerializedName("id")
            public int id;

            @SerializedName("url")
            public String url;

            public UserPhoto(int id, String url) {
                this.id = id;
                this.url = url;
            }
        }
    }
}
