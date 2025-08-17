package com.example.kurakani.model;

import java.util.ArrayList;
import java.util.List;

public class ProfileResponse {
    public boolean error;
    public String message;
    public User user;

    public static class User {
        public int id;
        public String fullname;
        public String username;
        public String email;

        public Integer age;             // nullable integer
        public String gender;
        public String purpose;
        public List<String> interests;  // JSON array from backend
        public String about;            // 'about' from backend
        public String bio;              // optional mapping
        public String job;
        public String education;
        public String profile;          // full URL or null
        public boolean is_verified;
        public int matches_count;
        public List<Photo> photos;

        public static class Photo {
            public int id;
            public String url;
        }

        // Convert backend photos to a simpler list for RecyclerView
        public List<UserPhoto> getUserPhotos() {
            List<UserPhoto> list = new ArrayList<>();
            if (photos != null) {
                for (Photo p : photos) {
                    list.add(new UserPhoto(p.id, p.url));
                }
            }
            return list;
        }
    }

    public static class UserPhoto {
        public final int id;
        public final String url;

        public UserPhoto(int id, String url) {
            this.id = id;
            this.url = url;
        }
    }
}
