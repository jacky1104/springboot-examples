package com.example.ai.mcp.service;

import com.example.ai.mcp.model.McpRequest;
import com.example.ai.mcp.model.McpResponse;
import com.example.ai.mcp.tool.TimeTools;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class McpService {

    private static final Logger logger = LoggerFactory.getLogger(McpService.class);

    private final AnthropicChatModel chatModel;
    private final ChatClient chatClient;
    private final TimeTools timeTools;
    private final WebClient webClient;
    private final boolean claudeCodeEnabled;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public McpService(
            AnthropicChatModel chatModel,
            ChatClient chatClient,
            TimeTools timeTools,
            WebClient webClient,
            boolean claudeCodeEnabled) {
        this.chatModel = chatModel;
        this.chatClient = chatClient;
        this.timeTools = timeTools;
        this.webClient = webClient;
        this.claudeCodeEnabled = claudeCodeEnabled;
    }

    public Mono<McpResponse> processRequest(McpRequest request) {
        logger.info("Processing MCP request with prompt: {}", request.getPrompt());

        try {
            // Build system message if provided
            SystemMessage systemMessage = null;
            if (request.getSystemPrompt() != null) {
                systemMessage = new SystemMessage(request.getSystemPrompt());
            }

            // Build user message with context
            UserMessage userMessage = new UserMessage(request.getPrompt());

            // Create prompt
            Prompt prompt;
            if (systemMessage != null) {
                prompt = new Prompt(systemMessage, userMessage);
            } else {
                prompt = new Prompt(userMessage);
            }

            // Call the model
            var response = chatModel.call(prompt);

            // Build response
            McpResponse mcpResponse = new McpResponse();
            mcpResponse.setId(UUID.randomUUID().toString());
            mcpResponse.setContent(response.getResult().getOutput().getText());
            mcpResponse.setModel(request.getModel());
            mcpResponse.setMetadata(Map.of(
                    "requestId", mcpResponse.getId(),
                    "claudeCodeEnabled", claudeCodeEnabled
            ));

            logger.info("Successfully processed MCP request with ID: {}", mcpResponse.getId());
            return Mono.just(mcpResponse);

        } catch (Exception e) {
            logger.error("Error processing MCP request", e);
            return Mono.error(new RuntimeException("Failed to process request: " + e.getMessage(), e));
        }
    }

    public Flux<String> streamResponse(McpRequest request) {
        logger.info("Streaming MCP request with prompt: {}", request.getPrompt());

        return Flux.create(sink -> {
            try {
                // Build prompt
                Prompt prompt = new Prompt(request.getPrompt());

                // Stream the response
                chatModel.stream(prompt)
                        .doOnNext(response -> {
                            if (response != null && response.getResult() != null && response.getResult().getOutput() != null) {
                                String content = response.getResult().getOutput().getText();
                                if (content != null && !content.isEmpty()) {
                                    sink.next(content);
                                }
                            }
                        })
                        .doOnComplete(() -> {
                            logger.info("Completed streaming response");
                            sink.complete();
                        })
                        .doOnError(error -> {
                            logger.error("Error streaming response", error);
                            sink.error(error);
                        })
                        .subscribe();

            } catch (Exception e) {
                logger.error("Error setting up stream", e);
                sink.error(e);
            }
        });
    }

    public Mono<McpResponse> processRequestWithTools(McpRequest request) {
        logger.info("Processing MCP tool request with prompt: {}", request.getPrompt());

        try {
            ChatClient.ChatClientRequestSpec spec = chatClient.prompt()
                    .tools(timeTools);

            if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
                spec.system(request.getSystemPrompt());
            }

            String content = spec.user(request.getPrompt())
                    .call()
                    .content();

            McpResponse mcpResponse = new McpResponse();
            mcpResponse.setId(UUID.randomUUID().toString());
            mcpResponse.setContent(content);
            mcpResponse.setModel(request.getModel());
            mcpResponse.setMetadata(Map.of(
                    "requestId", mcpResponse.getId(),
                    "claudeCodeEnabled", claudeCodeEnabled,
                    "tools", "get_current_time,add_numbers"
            ));

            logger.info("Successfully processed MCP tool request with ID: {}", mcpResponse.getId());
            return Mono.just(mcpResponse);
        } catch (Exception e) {
            logger.error("Error processing MCP tool request", e);
            return Mono.error(new RuntimeException("Failed to process tool request: " + e.getMessage(), e));
        }
    }

    public Mono<String> executeClaudeCode(String code) {
        if (!claudeCodeEnabled) {
            return Mono.error(new IllegalStateException("Claude Code is not enabled"));
        }

        logger.info("Executing Claude Code");

        // This would integrate with Claude Code API
        // For now, return a placeholder
        return Mono.just("Claude Code execution would happen here");
    }

    public boolean isClaudeCodeEnabled() {
        return claudeCodeEnabled;
    }
}
