package com.example.kurakani.model;

import java.util.List;

public class UploadPhotosResponse {
    public boolean error;
    public String message;
    public List<Photo> photos;

    public static class Photo {
        public int id;
        public String url;
    }
}
