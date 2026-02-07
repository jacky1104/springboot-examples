# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Spring Cloud Gateway** application that proxies and transforms HTTP requests. It is built with Spring Boot 4.x and uses WebFlux (reactive) stack. The gateway runs on Java 21.

## Build Commands

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "SwaggerApplicationTests"

# Run the application locally
./gradlew bootRun

# Run on a specific port
./gradlew bootRun --args='--server.port=8081'

# Clean build artifacts
./gradlew clean

# Create bootable JAR
./gradlew bootJar
```

## Architecture

### Core Components

**Route Configuration** (`src/main/java/.../config/GatewayConfig.java`):
- Defines routes using Java DSL (RouteLocator bean)
- Main route: `GET /api/v1/public/forticonverter/**` → `POST https://172.16.95.47/api/v2/forticonverter/customerGetAll`
- Transforms GET requests to POST and injects query parameters (`source=gateway`, `version=v1`)

**Global Filters** (`src/main/java/.../filter/`):
- `DownstreamLoggingFilter`: Logs all gateway requests/responses with method, path, route, and downstream URL
- Ordered with `Ordered.LOWEST_PRECEDENCE` to execute after other filters

**HttpClientConfig** (`src/main/java/.../config/HttpClientConfig.java`):
- Configures `InsecureTrustManagerFactory` to bypass SSL certificate validation for downstream HTTPS connections
- Required because backend uses IP addresses without valid SSL certificates

### Key Configuration (application.yaml)

```yaml
spring.cloud.gateway.httpclient:
  ssl.use-insecure-trust-manager: true  # Required for backend SSL
  connect-timeout: 5000
  response-timeout: 30s

management.endpoints.web.exposure.include: gateway,health,info
```

## Testing Endpoints

Use the provided HTTP file (`api-requests.http`) or curl:

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/v1/public/forticonverter/1
```

Check loaded routes via actuator:
```bash
curl http://localhost:8080/actuator/gateway/routes
```

## Important Implementation Notes

- Routes are defined in **Java config** (`GatewayConfig.java`), not YAML, due to need for programmatic filter logic
- Custom filters use `AbstractGatewayFilterFactory` pattern (see `GetToPostTransformGatewayFilterFactory`)
- The gateway uses reactive WebFlux (not servlet/MVC) - all filters return `Mono<Void>`
- SSL verification is disabled for downstream connections via custom `HttpClient` bean
