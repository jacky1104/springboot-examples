# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 4.0.2 application demonstrating gRPC integration with a hybrid REST/gRPC architecture. The application exposes both a gRPC server (port 9090) and a REST API (port 8084) that acts as a client to the gRPC service.

## Build and Development Commands

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.example.grpc.GrpcApplicationTests"

# Clean build artifacts
./gradlew clean

# Generate protobuf classes (automatically done during build)
./gradlew generateProto
```

## Architecture

The application follows a client-server architecture with both REST and gRPC endpoints:

### Request Flow
1. REST requests hit `GrpcController` at `/api/greet`
2. Controller delegates to `GrpcClient` which makes gRPC calls
3. gRPC server (`GrpcServerLifecycle`) receives calls on port 9090
4. `GreeterService` implements the business logic
5. Responses flow back through the same path

### Key Components

- **GrpcApplication**: Main Spring Boot application class
- **GrpcController**: REST endpoint that acts as a gRPC client
- **GrpcClient**: gRPC client wrapper that manages the channel and stubs
- **GreeterService**: gRPC service implementation
- **GrpcServerLifecycle**: Manages gRPC server lifecycle (starts on port 9090)
- **GrpcProperties**: Configuration for gRPC host/port

### Generated Code

Protobuf definitions in `src/main/proto/greeter.proto` generate:
- Message classes: `HelloRequest`, `HelloReply`
- Service interfaces: `GreeterGrpc` with stubs for client/server
- Generated code location: `build/generated/source/proto/main/`

## Configuration

- Application port: 8084 (REST API)
- gRPC server port: 9090
- Configuration file: `src/main/resources/application.yml`

## Testing

The project includes basic integration tests in `src/test/java`. When adding new features:
- Add unit tests for service logic
- Add integration tests for REST endpoints
- Test gRPC services using the generated stubs

## Common Issues and Solutions

1. **Build failures with javax.annotation.Generated**: The build.gradle includes `org.apache.tomcat:annotations-api` to resolve this Java 9+ compatibility issue.

2. **Port conflicts**: Ensure ports 8084 and 9090 are available before running the application.

3. **Protobuf compilation**: If proto changes aren't reflected, run `./gradlew clean generateProto` to regenerate classes.