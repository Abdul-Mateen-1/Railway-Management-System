package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.User;

import java.util.Optional;

public class UserService {
    private final BackendRepository repo = BackendRepository.getInstance();

    public Optional<User> authenticate(String email, String password, String role) {
        return repo.findUserByEmail(email)
                .filter(user -> user.getPassword().equals(password))
                .filter(user -> user.getRole().equalsIgnoreCase(role));
    }

    public Optional<User> register(User user) {
        if (repo.findUserByEmail(user.getEmail()).isPresent()) {
            return Optional.empty();
        }
        User copy = new User(repo.nextUserId(), user.getName(), user.getEmail(), user.getPhone(),
                user.getRole(), user.getPassword());
        return Optional.of(repo.addUser(copy));
    }

    public boolean emailExists(String email) {
        return repo.findUserByEmail(email).isPresent();
    }

    public Optional<User> getUserById(String userId) {
        return repo.findUserById(userId);
    }

    public Optional<User> updateUser(User updatedUser) {
        Optional<User> existing = repo.findUserById(updatedUser.getId());
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        if (repo.emailExists(updatedUser.getEmail(), updatedUser.getId())) {
            return Optional.empty();
        }
        if (repo.updateUser(updatedUser)) {
            return Optional.of(updatedUser);
        }
        return Optional.empty();
    }
}
