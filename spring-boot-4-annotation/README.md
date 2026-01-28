# Spring Boot 3.4.2 Custom Annotation Example

This project demonstrates how to create and use a custom annotation in Spring Boot 3.4.2 with Gradle 9.2.1. The `@Traceable` annotation is applied in both controller and service layers and is intercepted by a Spring AOP aspect.

## Key Features

- Spring Boot 3.4.2
- Gradle 9.2.1
- Custom annotation: `@Traceable`
- Aspect-oriented logging around annotated methods
- Example REST endpoint

## Project Structure

- `com.example.annotation.Traceable` - custom annotation
- `com.example.annotation.aop.TraceableAspect` - aspect handling the annotation
- `com.example.annotation.controller.GreetingController` - controller layer usage
- `com.example.annotation.service.GreetingService` - service layer usage

## How It Works

1. The `@Traceable` annotation marks methods (or classes) to be intercepted.
2. `TraceableAspect` uses `@Around` advice to log start/end and execution time.
3. The controller and service methods are annotated to show the annotation works across layers.

## Running the Application

```bash
./gradlew bootRun
```

## Example Usage

```bash
curl "http://localhost:8080/api/greet?name=Codex"
```

You will see logs like:

```
[trace] start: controller.greet
[trace] start: service.greet
[trace] end: service.greet (2 ms)
[trace] end: controller.greet (4 ms)
```

## Notes

- The `@Traceable` annotation supports optional labels via `value`.
- The aspect also works at class level using `@Traceable` on a class.
