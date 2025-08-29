package com.example.kurakani.model;

public class UserResponse {
    private int id;
    private String username;
    public String email;
    private Boolean profile_complete;
    private Boolean is_verified;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean isProfileComplete() { return profile_complete != null && profile_complete; }
    public void setProfileComplete(Boolean profile_complete) { this.profile_complete = profile_complete; }

    public boolean isVerified() {
        return is_verified;
    }
    public void setIsVerified(Boolean is_verified) { this.is_verified = is_verified; }
}
