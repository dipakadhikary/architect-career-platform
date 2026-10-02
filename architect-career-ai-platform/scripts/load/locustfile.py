"""
Load-testing scaffold for the AI Platform.

Supports concurrent requests, long conversations, large documents, and high throughput.
Use Locust against a running local or staging instance.

Examples:
  locust -f scripts/load/locustfile.py --host http://127.0.0.1:8090
  locust -f scripts/load/locustfile.py --headless -u 20 -r 5 -t 60s --host http://127.0.0.1:8090
"""

from __future__ import annotations

from locust import HttpUser, between, task


class AiPlatformUser(HttpUser):
    wait_time = between(0.1, 0.5)

    @task(5)
    def health(self) -> None:
        self.client.get("/api/v1/ai/health")

    @task(3)
    def chat(self) -> None:
        self.client.post(
            "/api/v1/ai/chat/completions",
            json={
                "userId": "load-user",
                "message": "Explain hexagonal architecture briefly.",
                "conversationId": "load-convo-1",
            },
        )

    @task(2)
    def knowledge_search(self) -> None:
        self.client.post(
            "/api/v1/ai/knowledge/search",
            json={"userId": "load-user", "query": "event driven design", "topK": 5},
        )

    @task(1)
    def large_document_summarize(self) -> None:
        content = ("Architecture decision records capture context and consequences. " * 200)
        self.client.post(
            "/api/v1/ai/knowledge/summarize",
            json={"userId": "load-user", "content": content},
        )

    @task(1)
    def long_conversation(self) -> None:
        history = [
            {"role": "user", "content": f"Turn {i}: discuss scalability patterns"}
            for i in range(20)
        ]
        self.client.post(
            "/api/v1/ai/chat/completions",
            json={
                "userId": "load-user",
                "message": "Summarize our discussion so far.",
                "conversationId": "load-long",
                "history": history,
            },
        )
