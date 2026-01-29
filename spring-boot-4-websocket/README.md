# Spring Boot 4 WebSocket (Gradle)

Simple WebSocket echo example using Spring Boot 4.0.2 with Redis pub/sub broadcast.

## Run

```bash
./gradlew bootRun
```

Requires Redis on `localhost:6379`.

Start Redis with docker-compose:

```bash
docker compose up -d
```

## Try it

Use a WebSocket client to send a message to `ws://localhost:8083/ws/echo` and expect `echo:<payload>` to be broadcast to all connected sessions (via Redis pub/sub).

Or open the bundled HTML client at `http://localhost:8083/` and click Connect.

## Data flow

```
Client -> /ws/echo -> EchoWebSocketHandler -> RedisPublisher
Redis (ws:broadcast) -> RedisSubscriber -> WebSocketSessionRegistry -> Clients
```

## Test

```bash
./gradlew test
```
