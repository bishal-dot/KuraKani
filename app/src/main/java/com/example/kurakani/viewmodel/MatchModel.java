package com.example.kurakani.viewmodel;

public class MatchModel {
    public String name;
    public int age;
    public String bio;
    public  int avatarResId;

    public MatchModel(String name, String bio, int avatarResId, int age){
        this.name = name;
        this.bio = bio;
        this.avatarResId = avatarResId;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public String getBio() {
        return bio;
    }
}
