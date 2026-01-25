#!/bin/bash

# Multi-file upload script for Spring AI RAG application
# Uploads all supported files from /home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf

API_BASE_URL="http://localhost:8080/api/documents"
SOURCE_DIR="/home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf"

# Supported file extensions
FILE_EXTENSIONS="txt pdf doc docx md yaml yml json xml properties conf config sh"

echo "Starting multi-file upload to Spring AI RAG application..."
echo "API Endpoint: $API_BASE_URL/upload"
echo "Source Directory: $SOURCE_DIR"
echo ""

# Create a temporary file to store all files for upload
TEMP_FILE_LIST=$(mktemp)

# Find all supported files
for ext in $FILE_EXTENSIONS; do
    find "$SOURCE_DIR" -type f -name "*.$ext" >> "$TEMP_FILE_LIST"
done

# Count total files
TOTAL_FILES=$(wc -l < "$TEMP_FILE_LIST")
echo "Found $TOTAL_FILES files to upload"
echo ""

# Counter for successful uploads
SUCCESS_COUNT=0
FAILED_COUNT=0

# Upload each file
while IFS= read -r file; do
    if [ -f "$file" ]; then
        # Get relative path for document name
        RELATIVE_PATH=$(realpath --relative-to="$SOURCE_DIR" "$file")

        echo "Uploading: $RELATIVE_PATH"

        # Upload file using curl
        RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
            -F "file=@$file" \
            -F "title=$RELATIVE_PATH" \
            "$API_BASE_URL/upload")

        # Extract HTTP status code
        HTTP_STATUS=$(echo "$RESPONSE" | tail -n1)

        if [ "$HTTP_STATUS" -eq 200 ]; then
            echo "  ✓ Success"
            ((SUCCESS_COUNT++))
        else
            echo "  ✗ Failed (HTTP $HTTP_STATUS)"
            ((FAILED_COUNT++))
        fi

        # Small delay between uploads
        sleep 0.5
    fi
done < "$TEMP_FILE_LIST"

# Clean up
rm -f "$TEMP_FILE_LIST"

echo ""
echo "Upload Summary:"
echo "  Total files: $TOTAL_FILES"
echo "  Successful: $SUCCESS_COUNT"
echo "  Failed: $FAILED_COUNT"
echo ""

if [ $FAILED_COUNT -eq 0 ]; then
    echo "All files uploaded successfully!"
    exit 0
else
    echo "Some files failed to upload."
    exit 1
fi