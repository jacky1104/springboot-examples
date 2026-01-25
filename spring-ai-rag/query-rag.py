#!/usr/bin/env python3
"""
Query script for Spring AI RAG application
"""

import requests
import json
import sys

API_URL = "http://localhost:8080/api/qa/ask"

def query_rag(question):
    """Send a query to the RAG system"""
    try:
        response = requests.post(
            API_URL,
            json={"question": question},
            headers={"Content-Type": "application/json"},
            timeout=30
        )

        if response.status_code == 200:
            data = response.json()
            return data.get('answer', 'No answer returned')
        else:
            return f"Error: HTTP {response.status_code} - {response.text}"
    except Exception as e:
        return f"Error: {str(e)}"

def main():
    # Predefined queries about auditlog servers
    queries = [
        {
            "title": "Auditlog server in devqa_global environment",
            "question": "What is the auditlog server configuration in the devqa_global environment? Please provide the server details, port, and any relevant configuration."
        },
        {
            "title": "Auditlog server in devqa_europe environment",
            "question": "What is the auditlog server configuration in the devqa_europe environment? Please provide the server details, port, and any relevant configuration."
        },
        {
            "title": "All auditlog server configurations",
            "question": "List all auditlog server configurations across different environments including devqa_global and devqa_europe. Include server names, ports, and any specific settings."
        }
    ]

    print("Spring AI RAG Query System")
    print("=" * 50)
    print(f"API Endpoint: {API_URL}")
    print()

    # Run predefined queries
    for i, query in enumerate(queries, 1):
        print(f"{i}. {query['title']}:")
        print("-" * 40)
        answer = query_rag(query['question'])
        print(answer)
        print()

    # Interactive mode
    print("\nEnter your own question (or 'quit' to exit):")
    while True:
        user_question = input("\nQuestion: ").strip()
        if user_question.lower() in ['quit', 'exit', 'q']:
            break
        if user_question:
            print("Answer:")
            print("-" * 40)
            answer = query_rag(user_question)
            print(answer)

if __name__ == "__main__":
    main()