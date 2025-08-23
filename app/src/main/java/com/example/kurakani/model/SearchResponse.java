package com.example.kurakani.model;

import com.example.kurakani.viewmodel.ProfileModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {
    private boolean success;
    private List<ProfileModel> users;

    public boolean isSuccess() { return success; }
    public List<ProfileModel> getUsers() { return users; }

}
