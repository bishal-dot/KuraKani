package com.example.kurakani.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    @SerializedName("id")
    private int id; // user ID from backend

    @SerializedName("name")
    private String name;

    @SerializedName("username")
    private String username;

    @SerializedName("age")
    private Integer age;

    @SerializedName("profile")
    private String profile;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    private String confirmPassword;

    @SerializedName("interests")
    private List<String> interests;

    @SerializedName("fullname")
    public String fullname;

    // Additional fields
    @SerializedName("gender")
    private String gender;

    @SerializedName("purpose")
    private String purpose;

    @SerializedName("about")
    private String about;

    @SerializedName("photos")
    private List<Photo> photos;

    // Unread messages count
    @SerializedName("unread_count")
    private int unreadCount;

    // ===================
    // Constructors
    // ===================
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password, String confirmPassword) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User() {
    }

    @Override
    public String toString() {
        if (id == 0) return "No users found";
        return fullname != null ? fullname : "";
    }

    // ===================
    // Getters
    // ===================
    public int getId() { return id; }
    public String getName() { return name; }
    public String getFullname() { return fullname; }
    public String getUsername() { return username; }
    public Integer getAge() { return age; }
    public String getProfile() { return profile; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
    public List<String> getInterests() { return interests; }
    public String getGender() { return gender; }
    public String getPurpose() { return purpose; }
    public String getAbout() { return about; }
    public List<Photo> getPhotos() { return photos; }
    public int getUnreadCount() { return unreadCount; }

    // ===================
    // Setters
    // ===================
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public void setUsername(String username) { this.username = username; }
    public void setAge(Integer age) { this.age = age; }
    public void setProfile(String profile) { this.profile = profile; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public void setInterests(List<String> interests) { this.interests = interests; }
    public void setGender(String gender) { this.gender = gender; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setAbout(String about) { this.about = about; }
    public void setPhotos(List<Photo> photos) { this.photos = photos; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }

    // ===================
    // Nested Photo class
    // ===================
    public static class Photo implements Serializable {
        @SerializedName("id")
        private int id;

        @SerializedName("url")
        private String url;

        public int getId() { return id; }
        public String getUrl() { return url; }
        public void setId(int id) { this.id = id; }
        public void setUrl(String url) { this.url = url; }
    }
}
