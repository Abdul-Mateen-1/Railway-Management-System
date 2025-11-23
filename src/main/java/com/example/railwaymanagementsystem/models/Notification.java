package com.example.railwaymanagementsystem.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    private final String id;
    private final String userId;
    private final String message;
    private final LocalDateTime timestamp;
    private boolean isRead;

    public Notification(String userId, String message) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
