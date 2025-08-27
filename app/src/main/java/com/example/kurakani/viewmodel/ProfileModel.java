package com.example.kurakani.viewmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfileModel implements Serializable {
    private int id;
    private String fullname;
    private String username;
    private int age;
    private String gender;
    private String purpose;
    private String about;
    private String profile; // profile image URL
    private List<String> interests;
    private List<String> photos;
    private boolean isVerified;

    // Full constructor
    public ProfileModel(int id, String fullname, String username, int age, String gender,
                        String purpose, String about, String profile,
                        List<String> interests, List<String> photos, boolean isVerified ) {
        this.id = id;
        this.fullname = fullname != null ? fullname : "";
        this.username = username != null ? username : "";
        this.age = age;
        this.gender = gender != null ? gender : "";
        this.purpose = purpose != null ? purpose : "";
        this.about = about != null ? about : "";
        this.profile = profile != null ? profile : "";
        this.interests = interests != null ? interests : new ArrayList<>();
        this.photos = photos != null ? photos : new ArrayList<>();
        this.isVerified = isVerified;
    }

    // Minimal constructor for homepage
    public ProfileModel(int id, String fullname, String username, int age,
                        String profile, List<String> interests, Boolean isVerified) {
        this(id, fullname, username, age, "", "", "", profile, interests, new ArrayList<>(), isVerified);
    }

    // Getters
    public int getId() { return id; }
    public String getFullname() { return fullname; }
    public String getUsername() { return username; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getPurpose() { return purpose; }
    public String getAbout() { return about; }
    public String getProfile() { return profile; }
    public List<String> getInterests() { return interests; }
    public List<String> getPhotos() { return photos; }
    public boolean isVerified() { return isVerified; }
}
