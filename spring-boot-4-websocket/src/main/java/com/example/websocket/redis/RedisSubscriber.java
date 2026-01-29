package com.example.websocket.redis;

import com.example.websocket.handler.WebSocketSessionRegistry;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisSubscriber implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(RedisSubscriber.class);

    private final WebSocketSessionRegistry sessionRegistry;

    public RedisSubscriber(WebSocketSessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        logger.info("Redis message received: {}", payload);
        sessionRegistry.broadcast(payload);
    }
}
