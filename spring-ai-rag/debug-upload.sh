#!/bin/bash

# Debug upload script
API_URL="http://localhost:8080/api/documents/upload"

echo "Debug upload test..."
echo "==================="

# Create a simple test file
echo "[test]" > /tmp/simple-test.txt
echo "1.1.1.1 ansible_user=test" >> /tmp/simple-test.txt

echo "Uploading simple test file..."
curl -v \
  -F "file=@/tmp/simple-test.txt" \
  -F "title=simple-test" \
  "$API_URL" 2>&1 | grep -E "(HTTP|Error|Exception)" | head -10

# Now try with a real serverHosts file but smaller
echo ""
echo "Uploading first 10 lines of serverHosts..."
head -10 /home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf/dev1/serverHosts > /tmp/partial-serverhosts.txt

curl -v \
  -F "file=@/tmp/partial-serverhosts.txt" \
  -F "title=partial-serverhosts" \
  "$API_URL" 2>&1 | grep -E "(HTTP|Error|Exception)" | head -10

# Clean up
rm -f /tmp/simple-test.txt /tmp/partial-serverhosts.txt