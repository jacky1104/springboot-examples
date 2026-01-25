package com.example.websocket;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebsocketApplicationTests {

    @LocalServerPort
    private int port;

    @Test
    void echoWebSocketReturnsPrefixedPayload() throws Exception {
        StandardWebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<String> reply = new CompletableFuture<>();

        WebSocketSession session = client.execute(new AbstractWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                session.sendMessage(new TextMessage("hello"));
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                reply.complete(message.getPayload());
            }
        }, new WebSocketHttpHeaders(), URI.create("ws://localhost:" + port + "/ws/echo"))
                .get(5, TimeUnit.SECONDS);

        String payload = reply.get(5, TimeUnit.SECONDS);
        session.close();

        assertThat(payload).isEqualTo("echo:hello");
    }
}
