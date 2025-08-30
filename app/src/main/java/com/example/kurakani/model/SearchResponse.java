package com.example.kurakani.model;

import com.example.kurakani.model.User;
import com.example.kurakani.viewmodel.ProfileModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {
    private boolean success;

    private List<ProfileModel> users;
    private List<ProfileResponse.User> user;

    private List<String> interests;

    public boolean isSuccess() { return success; }

    public List<ProfileResponse.User> getUser() { return user; }

    public List<ProfileModel> getUsers() { return users; }


    public void setUsers(List<ProfileResponse.User> user) {
        this.user = user;
    }
    public List<String> getInterests() { return interests; }

}
