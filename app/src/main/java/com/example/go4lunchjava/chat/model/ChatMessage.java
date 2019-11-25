package com.example.go4lunchjava.chat.model;

public class ChatMessage {

    //Rmq: No member prefix for FireBase
    private String senderId;
    private long creationTimeStamp;
    private String content;

    //Empty constructor required by FireStore
    public ChatMessage() {}

    public ChatMessage(String senderId, long creationTime, String content) {
        this.senderId = senderId;
        this.creationTimeStamp = creationTime;
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public long getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public String getContent() {
        return content;
    }
}
