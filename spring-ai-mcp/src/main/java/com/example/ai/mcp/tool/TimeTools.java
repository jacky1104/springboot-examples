package com.example.ai.mcp.tool;

import java.time.Instant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class TimeTools {

    @Tool(name = "get_current_time", description = "Get the current server time in ISO-8601 UTC.")
    public String getCurrentTime() {
        return Instant.now().toString();
    }

    @Tool(name = "add_numbers", description = "Add two integers and return the sum.")
    public int addNumbers(
            @ToolParam(description = "First integer") int a,
            @ToolParam(description = "Second integer") int b) {
        System.out.println("Adding " + a + " and " + b);
        return a + b;
    }

    @Tool(name = "minus_numbers", description = "Subtract two integers and return the difference.")
    public int minusNumbers(
            @ToolParam(description = "First integer") int a,
            @ToolParam(description = "Second integer") int b) {
        System.out.println("Subtracting " + b + " from " + a);
        return a - b;
    }
}
