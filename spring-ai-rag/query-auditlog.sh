#!/bin/bash

# Query script to find auditlog server information
API_URL="http://localhost:8080/api/qa/ask"

echo "Querying auditlog server information..."
echo "======================================"
echo ""

# Query for devqa_global environment
echo "1. Auditlog server in devqa_global environment:"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "What is the auditlog server configuration in the devqa_global environment? Please provide the server details, port, and any relevant configuration."
    }' \
    "$API_URL")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"
echo ""

# Query for devqa_europe environment
echo "2. Auditlog server in devqa_europe environment:"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "What is the auditlog server configuration in the devqa_europe environment? Please provide the server details, port, and any relevant configuration."
    }' \
    "$API_URL")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"
echo ""

# General query for auditlog servers across all environments
echo "3. All auditlog server configurations:"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "List all auditlog server configurations across different environments including devqa_global and devqa_europe. Include server names, ports, and any specific settings."
    }' \
    "$API_URL")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"