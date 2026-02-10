package com.example.event.controller;

import com.example.event.publisher.UserEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller to demonstrate publishing events via HTTP endpoints.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserEventPublisher eventPublisher;

    public UserController(UserEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Endpoint to simulate user registration and publish an event.
     *
     * @param username the username to register
     * @param email the email of the user
     * @return response with registration status
     */
    @PostMapping("/register")
    public Map<String, String> registerUser(
            @RequestParam String username,
            @RequestParam String email) {

        System.out.println("[Controller] Registering user: " + username);

        // Publish the event
        eventPublisher.publishUserRegisteredEvent(username, email);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "User " + username + " registered successfully");

        System.out.println("[Controller] Registration completed for: " + username);

        return response;
    }
}
