package com.example.kurakani.model;

import java.io.Serializable;

public class Message implements Serializable {

    private int id;
    private int sender_id;
    private int receiver_id;
    private String message;
    private String created_at;

    // Getter for message ID
    public int getId() {
        return id;
    }

    // Getter for sender ID (renamed to standard getter)
    public int getSenderId() {
        return sender_id;
    }

    // Getter for receiver ID (renamed to standard getter)
    public int getReceiverId() {
        return receiver_id;
    }

    // Getter for message text
    public String getMessage() {
        return message;
    }

    // Getter for created_at timestamp (renamed to standard getter)
    public String getCreatedAt() {
        return created_at;
    }

    // Optional setters (if needed)
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
}
