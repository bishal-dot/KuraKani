package com.example.kurakani.model;

import androidx.annotation.Nullable;
import java.util.List;

public class ProfileRequest {
    @Nullable private String fullname;
    @Nullable private Integer age;
    @Nullable private String gender;
    @Nullable private String purpose;
    @Nullable private String job;
    @Nullable private List<String> interests; // converted to JSON when sending
    @Nullable private String education;
    @Nullable private String about;

    public ProfileRequest() {}

    public ProfileRequest(@Nullable String fullname,
                          @Nullable Integer age,
                          @Nullable String gender,
                          @Nullable String purpose,
                          @Nullable String job,
                          @Nullable List<String> interests,
                          @Nullable String education,
                          @Nullable String about) {
        this.fullname = fullname;
        this.age = age;
        this.gender = gender;
        this.purpose = purpose;
        this.job = job;
        this.interests = interests;
        this.education = education;
        this.about = about;
    }

    // Getters and Setters
    @Nullable
    public String getFullname() { return fullname; }
    public void setFullname(@Nullable String fullname) { this.fullname = fullname; }

    @Nullable
    public Integer getAge() { return age; }
    public void setAge(@Nullable Integer age) { this.age = age; }

    @Nullable
    public String getGender() { return gender; }
    public void setGender(@Nullable String gender) { this.gender = gender; }

    @Nullable
    public String getPurpose() { return purpose; }
    public void setPurpose(@Nullable String purpose) { this.purpose = purpose; }

    @Nullable
    public String getJob() { return job; }
    public void setJob(@Nullable String job) { this.job = job; }

    @Nullable
    public List<String> getInterests() { return interests; }
    public void setInterests(@Nullable List<String> interests) { this.interests = interests; }

    @Nullable
    public String getEducation() { return education; }
    public void setEducation(@Nullable String education) { this.education = education; }

    @Nullable
    public String getAbout() { return about; }
    public void setAbout(@Nullable String about) { this.about = about; }
}
