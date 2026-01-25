#!/usr/bin/env python3
"""
Multi-file uploader for Spring AI RAG application
Uploads all supported files from /home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf
"""

import os
import requests
import mimetypes
from pathlib import Path
from concurrent.futures import ThreadPoolExecutor, as_completed
import time

# Configuration
API_BASE_URL = "http://localhost:8080/api/documents"
SOURCE_DIR = "/home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf"
MAX_WORKERS = 5  # Number of concurrent uploads

# Supported file extensions
SUPPORTED_EXTENSIONS = {
    '.txt', '.pdf', '.doc', '.docx', '.md',
    '.yaml', '.yml', '.json', '.xml', '.properties',
    '.conf', '.config', '.sh'
}

def get_files_to_upload():
    """Find all supported files in the source directory"""
    files = []
    source_path = Path(SOURCE_DIR)

    for file_path in source_path.rglob('*'):
        if file_path.is_file() and file_path.suffix.lower() in SUPPORTED_EXTENSIONS:
            relative_path = file_path.relative_to(source_path)
            files.append((file_path, relative_path))

    return files

def upload_file(file_path, relative_path):
    """Upload a single file to the API"""
    try:
        # Guess MIME type
        mime_type, _ = mimetypes.guess_type(str(file_path))
        if not mime_type:
            mime_type = 'application/octet-stream'

        # Prepare the file
        with open(file_path, 'rb') as f:
            files = {'file': (relative_path.name, f, mime_type)}
            data = {'title': str(relative_path)}

            # Upload
            response = requests.post(
                f"{API_BASE_URL}/upload",
                files=files,
                data=data,
                timeout=30
            )

            if response.status_code == 200:
                return True, f"✓ Uploaded: {relative_path}"
            else:
                return False, f"✗ Failed: {relative_path} (HTTP {response.status_code})"

    except Exception as e:
        return False, f"✗ Error: {relative_path} - {str(e)}"

def main():
    print("Spring AI RAG Multi-File Uploader")
    print("=" * 50)
    print(f"API Endpoint: {API_BASE_URL}/upload")
    print(f"Source Directory: {SOURCE_DIR}")
    print(f"Max Concurrent Uploads: {MAX_WORKERS}")
    print()

    # Get files to upload
    files = get_files_to_upload()
    total_files = len(files)

    if total_files == 0:
        print("No supported files found!")
        return

    print(f"Found {total_files} files to upload")
    print()

    # Upload files with progress tracking
    success_count = 0
    failed_count = 0

    with ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
        # Submit all upload tasks
        future_to_file = {
            executor.submit(upload_file, file_path, relative_path): (file_path, relative_path)
            for file_path, relative_path in files
        }

        # Process completed uploads
        for future in as_completed(future_to_file):
            success, message = future.result()
            print(message)

            if success:
                success_count += 1
            else:
                failed_count += 1

    # Summary
    print()
    print("Upload Summary:")
    print(f"  Total files: {total_files}")
    print(f"  Successful: {success_count}")
    print(f"  Failed: {failed_count}")

    if failed_count == 0:
        print("\nAll files uploaded successfully! ✓")
        return 0
    else:
        print(f"\n{failed_count} files failed to upload.")
        return 1

if __name__ == "__main__":
    exit(main())