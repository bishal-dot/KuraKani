package com.example.kurakani.model;

import java.io.Serializable;

public class Message implements Serializable {

    // Default placeholders
    private static final String DEFAULT_AVATAR_URL = "https://example.com/default_profile.png";
    private static final String DEFAULT_IMAGE_URL  = "https://example.com/default_message.png";
    // 👆 You can replace with a real placeholder image (or leave null if you want Glide to handle it)

    private int id;
    private int sender_id;
    private int receiver_id;
    private String message;
    private String created_at;

    // New fields for image support
    private String imageUrl;          // URL of message image
    private String senderProfileUrl;  // URL of sender profile image

    // --- Getters ---
    public int getId() {
        return id;
    }

    public static String getDefaultImageUrl() {
        return DEFAULT_IMAGE_URL;
    }

    public int getSenderId() {
        return sender_id;
    }

    public int getReceiverId() {
        return receiver_id;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return created_at;
    }

    // ✅ Safe getter for message image URL
    public String getImageUrl() {
        return (imageUrl != null && !imageUrl.isEmpty())
                ? imageUrl
                : DEFAULT_IMAGE_URL;
    }

    // ✅ Safe getter for sender profile image URL
    public String getSenderProfileUrl() {
        return (senderProfileUrl != null && !senderProfileUrl.isEmpty())
                ? senderProfileUrl
                : DEFAULT_AVATAR_URL;
    }

    // --- Setters ---
    public void setId(int id) {
        this.id = id;
    }

    public void setSenderId(int sender_id) {
        this.sender_id = sender_id;
    }

    public void setReceiverId(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setSenderProfileUrl(String senderProfileUrl) {
        this.senderProfileUrl = senderProfileUrl;
    }
}
