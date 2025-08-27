package com.example.kurakani.viewmodel;

public class MatchesModel {
    private String id;
    private String name;
    private String status; // "Matched" or "Pending"

    public MatchesModel(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

}
