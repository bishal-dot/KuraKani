package com.example.kurakani.model;

import androidx.annotation.Nullable;
import java.util.List;

public class ProfileRequest {
    @Nullable public String fullname;
    @Nullable public Integer age;                  // nullable for partial updates
    @Nullable public String gender;
    @Nullable public String profile_photo_base64;
    @Nullable public String purpose;
    @Nullable public String job;
    @Nullable public List<String> interests;
    @Nullable public String education;
    @Nullable public String about;

    public ProfileRequest() {} // default constructor for partial updates

    public ProfileRequest(@Nullable String fullname,
                          @Nullable Integer age,
                          @Nullable String gender,
                          @Nullable String profilePhotoBase64,
                          @Nullable String purpose,
                          @Nullable String job,
                          @Nullable List<String> interests,
                          @Nullable String education,
                          @Nullable String about) {
        this.fullname = fullname;
        this.age = age;
        this.gender = gender;
        this.profile_photo_base64 = profilePhotoBase64;
        this.purpose = purpose;
        this.job = job;
        this.interests = interests;
        this.education = education;
        this.about = about;
    }
}
