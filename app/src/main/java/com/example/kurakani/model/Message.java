package com.example.kurakani.model;

public class Message {
    private int id;
    private int sender_id;
    private int receiver_id;
    private String message;
    private String created_at;

    public int getId() { return id; }
    public int getSender_id() { return sender_id; }
    public int getReceiver_id() { return receiver_id; }
    public String getMessage() { return message; }
    public String getCreated_at() { return created_at; }
}
