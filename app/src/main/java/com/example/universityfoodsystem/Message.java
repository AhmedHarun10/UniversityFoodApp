package com.example.universityfoodsystem;

import androidx.annotation.NonNull;

public class Message {
    private String sender;
    private String message;
    private String receiver;
    private String mUrl;

    public Message() {
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public Message(String sender, String message, String receiver, String mUrl) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.mUrl = mUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}