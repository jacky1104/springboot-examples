package com.example.ai.mcp.controller;

import com.example.ai.mcp.model.McpRequest;
import com.example.ai.mcp.model.McpResponse;
import com.example.ai.mcp.service.McpService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import java.util.Map;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/mcp")
@CrossOrigin(origins = "*")
public class McpController {

    private static final Logger logger = LoggerFactory.getLogger(McpController.class);

    private final McpService mcpService;

    @Autowired
    public McpController(McpService mcpService) {
        this.mcpService = mcpService;
    }

    @PostMapping("/chat")
    public Mono<ResponseEntity<McpResponse>> chat(@Valid @RequestBody McpRequest request) {
        logger.info("Received chat request");
        return mcpService.processRequest(request)
                .map(response -> ResponseEntity.ok(response))
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    @PostMapping("/chat-tools")
    public Mono<ResponseEntity<McpResponse>> chatWithTools(@Valid @RequestBody McpRequest request) {
        logger.info("Received chat request with tools");
        return mcpService.processRequestWithTools(request)
                .map(response -> ResponseEntity.ok(response))
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@Valid @RequestBody McpRequest request) {
        logger.info("Received stream request");
        return mcpService.streamResponse(request)
                .onErrorResume(error -> {
                    logger.error("Error in stream", error);
                    return Flux.just("Error: " + error.getMessage());
                });
    }

    @PostMapping("/claude-code")
    public Mono<ResponseEntity<String>> executeClaudeCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        if (code == null || code.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("Code is required"));
        }

        logger.info("Received Claude Code execution request");
        return mcpService.executeClaudeCode(code)
                .map(result -> ResponseEntity.ok(result))
                .onErrorReturn(ResponseEntity.internalServerError().body("Failed to execute code"));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "claudeCodeEnabled", mcpService.isClaudeCodeEnabled(),
                "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = Map.of(
                "name", "Spring AI MCP",
                "version", "0.0.1-SNAPSHOT",
                "description", "Spring Boot application for Model Context Protocol with Claude AI",
                "features", Map.of(
                        "chat", true,
                        "streaming", true,
                        "claudeCode", mcpService.isClaudeCodeEnabled()
                )
        );
        return ResponseEntity.ok(info);
    }
}
