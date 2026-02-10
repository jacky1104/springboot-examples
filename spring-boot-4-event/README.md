# Spring Boot 4 Event Demo

This project demonstrates how to use Spring Events in Spring Boot 4 for decoupled, event-driven communication between components.

## Features Demonstrated

### 1. Custom Event Class
- `UserRegisteredEvent` - A custom event representing a user registration
- Events are simple POJOs that carry data

### 2. Event Publishing
- `UserEventPublisher` - Demonstrates how to use `ApplicationEventPublisher` to publish custom events
- Events are published synchronously by default

### 3. Event Listening
- `UserRegisteredListener` - Uses `@EventListener` to listen for `UserRegisteredEvent`
- `AnalyticsListener` - Demonstrates asynchronous event handling with `@Async`

### 4. Async Configuration
- `AsyncConfig` - Enables async processing with `@EnableAsync`
- Async listeners run in separate threads and don't block the main flow

## Project Structure

```
src/main/java/com/example/event/
├── EventApplication.java           # Main application class
├── config/
│   └── AsyncConfig.java            # Async configuration
├── controller/
│   └── UserController.java         # REST API to trigger events
├── event/
│   └── UserRegisteredEvent.java    # Custom event class
├── listener/
│   ├── UserRegisteredListener.java # Synchronous listener
│   └── AnalyticsListener.java      # Asynchronous listener
└── publisher/
    └── UserEventPublisher.java     # Event publisher component
```

## Running the Application

```bash
./gradlew bootRun
```

## Testing the Events

Once the application is running, trigger an event via curl:

```bash
curl -X POST "http://localhost:8080/api/users/register?username=john&email=john@example.com"
```

### Expected Output

```
[Controller] Registering user: john
[Publisher] Publishing event: UserRegisteredEvent{username='john', email='john@example.com', registeredAt=2024-...}
[Controller] Registration completed for: john
[Listener] Received event: UserRegisteredEvent{username='john', email='john@example.com', registeredAt=2024-...}
[Listener] Sending welcome email to john at john@example.com
[Analytics] Tracking registration for: john (Thread: task-1)
[Analytics] Completed tracking for: john
```

Notice that:
1. The controller completes before the analytics listener finishes (async behavior)
2. The analytics runs in a different thread (`task-1`)
3. The welcome email listener runs synchronously

## Key Concepts

### Synchronous Events (Default)
- Event listeners run in the same thread as the publisher
- The publisher waits for all listeners to complete
- Good for operations that must complete before continuing

### Asynchronous Events
- Annotate listener method with `@Async`
- Requires `@EnableAsync` configuration
- Listener runs in a separate thread
- Publisher does not wait for completion
- Good for side effects like analytics, logging, notifications

### Event Listener Methods
- Use `@EventListener` annotation
- Method parameter determines which event type to listen for
- Multiple listeners can receive the same event
- Listeners can be in different components/packages

## References

- [Spring Events Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-functionality-events)
- [Spring Boot Application Events](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.spring-application.application-events-and-listeners)
