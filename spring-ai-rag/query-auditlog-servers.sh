#!/bin/bash

# Query script to find auditlog servers in different environments
API_URL="http://localhost:8080/api/qa/ask"

echo "Querying auditlog server information from serverHosts files..."
echo "============================================================="
echo ""

# Query for DevQA Global environment
echo "1. DevQA Global Environment - Auditlog servers:"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "From the DevQA Global serverHosts file, what are the server IP addresses and configurations? Show all server entries with their IPs and roles."
    }' \
    "$API_URL")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"
echo ""

# Query for DevQA Europe environment
echo "2. DevQA Europe Environment - Auditlog servers:"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "From the DevQA Europe serverHosts file, what are the server IP addresses and configurations? Show all server entries with their IPs and roles."
    }' \
    "$API_URL")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"
echo ""

# Query for specific auditlog servers
echo "3. Finding auditlog specific servers:"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "Search all serverHosts files for any servers with \"auditlog\" in their names or roles. List the environment, server names, and IP addresses."
    }' \
    "$API_URL")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"
echo ""

# Query for all environments and their server lists
echo "4. All environments and their server configurations:"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "List all serverHosts files by environment name and show the server IP addresses and roles for each environment. Focus on identifying which servers handle auditlog functionality."
    }' \
    "$API_URL")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"