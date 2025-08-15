package com.example.kurakani.model;

import java.util.List;

public class ProfileResponse {
    public boolean error;
    public String message;

    public User user;

    public static class User {
        public String fullname;

        public String email;

        public String username;

        public int age;
        public String gender;
        public String purpose;
        public String job;
        public String interests;
        public String education;
        public String bio;

        public String profile;      // profile image filename
        public int matches_count;   // number of matches

        public List<Photo> photos; // <-- changed

        public static class Photo {
            public int id;
            public String url;
        } // URLs or paths of uploaded photos
    }
}
