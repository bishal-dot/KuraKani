package com.example.kurakani.model;

public class ProfileModel {
    private int imageResId;
    private String name;
    private String bio;
    private int age;
    private String hobbies;

    public ProfileModel(int imageResId, String name, int age, String bio, String hobbies) {
        this.imageResId = imageResId;
        this.name = name;
        this.age = age;
        this.bio = bio;
        this.hobbies = hobbies;
    }

    public int getImageResId() { return imageResId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getBio() { return bio; }

    public String getHobbies() {
        return hobbies;
    }
}
