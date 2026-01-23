package com.example.ai.mcp.config;

import com.example.ai.mcp.service.McpService;
import com.example.ai.mcp.tool.TimeTools;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class McpConfig {

    @Value("${claude.code.enabled:false}")
    private boolean claudeCodeEnabled;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
    }

    @Bean
    public ChatClient chatClient(AnthropicChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    public McpService mcpService(
            AnthropicChatModel chatModel,
            ChatClient chatClient,
            TimeTools timeTools,
            WebClient webClient) {
        return new McpService(chatModel, chatClient, timeTools, webClient, claudeCodeEnabled);
    }
}
