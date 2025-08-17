package com.example.kurakani.model;

import java.util.List;

public class ProfileRequest {
    public String fullname;

    public int age;
    public String gender;
    public String profile_photo_base64;  // matches Laravel
    public String purpose;
    public String job;
    public List<String>  interests;
    public String education;
    public String about;

    public ProfileRequest(String fullname, int age, String gender,
                          String profileBase64,
                          String purpose, String job, List<String> interests,
                          String education, String about) {
        this.fullname = fullname;
        this.age = age;
        this.gender = gender;
        this.profile_photo_base64 = profileBase64;   // match Laravel key
        this.purpose = purpose;
        this.job = job;
        this.interests = interests;
        this.education = education;
        this.about = about;
    }
}
