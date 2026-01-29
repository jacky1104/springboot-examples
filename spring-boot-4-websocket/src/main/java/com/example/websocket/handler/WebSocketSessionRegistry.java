package com.example.websocket.handler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WebSocketSessionRegistry {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketSessionRegistry.class);

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    public void add(WebSocketSession session) {
        sessions.add(session);
    }

    public void remove(WebSocketSession session) {
        sessions.remove(session);
    }

    public void broadcast(String payload) {
        TextMessage message = new TextMessage(payload);
        logger.info("Broadcasting websocket message to {} sessions: {}", sessions.size(), payload);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException ignored) {
                    // Best-effort broadcast; failed sessions can retry on next message.
                }
            }
        }
    }
}
