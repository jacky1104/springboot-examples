package com.example.ai.mcp;

import com.example.ai.mcp.model.McpRequest;
import com.example.ai.mcp.model.McpResponse;
import com.example.ai.mcp.service.McpService;
import com.example.ai.mcp.tool.TimeTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class McpServiceTest {

    @Mock
    private AnthropicChatModel chatModel;

    @Mock
    private WebClient webClient;

    @Mock
    private ChatClient chatClient;

    @Mock
    private TimeTools timeTools;

    private McpService mcpService;

    @BeforeEach
    void setUp() {
        mcpService = new McpService(chatModel, chatClient, timeTools, webClient, true);
    }

    @Test
    void processRequest_Success() {
        // Given
        McpRequest request = new McpRequest();
        request.setPrompt("Test prompt");
        request.setModel("claude-3-sonnet");

        ChatResponse mockResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        org.springframework.ai.chat.messages.AssistantMessage assistantMessage =
            mock(org.springframework.ai.chat.messages.AssistantMessage.class);

        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);
        when(mockResponse.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(assistantMessage);
        when(assistantMessage.getText()).thenReturn("Test response");

        // When
        var resultMono = mcpService.processRequest(request);

        // Then
        McpResponse response = resultMono.block();
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Test response", response.getContent());
        assertEquals("claude-3-sonnet", response.getModel());
        assertNotNull(response.getMetadata());
        assertEquals(true, response.getMetadata().get("claudeCodeEnabled"));

        verify(chatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    void processRequest_WithSystemPrompt() {
        // Given
        McpRequest request = new McpRequest();
        request.setPrompt("Test prompt");
        request.setSystemPrompt("You are a helpful assistant");
        request.setModel("claude-3-sonnet");

        ChatResponse mockResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        org.springframework.ai.chat.messages.AssistantMessage assistantMessage =
            mock(org.springframework.ai.chat.messages.AssistantMessage.class);

        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);
        when(mockResponse.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(assistantMessage);
        when(assistantMessage.getText()).thenReturn("Test response with system prompt");

        // When
        var resultMono = mcpService.processRequest(request);

        // Then
        McpResponse response = resultMono.block();
        assertNotNull(response);
        assertEquals("Test response with system prompt", response.getContent());
    }

    @Test
    void processRequest_Error() {
        // Given
        McpRequest request = new McpRequest();
        request.setPrompt("Test prompt");
        request.setModel("claude-3-sonnet");

        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("Model error"));

        // When
        var resultMono = mcpService.processRequest(request);

        // Then
        assertThrows(RuntimeException.class, () -> resultMono.block());

        verify(chatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    void processRequestWithTools_Success() {
        // Given
        McpRequest request = new McpRequest();
        request.setPrompt("What time is it?");
        request.setSystemPrompt("Use tools when helpful.");
        request.setModel("claude-3-sonnet");

        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec responseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.tools(any())).thenReturn(requestSpec);
        when(requestSpec.system(any(String.class))).thenReturn(requestSpec);
        when(requestSpec.user(any(String.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("The current time is 2026-01-23T18:44:23Z");

        // When
        var resultMono = mcpService.processRequestWithTools(request);

        // Then
        McpResponse response = resultMono.block();
        assertNotNull(response);
        assertEquals("The current time is 2026-01-23T18:44:23Z", response.getContent());
        assertEquals("claude-3-sonnet", response.getModel());
    }

    @Test
    void streamResponse_Success() {
        // Given
        McpRequest request = new McpRequest();
        request.setPrompt("Stream test prompt");

        Flux<ChatResponse> mockFlux = Flux.just(
            createChatResponse("Part 1 "),
            createChatResponse("Part 2 "),
            createChatResponse("Part 3")
        );

        when(chatModel.stream(any(Prompt.class))).thenReturn(mockFlux);

        // When
        var resultFlux = mcpService.streamResponse(request);

        // Then
        String result = resultFlux.collectList().block().stream().reduce("", String::concat);
        assertEquals("Part 1 Part 2 Part 3", result);

        verify(chatModel, times(1)).stream(any(Prompt.class));
    }

    @Test
    void streamResponse_Error() {
        // Given
        McpRequest request = new McpRequest();
        request.setPrompt("Stream test prompt");

        when(chatModel.stream(any(Prompt.class))).thenReturn(Flux.error(new RuntimeException("Stream error")));

        // When
        var resultFlux = mcpService.streamResponse(request);

        // Then
        assertThrows(RuntimeException.class, () -> resultFlux.blockLast());

        verify(chatModel, times(1)).stream(any(Prompt.class));
    }

    @Test
    void executeClaudeCode_Enabled() {
        // When
        var result = mcpService.executeClaudeCode("test code");

        // Then
        assertEquals("Claude Code execution would happen here", result.block());
    }

    @Test
    void executeClaudeCode_Disabled() {
        // Given
        mcpService = new McpService(chatModel, chatClient, timeTools, webClient, false);

        // When
        var result = mcpService.executeClaudeCode("test code");

        // Then
        assertThrows(IllegalStateException.class, () -> result.block());
    }

    @Test
    void isClaudeCodeEnabled() {
        // When & Then
        assertTrue(mcpService.isClaudeCodeEnabled());

        // Given
        mcpService = new McpService(chatModel, chatClient, timeTools, webClient, false);

        // When & Then
        assertFalse(mcpService.isClaudeCodeEnabled());
    }

    private ChatResponse createChatResponse(String text) {
        ChatResponse response = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        org.springframework.ai.chat.messages.AssistantMessage assistantMessage =
            mock(org.springframework.ai.chat.messages.AssistantMessage.class);

        when(response.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(assistantMessage);
        when(assistantMessage.getText()).thenReturn(text);

        return response;
    }
}
