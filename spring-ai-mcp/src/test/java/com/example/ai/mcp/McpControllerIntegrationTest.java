package com.example.ai.mcp;

import com.example.ai.mcp.model.McpRequest;
import com.example.ai.mcp.model.McpResponse;
import com.example.ai.mcp.controller.McpController;
import com.example.ai.mcp.service.McpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = McpController.class)
class McpControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private McpService mcpService;

    @Test
    void testChatEndpoint_Success() {
        // Given
        McpRequest request = new McpRequest();
        request.setPrompt("Test prompt");
        request.setModel("claude-3-sonnet");

        McpResponse response = new McpResponse();
        response.setId("test-id");
        response.setContent("Test response");
        response.setModel("claude-3-sonnet");

        when(mcpService.processRequest(any(McpRequest.class))).thenReturn(Mono.just(response));

        // When & Then
        webTestClient.post()
                .uri("/api/mcp/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(McpResponse.class)
                .consumeWith(result -> {
                    McpResponse body = result.getResponseBody();
                    assert body != null;
                    assert body.getId().equals("test-id");
                    assert body.getContent().equals("Test response");
                    assert body.getModel().equals("claude-3-sonnet");
                });
    }

    @Test
    void testChatEndpoint_ValidationError() {
        // Given - Empty request body
        McpRequest request = new McpRequest();

        // When & Then
        webTestClient.post()
                .uri("/api/mcp/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testStreamEndpoint_Success() {
        // Given
        McpRequest request = new McpRequest();
        request.setPrompt("Stream test");

        when(mcpService.streamResponse(any(McpRequest.class)))
                .thenReturn(Flux.just("Part 1 ", "Part 2 ", "Part 3"));

        // When & Then
        webTestClient.post()
                .uri("/api/mcp/stream")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    assert body != null;
                    // SSE format includes "data:" prefix and newlines
                    assert body.contains("data:Part 1");
                    assert body.contains("data:Part 2");
                    assert body.contains("data:Part 3");
                });
    }

    @Test
    void testClaudeCodeEndpoint_Success() {
        // Given
        String code = "print('Hello World')";
        when(mcpService.executeClaudeCode(code)).thenReturn(Mono.just("Code executed successfully"));

        // When & Then
        webTestClient.post()
                .uri("/api/mcp/claude-code")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(java.util.Map.of("code", code))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Code executed successfully");
    }

    @Test
    void testClaudeCodeEndpoint_MissingCode() {
        // When & Then
        webTestClient.post()
                .uri("/api/mcp/claude-code")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(java.util.Map.of())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Code is required");
    }

    @Test
    void testHealthEndpoint() {
        // Given
        when(mcpService.isClaudeCodeEnabled()).thenReturn(true);

        // When & Then
        webTestClient.get()
                .uri("/api/mcp/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.claudeCodeEnabled").isEqualTo(true)
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void testInfoEndpoint() {
        // Given
        when(mcpService.isClaudeCodeEnabled()).thenReturn(true);

        // When & Then
        webTestClient.get()
                .uri("/api/mcp/info")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Spring AI MCP")
                .jsonPath("$.version").isEqualTo("0.0.1-SNAPSHOT")
                .jsonPath("$.features.chat").isEqualTo(true)
                .jsonPath("$.features.streaming").isEqualTo(true)
                .jsonPath("$.features.claudeCode").isEqualTo(true);
    }
}