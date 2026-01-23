# Spring AI MCP

A Spring Boot application implementing Model Context Protocol (MCP) with Spring AI framework and Claude integration.

## Features

- Model Context Protocol (MCP) server implementation
- Spring AI integration with Anthropic Claude
- RESTful API for chat and streaming
- Claude Code integration support
- Reactive programming with WebFlux
- Health checks and monitoring

## Quick Start

1. Set your Anthropic API key:
```bash
export ANTHROPIC_API_KEY="your-api-key-here"
```

2. Run the application:
```bash
./gradlew bootRun
```

3. Test the API:
```bash
curl -X POST http://localhost:8081/api/mcp/chat \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Hello, Claude!"}'
```

## Documentation

See [claude.md](claude.md) for detailed documentation including:
- API endpoints
- Configuration options
- Claude Code integration
- Development tips
- Troubleshooting

## Project Status

See [plan.md](plan.md) for the project roadmap and progress tracking.