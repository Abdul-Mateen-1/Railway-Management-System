package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.Notification;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class NotificationService {
    private final BackendRepository repo = BackendRepository.getInstance();

    public void createNotification(String userId, String message) {
        Notification notification = new Notification(userId, message);
        repo.addNotification(notification);
    }

    public ObservableList<Notification> getNotificationsForUser(String userId) {
        // Return a filtered list that only contains notifications for the specified user
        return new FilteredList<>(repo.getNotifications(), n -> n.getUserId().equals(userId));
    }
}
