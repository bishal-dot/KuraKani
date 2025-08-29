package com.example.kurakani.model;

import com.example.kurakani.viewmodel.ProfileModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {
    private boolean success;
    private List<ProfileResponse.User> users;

    public boolean isSuccess() { return success; }
    public List<ProfileResponse.User> getUsers() { return users; }


    public void setUsers(List<ProfileResponse.User> users) {
        this.users = users;
    }

}
