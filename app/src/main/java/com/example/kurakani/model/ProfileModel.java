package com.example.kurakani.model;

public class ProfileModel {
    private int imageResId;
    private String name;
    private int age;
    private String address;

    public ProfileModel(int imageResId, String name, int age, String address) {
        this.imageResId = imageResId;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public int getImageResId() { return imageResId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getAddress() { return address; }

}
