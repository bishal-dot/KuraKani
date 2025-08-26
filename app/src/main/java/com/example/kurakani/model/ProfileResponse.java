package com.example.kurakani.model;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ProfileResponse {
    public boolean error;
    public String message;
    public User user;

    public static class User {
        public int id;
        @Nullable public String fullname;
        @Nullable public String username;
        @Nullable public String email;

        @Nullable public Integer age;
        @Nullable public String gender;
        @Nullable public String purpose;
        @Nullable public List<String> interests;
        @Nullable public String about;
        @Nullable public String bio;
        @Nullable public String job;
        @Nullable public String education;
        @Nullable public String profile;      // full URL
        public boolean is_verified;
        public int matches_count;
        @Nullable public List<Photo> photos;

        public static class Photo {
            public int id;
            public String url;
        }

        // Convert backend photos to RecyclerView-friendly list
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
