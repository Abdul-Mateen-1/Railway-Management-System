package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.User;

import java.util.Optional;

/**
 * Holds the currently logged in user across the application.
 */
public final class AppSession {
    private static final AppSession INSTANCE = new AppSession();
    private User currentUser;

    private AppSession() {}

    public static AppSession getInstance() {
        return INSTANCE;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void clear() {
        currentUser = null;
    }
}

