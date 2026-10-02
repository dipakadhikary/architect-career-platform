"""Foundation health endpoint tests."""

from __future__ import annotations

from fastapi.testclient import TestClient


def test_liveness(client: TestClient) -> None:
    response = client.get("/api/v1/system/liveness")
    assert response.status_code == 200
    assert response.json()["status"] == "UP"


def test_contract_health(client: TestClient) -> None:
    response = client.get("/api/v1/ai/health")
    assert response.status_code == 200
    payload = response.json()
    assert payload["status"] in {"AVAILABLE", "DEGRADED", "UNAVAILABLE"}
    assert payload["enabled"] is True
    assert "checkedAt" in payload
    assert "X-Correlation-Id" in response.headers
