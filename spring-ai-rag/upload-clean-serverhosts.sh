#!/bin/bash

# Upload cleaned serverHosts files
API_URL="http://localhost:8080/api/documents/upload"
SOURCE_DIR="/home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf"

echo "Uploading cleaned serverHosts files..."
echo "======================================"
echo ""

# Find all serverHosts files
SERVERHOSTS_FILES=$(find "$SOURCE_DIR" -name "serverHosts" -type f | sort)

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
    echo "Processing: $ENV_NAME/serverHosts"

    # Clean the file (remove non-printable characters)
    CLEAN_FILE="/tmp/clean-$ENV_NAME-serverhosts.txt"
    sed 's/[^[:print:]\t\n]//g' "$file" > "$CLEAN_FILE"

    # Upload with environment info
    RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
        -F "file=@$CLEAN_FILE" \
        -F "title=$ENV_NAME-serverHosts" \
        "$API_URL")

    HTTP_STATUS=$(echo "$RESPONSE" | tail -n1)

    if [ "$HTTP_STATUS" -eq 200 ]; then
        echo "  ✓ Success"
        ((SUCCESS_COUNT++))
    else
        echo "  ✗ Failed (HTTP $HTTP_STATUS)"
        echo "    Error: $(echo "$RESPONSE" | head -n-1)"
        ((FAILED_COUNT++))
    fi

    # Clean up temporary file
    rm -f "$CLEAN_FILE"

done

echo ""
echo "Upload Summary:"
echo "  Total files: $(echo "$SERVERHOSTS_FILES" | wc -l)"
echo "  Successful: $SUCCESS_COUNT"
echo "  Failed: $FAILED_COUNT"
echo ""

# Test the improved search
echo "Testing improved search..."
echo ""
echo "Query: auditlog server IP address"
echo "---------------------------------"
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{
        "question": "What is the IP address of the auditlog server? Show the complete configuration including VM details and environment."
    }' \
    "http://localhost:8080/api/qa/ask")

echo "$RESPONSE" | jq -r '.answer' 2>/dev/null || echo "$RESPONSE"