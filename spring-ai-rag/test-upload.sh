#!/bin/bash

# Test upload endpoint with a simple file
echo "Testing upload endpoint..."

# Create a test file
echo "This is a test document for Spring AI RAG application." > /tmp/test-upload.txt

# Upload the test file
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
    -F "file=@/tmp/test-upload.txt" \
    -F "title=test-document" \
    http://localhost:8080/api/documents/upload)

# Extract HTTP status code
HTTP_STATUS=$(echo "$RESPONSE" | tail -n1)

# Clean up
rm -f /tmp/test-upload.txt

if [ "$HTTP_STATUS" -eq 200 ]; then
    echo "✓ Upload endpoint is working!"
    echo "Response: $(echo "$RESPONSE" | head -n-1)"
    exit 0
else
    echo "✗ Upload endpoint test failed (HTTP $HTTP_STATUS)"
    echo "Response: $(echo "$RESPONSE" | head -n-1)"
    exit 1
fi