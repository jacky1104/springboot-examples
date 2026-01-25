#!/bin/bash

# Test script for querying serverHosts files with improved chunking
API_URL="http://localhost:8080/api/qa/ask"

echo "Testing improved serverHosts query with better chunking..."
echo "========================================================="
echo ""

# Query 1: Find auditlog servers
echo "1. Finding auditlog servers across all environments:"
echo "----------------------------------------------------"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "What is the IP address of the auditlog server? Show me the complete auditlog section with all configuration details including VM host and name."
    }' \
    "$API_URL")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"
echo ""

# Query 2: Find servers by environment
echo "2. Finding all servers in DevQA environments:"
echo "---------------------------------------------"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "List all servers in the DevQA environment with their IP addresses and roles. Include the environment name for each server."
    }' \
    "$API_URL")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"
echo ""

# Query 3: Find specific server roles
echo "3. Finding controller servers:"
echo "------------------------------"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "What are the IP addresses of all controller servers across all environments? Include their VM host and VM name details."
    }' \
    "$API_URL")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"
echo ""

# Query 4: Search by IP range
echo "4. Finding servers in 172.16.97.x range:"
echo "----------------------------------------"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "List all servers with IP addresses in the 172.16.97.x range. Show their roles, VM hosts, and which environment they belong to."
    }' \
    "$API_URL")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"
echo ""

# Query 5: Find database servers
echo "5. Finding all database servers:"
echo "--------------------------------"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "Find all database servers (any role ending with DB) across all environments. Show their IP addresses and environment names."
    }' \
    "$API_URL")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"