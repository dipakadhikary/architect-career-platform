"""Integration tests for Knowledge API endpoints."""

from __future__ import annotations

from fastapi.testclient import TestClient


def test_openapi_includes_knowledge_paths(client: TestClient) -> None:
    payload = client.get("/openapi.json").json()
    paths = payload["paths"]
    assert "/api/v1/ai/knowledge/index" in paths
    assert "/api/v1/ai/knowledge/search" in paths
    assert "/api/v1/ai/knowledge/summarize" in paths


def test_knowledge_index_search_summarize_flow(client: TestClient) -> None:
    index = client.post(
        "/api/v1/ai/knowledge/index",
        json={
            "userId": "user-1",
            "noteId": "note-1",
            "title": "Clean Architecture",
            "content": (
                "Clean Architecture separates business rules from frameworks. "
                "Ports and adapters keep infrastructure replaceable. "
                "RAG pipelines should follow the same separation."
            ),
            "tags": ["architecture"],
        },
    )
    assert index.status_code == 200, index.text
    indexed = index.json()
    assert indexed["status"] == "INDEXED"
    assert indexed["noteId"] == "note-1"
    assert "documentId" in indexed

    search = client.post(
        "/api/v1/ai/knowledge/search",
        json={"userId": "user-1", "query": "clean architecture ports", "limit": 5},
    )
    assert search.status_code == 200, search.text
    hits = search.json()["hits"]
    assert isinstance(hits, list)
    assert hits
    assert hits[0]["noteId"] == "note-1"
    assert 0.0 <= hits[0]["score"] <= 1.0

    summarize = client.post(
        "/api/v1/ai/knowledge/summarize",
        json={
            "userId": "user-1",
            "noteId": "note-1",
            "content": "Explain Clean Architecture and replaceable adapters.",
            "maxLength": 400,
        },
    )
    assert summarize.status_code == 200, summarize.text
    body = summarize.json()
    assert body["summary"]
    assert body.get("noteId") == "note-1"
