package com.example.kurakani.model;

public class Match {
    public String name;
    public int age;
    public String bio;
    public  int avatarResId;

    public Match(String name, String bio, int avatarResId, int age){
        this.name = name;
        this.bio = bio;
        this.avatarResId = avatarResId;
        this.age = age;
    }
}
