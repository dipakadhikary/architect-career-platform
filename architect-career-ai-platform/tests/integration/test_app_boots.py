"""Integration-style boot checks for the platform foundation."""

from __future__ import annotations

from fastapi.testclient import TestClient


def test_openapi_available(client: TestClient) -> None:
    response = client.get("/openapi.json")
    assert response.status_code == 200
    payload = response.json()
    assert payload["info"]["title"] == "ACOS AI Platform"
    paths = payload["paths"]
    assert "/api/v1/ai/health" in paths
    assert "/api/v1/ai/knowledge/index" in paths
    assert "/api/v1/ai/chat/completions" in paths
    assert "/api/v1/system/liveness" in paths
    assert "/api/v1/system/readiness" in paths
    assert "/api/v1/system/metrics" in paths


def test_metrics_endpoint(client: TestClient) -> None:
    client.get("/api/v1/system/liveness")
    response = client.get("/api/v1/system/metrics")
    assert response.status_code == 200
    assert "acos_ai_http_requests_total" in response.text
