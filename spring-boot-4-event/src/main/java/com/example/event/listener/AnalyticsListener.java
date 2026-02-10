package com.example.event.listener;

import com.example.event.event.UserRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Demonstrates asynchronous event handling with @Async.
 * This listener runs in a separate thread and does not block the main flow.
 */
@Component
public class AnalyticsListener {

    /**
     * Handles UserRegisteredEvent asynchronously.
     * The @Async annotation makes this method run in a separate thread.
     *
     * @param event the user registration event
     */
    @Async
    @EventListener
    public void trackUserRegistration(UserRegisteredEvent event) {
        System.out.println("[Analytics] Tracking registration for: " + event.getUsername() +
                " (Thread: " + Thread.currentThread().getName() + ")");

        // Simulate some analytics processing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("[Analytics] Completed tracking for: " + event.getUsername());
    }
}
