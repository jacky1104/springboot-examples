package com.example.event.listener;

import com.example.event.event.UserRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener that handles UserRegisteredEvent.
 * Demonstrates the @EventListener annotation for listening to application events.
 */
@Component
public class UserRegisteredListener {

    /**
     * Handles UserRegisteredEvent by sending a welcome email notification.
     * This method is invoked automatically when a UserRegisteredEvent is published.
     *
     * @param event the user registration event
     */
    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        System.out.println("[Listener] Received event: " + event);
        sendWelcomeEmail(event.getUsername(), event.getEmail());
    }

    private void sendWelcomeEmail(String username, String email) {
        // Simulate sending an email
        System.out.println("[Listener] Sending welcome email to " + username + " at " + email);
    }
}
