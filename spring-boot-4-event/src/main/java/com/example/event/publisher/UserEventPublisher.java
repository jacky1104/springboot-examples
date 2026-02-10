package com.example.event.publisher;

import com.example.event.event.UserRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Component responsible for publishing application events.
 * Demonstrates how to use ApplicationEventPublisher to publish custom events.
 */
@Component
public class UserEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public UserEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes a UserRegisteredEvent when a user successfully registers.
     *
     * @param username the username of the registered user
     * @param email the email of the registered user
     */
    public void publishUserRegisteredEvent(String username, String email) {
        UserRegisteredEvent event = new UserRegisteredEvent(username, email);
        System.out.println("[Publisher] Publishing event: " + event);
        eventPublisher.publishEvent(event);
    }
}
