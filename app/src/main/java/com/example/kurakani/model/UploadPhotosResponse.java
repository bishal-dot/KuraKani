package com.example.kurakani.model;

import java.util.List;

public class UploadPhotosResponse {
    public boolean error;
    public String message;
    public String profile_url;
    public List<Photo> photos;

    public static class Photo {
        public int id;
        public String url;

        public Photo(int id, String url){
            this.id = id;
            this.url = url;
        }
    }
}
