#!/bin/bash

# Upload script for serverHosts files
API_URL="http://localhost:8080/api/documents/upload"
SOURCE_DIR="/home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf"

echo "Uploading serverHosts files..."
echo "================================"

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
    # Get relative path from deployconf directory
    RELATIVE_PATH=$(realpath --relative-to="$SOURCE_DIR" "$file")
    ENV_NAME=$(dirname "$RELATIVE_PATH")

    echo "Uploading: $RELATIVE_PATH (Environment: $ENV_NAME)"

    # Upload with environment info in metadata
    RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
        -F "file=@$file" \
        -F "title=serverHosts-$ENV_NAME" \
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

    # Small delay between uploads
    sleep 0.2
done

echo ""
echo "Upload Summary:"
echo "  Total files: $(echo "$SERVERHOSTS_FILES" | wc -l)"
echo "  Successful: $SUCCESS_COUNT"
echo "  Failed: $FAILED_COUNT"

if [ $FAILED_COUNT -eq 0 ]; then
    echo "All serverHosts files uploaded successfully!"
    exit 0
else
    echo "$FAILED_COUNT files failed to upload."
    exit 1
fi