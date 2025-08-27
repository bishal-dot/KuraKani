package com.example.kurakani.model;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ProfileResponse {

    private boolean error;
    private String message;
    private User user;

    // ✅ Getters and setters
    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // ================== Nested User ==================
    public static class User {
        private int id;
        @Nullable private String fullname;
        @Nullable private String username;
        @Nullable private String email;

        @Nullable private Integer age;
        @Nullable private String gender;
        @Nullable private String purpose;
        @Nullable private List<String> interests;
        @Nullable private String about;
        @Nullable private String bio;
        @Nullable private String job;
        @Nullable private String education;
        @Nullable private String profile;      // full URL
        private boolean is_verified;
        private int matches_count;
        @Nullable private List<Photo> photos;

        // ✅ Getters
        public int getId() { return id; }
        @Nullable public String getFullname() { return fullname; }
        @Nullable public String getUsername() { return username; }
        @Nullable public String getEmail() { return email; }
        @Nullable public Integer getAge() { return age; }
        @Nullable public String getGender() { return gender; }
        @Nullable public String getPurpose() { return purpose; }
        @Nullable public List<String> getInterests() { return interests; }
        @Nullable public String getAbout() { return about; }
        @Nullable public String getBio() { return bio; }
        @Nullable public String getJob() { return job; }
        @Nullable public String getEducation() { return education; }
        @Nullable public String getProfile() { return profile; }
        public boolean isVerified() { return is_verified; }
        public int getMatchesCount() { return matches_count; }
        @Nullable public List<Photo> getPhotos() { return photos; }

        // ✅ Convert backend photos to RecyclerView-friendly list
        public List<UserPhoto> getUserPhotos() {
            List<UserPhoto> list = new ArrayList<>();
            if (photos != null) {
                for (Photo p : photos) {
                    if (p != null && p.url != null) {
                        list.add(new UserPhoto(p.id, p.url));
                    }
                }
            }
            return list;
        }

        // ================== Nested Photo ==================
        public static class Photo {
            public int id;
            public String url;
        }
    }

    // ================== UserPhoto (for RecyclerView) ==================
    public static class UserPhoto {
        private final int id;
        private final String url;

        public UserPhoto(int id, String url) {
            this.id = id;
            this.url = url;
        }

        // ✅ Getter for id
        public int getId() {
            return id;
        }

        // ✅ Getter for url
        public String getUrl() {
            return url;
        }
    }

}
