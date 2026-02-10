package com.example.event.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class to enable asynchronous processing.
 * Required for @Async annotation to work on event listeners.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // No additional configuration needed for basic async support
}
