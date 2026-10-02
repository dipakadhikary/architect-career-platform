"""Integration tests for agentic contract orchestration endpoints."""

from __future__ import annotations

from fastapi.testclient import TestClient


def test_openapi_includes_agentic_paths(client: TestClient) -> None:
    paths = client.get("/openapi.json").json()["paths"]
    assert "/api/v1/ai/chat/completions" in paths
    assert "/api/v1/ai/learning/quiz/generate" in paths
    assert "/api/v1/ai/career/resume/generate" in paths
    assert "/api/v1/ai/portfolio/review" in paths


def test_chat_completion_flow(client: TestClient) -> None:
    response = client.post(
        "/api/v1/ai/chat/completions",
        json={
            "userId": "11111111-1111-1111-1111-111111111111",
            "message": "How should I learn system design?",
            "history": [
                {"role": "user", "content": "I want to become a staff engineer."},
                {"role": "assistant", "content": "Focus on distributed systems."},
            ],
        },
    )
    assert response.status_code == 200, response.text
    body = response.json()
    assert body["conversationId"]
    assert body["message"]
    assert body.get("model") == "acos-chat-v1"


def test_learning_career_portfolio_workflows(client: TestClient) -> None:
    quiz = client.post(
        "/api/v1/ai/learning/quiz/generate",
        json={
            "userId": "11111111-1111-1111-1111-111111111111",
            "topic": "Clean Architecture",
            "difficulty": "INTERMEDIATE",
            "questionCount": 2,
        },
    )
    assert quiz.status_code == 200, quiz.text
    quiz_body = quiz.json()
    assert quiz_body["quizId"]
    assert len(quiz_body["questions"]) == 2

    recommend = client.post(
        "/api/v1/ai/learning/topics/recommend-next",
        json={
            "userId": "11111111-1111-1111-1111-111111111111",
            "completedTopics": ["REST basics"],
            "goals": ["Become a backend architect"],
        },
    )
    assert recommend.status_code == 200, recommend.text
    assert recommend.json()["topic"]

    progress = client.post(
        "/api/v1/ai/learning/progress/evaluate",
        json={
            "userId": "11111111-1111-1111-1111-111111111111",
            "completedTopics": ["REST basics", "Spring MVC"],
            "quizScores": [80, 90],
        },
    )
    assert progress.status_code == 200, progress.text
    assert 0 <= progress.json()["progressPercent"] <= 100

    resume = client.post(
        "/api/v1/ai/career/resume/generate",
        json={
            "userId": "11111111-1111-1111-1111-111111111111",
            "targetRole": "Principal Architect",
            "experienceHighlights": ["Led platform modernization"],
            "skills": ["Java", "Event-Driven Architecture"],
        },
    )
    assert resume.status_code == 200, resume.text
    assert resume.json()["format"] == "markdown"
    assert "Principal Architect" in resume.json()["content"]

    interview = client.post(
        "/api/v1/ai/career/interview/analyze",
        json={
            "userId": "11111111-1111-1111-1111-111111111111",
            "transcript": "Discussed CAP theorem and trade-offs.",
            "jobDescription": "Distributed systems role",
        },
    )
    assert interview.status_code == 200, interview.text
    assert interview.json()["summary"]

    cover = client.post(
        "/api/v1/ai/career/cover-letter/generate",
        json={
            "userId": "11111111-1111-1111-1111-111111111111",
            "targetRole": "Staff Engineer",
            "companyName": "Acme",
            "highlights": ["Built developer platform"],
        },
    )
    assert cover.status_code == 200, cover.text
    assert cover.json()["content"]

    portfolio = client.post(
        "/api/v1/ai/portfolio/review",
        json={
            "userId": "11111111-1111-1111-1111-111111111111",
            "projectIds": ["77777777-7777-7777-7777-777777777777"],
            "targetRole": "Staff Engineer",
        },
    )
    assert portfolio.status_code == 200, portfolio.text
    assert portfolio.json()["summary"]

    skill_gap = client.post(
        "/api/v1/ai/portfolio/skill-gap/analyze",
        json={
            "userId": "11111111-1111-1111-1111-111111111111",
            "targetRole": "Cloud Architect",
            "currentSkills": ["Java", "Kubernetes"],
            "projectTechnologies": ["Spring Boot"],
        },
    )
    assert skill_gap.status_code == 200, skill_gap.text
    assert skill_gap.json()["summary"]
