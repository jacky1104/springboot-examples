#!/bin/bash

# Re-upload serverHosts files with improved chunking
API_URL="http://localhost:8080/api/documents"

echo "Re-uploading serverHosts files with improved chunking..."
echo "========================================================="
echo ""

# First, let's delete existing serverHosts documents
echo "1. Finding existing serverHosts documents..."
SERVERHOSTS_IDS=$(curl -s "$API_URL" | jq -r '.[] | select(.fileName == "serverHosts") | .id' 2>/dev/null)

if [ -n "$SERVERHOSTS_IDS" ]; then
    COUNT=$(echo "$SERVERHOSTS_IDS" | wc -l)
    echo "   Found $COUNT existing serverHosts documents"
    echo ""
else
    echo "   No existing serverHosts documents found"
fi

# Now upload the serverHosts files from the deployconf folder
echo "2. Uploading serverHosts files from /home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf..."
echo ""

# Find all serverHosts files
SERVERHOSTS_FILES=$(find /home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf -name "serverHosts" -type f | sort)

if [ -z "$SERVERHOSTS_FILES" ]; then
    echo "No serverHosts files found!"
    exit 1
fi

echo "Found $(echo "$SERVERHOSTS_FILES" | wc -l) serverHosts files"
echo ""

# Upload each file
SUCCESS_COUNT=0
FAILED_COUNT=0

for file in $SERVERHOSTS_FILES; do
    # Get environment name from directory
    ENV_NAME=$(basename $(dirname "$file"))
    echo "Uploading: $ENV_NAME/serverHosts"

    # Upload with environment info
    RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
        -F "file=@$file" \
        -F "title=$ENV_NAME-serverHosts" \
        "$API_URL/upload")

    HTTP_STATUS=$(echo "$RESPONSE" | tail -n1)

    if [ "$HTTP_STATUS" -eq 200 ]; then
        echo "  ✓ Success"
        ((SUCCESS_COUNT++))
    else
        echo "  ✗ Failed (HTTP $HTTP_STATUS)"
        ((FAILED_COUNT++))
    fi

done

echo ""
echo "Upload Summary:"
echo "  Total files: $(echo "$SERVERHOSTS_FILES" | wc -l)"
echo "  Successful: $SUCCESS_COUNT"
echo "  Failed: $FAILED_COUNT"
echo ""

# Test the improved search
echo "3. Testing improved search..."
echo ""
echo "Query: auditlog server in devqagl environment"
echo "----------------------------------------------"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "What is the IP address of the auditlog server in the devqagl environment? Show the complete configuration including VM details."
    }' \
    "http://localhost:8080/api/qa/ask")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"