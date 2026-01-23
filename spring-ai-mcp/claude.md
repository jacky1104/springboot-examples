# Spring AI MCP - Claude Code Integration Guide

## Overview

This project implements a Model Context Protocol (MCP) server using Spring Boot and Spring AI framework, integrated with **internally deployed Claude AI** at Fortinet for intelligent code assistance and generation.

> **Note**: This project is configured to use the internal Claude deployment at `https://aicoding.fortinet-us.com:7001/yvr`

This project implements a Model Context Protocol (MCP) server using Spring Boot and Spring AI framework, integrated with Claude AI for intelligent code assistance and generation.

## Features

- ✅ Model Context Protocol (MCP) server implementation
- ✅ Spring AI integration with Anthropic Claude
- ✅ RESTful API for chat and streaming
- ✅ Claude Code integration support
- ✅ WebFlux for reactive programming
- ✅ Health checks and monitoring

## Quick Start

### Prerequisites

- Java 21 or higher
- Gradle 8.5+
- Anthropic API key

### Environment Setup

1. Set your Anthropic authentication token:
```bash
export ANTHROPIC_AUTH_TOKEN="your-auth-token-here"
```

2. Run the application:
```bash
./gradlew bootRun
```

The server will start on `http://localhost:8081`

## API Endpoints

### Chat Endpoint
```bash
POST /api/mcp/chat
Content-Type: application/json

{
  "prompt": "Your question here",
  "model": "forti-coder",
  "max_tokens": 4096,
  "temperature": 0.7
}
```

### Chat With Tools Endpoint
Use this endpoint to allow the model to call server-side `@Tool` methods.

Example (time tool):
```bash
POST /api/mcp/chat-tools
Content-Type: application/json

{
  "prompt": "What time is it? Use the get_current_time tool.",
  "system_prompt": "Use tools when helpful."
}
```

Example (math tool):
```bash
POST /api/mcp/chat-tools
Content-Type: application/json

{
  "prompt": "Add 12 and 30 using add_numbers.",
  "system_prompt": "Use tools when helpful."
}
```

### Streaming Endpoint
```bash
POST /api/mcp/stream
Content-Type: application/json

{
  "prompt": "Your question here",
  "stream": true
}
```

### Health Check
```bash
GET /api/mcp/health
```

### Info Endpoint
```bash
GET /api/mcp/info
```

## Claude Code Integration

### Enabling Claude Code

Set the following environment variables:
```bash
export CLAUDE_CODE_ENABLED=true
export CLAUDE_CODE_AUTO_SYNC=true
export CLAUDE_CODE_WORKSPACE=./workspace
```

### Using Claude Code

Send code to be executed:
```bash
POST /api/mcp/claude-code
Content-Type: application/json

{
  "code": "your code here"
}
```

## Development Tips

### 1. Using with Claude Code CLI

The application can be integrated with Claude Code CLI for enhanced development experience:

```bash
# Run with Claude Code profile
./gradlew bootRun --args='--spring.profiles.active=claude'
```

### 2. Debugging

Enable debug logging:
```bash
export LOGGING_LEVEL_COM_EXAMPLE_AI_MCP=DEBUG
```

### 3. Testing

Run tests:
```bash
./gradlew test
```

### 4. Building

Build the project:
```bash
./gradlew build
```

## Configuration

### Application Properties

Key configurations in `application.yml`:

- `spring.ai.anthropic.api-key`: Your Anthropic authentication token (ANTHROPIC_AUTH_TOKEN)
- `spring.ai.anthropic.base-url`: Internal Claude deployment URL (https://aicoding.fortinet-us.com:7001/yvr)
- `claude.code.enabled`: Enable/disable Claude Code integration
- `server.port`: Server port (default: 8081)
- `mcp.server.*`: MCP server configuration

### Profiles

- `dev`: Development profile with debug logging
- `prod`: Production profile with minimal logging
- `claude`: Profile for Claude Code integration

## Architecture

### Components

1. **McpController**: REST API endpoints
2. **McpService**: Business logic for AI interactions
3. **McpConfig**: Spring configuration and beans
4. **Model Classes**: Request/response DTOs

### Flow

1. Client sends request to `/api/mcp/chat`
2. McpController validates and forwards to McpService
3. McpService processes with AnthropicChatModel
4. Response is formatted and returned

## Best Practices

1. **Error Handling**: All endpoints return appropriate HTTP status codes
2. **Validation**: Request validation using Bean Validation
3. **Logging**: Structured logging with SLF4J
4. **Reactive**: Using WebFlux for non-blocking operations
5. **Monitoring**: Actuator endpoints for health checks

## Troubleshooting

### Common Issues

1. **API Key Error**: Ensure `ANTHROPIC_API_KEY` is set
2. **Port Conflict**: Change `server.port` in application.yml
3. **Memory Issues**: Increase JVM heap size in gradle.properties

### Debug Commands

```bash
# Check application health
curl http://localhost:8081/api/mcp/health

# Get application info
curl http://localhost:8081/api/mcp/info

# Test chat endpoint
curl -X POST http://localhost:8081/api/mcp/chat \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Hello, Claude!"}'
```

## Future Enhancements

- [ ] Tool calling support
- [ ] Context persistence
- [ ] Multi-model support
- [ ] Rate limiting
- [ ] Authentication
- [ ] WebSocket support
- [ ] MCP client implementation

## Resources

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Anthropic Claude API](https://docs.anthropic.com/en/api/getting-started)
- [Model Context Protocol](https://modelcontextprotocol.io/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
