"""Problem Details mapping tests."""

from __future__ import annotations

from fastapi.testclient import TestClient


def test_not_found_problem_details(client: TestClient) -> None:
    response = client.get("/api/v1/system/does-not-exist")
    assert response.status_code == 404
    assert response.headers["content-type"].startswith("application/problem+json")
    payload = response.json()
    assert payload["status"] == 404
    assert payload["code"] == "AI_HTTP_ERROR"
