package com.example.websocket.handler;

import com.example.websocket.redis.RedisPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class EchoWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionRegistry sessionRegistry;
    private final RedisPublisher redisPublisher;

    public EchoWebSocketHandler(WebSocketSessionRegistry sessionRegistry, RedisPublisher redisPublisher) {
        this.sessionRegistry = sessionRegistry;
        this.redisPublisher = redisPublisher;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessionRegistry.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionRegistry.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = "echo:" + message.getPayload();
        redisPublisher.publish(payload);
    }
}
