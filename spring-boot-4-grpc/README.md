# Spring Boot 4 gRPC (Gradle)

Minimal Spring Boot 4.0.2 gRPC example with a simple Greeter service.

## Run

```bash
./gradlew bootRun
```

## Send a gRPC request

This app starts a gRPC server on `localhost:9090` and exposes a REST endpoint that calls it:

```bash
curl "http://localhost:8084/api/greet?name=Codex"
```

The response comes from the gRPC call and should look like:

```json
{"message":"Hello, Codex!"}
```

## Direct gRPC client snippet

```java
ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
        .usePlaintext()
        .build();
GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
HelloReply reply = stub.sayHello(HelloRequest.newBuilder().setName("Codex").build());
System.out.println(reply.getMessage());
channel.shutdown();
```

## Test

```bash
./gradlew test
```
