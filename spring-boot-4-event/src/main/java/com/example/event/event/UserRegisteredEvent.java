package com.example.event.event;

import java.time.LocalDateTime;

/**
 * Custom event class representing a user registration event.
 * This event is published when a new user successfully registers.
 */
public class UserRegisteredEvent {

    private final String username;
    private final String email;
    private final LocalDateTime registeredAt;

    public UserRegisteredEvent(String username, String email) {
        this.username = username;
        this.email = email;
        this.registeredAt = LocalDateTime.now();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    @Override
    public String toString() {
        return "UserRegisteredEvent{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }
}
