package com.example.kurakani.viewmodel;

public class StoryModel {
    private int imageResId;
    private String name;

    public StoryModel(int imageResId, String name) {
        this.imageResId = imageResId;
        this.name = name;
    }

    public int getImageResId() { return imageResId; }
    public String getName() { return name; }

}

