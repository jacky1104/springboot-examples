package com.example.ai.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class McpRequest {

    @NotBlank(message = "Prompt is required")
    private String prompt;

    @JsonProperty("model")
    private String model = "claude-3-5-sonnet-20241022";

    @JsonProperty("max_tokens")
    private Integer maxTokens = 4096;

    @JsonProperty("temperature")
    private Double temperature = 0.7;

    @JsonProperty("system_prompt")
    private String systemPrompt;

    @JsonProperty("context")
    private Map<String, Object> context;

    @JsonProperty("tools")
    private Object[] tools;

    @JsonProperty("stream")
    private Boolean stream = false;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public Object[] getTools() {
        return tools;
    }

    public void setTools(Object[] tools) {
        this.tools = tools;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }
}