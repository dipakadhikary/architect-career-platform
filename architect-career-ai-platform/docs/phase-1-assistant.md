# Phase 1 assistant

This document describes the Phase 1 assistant inside `architect-career-ai-platform`.

Retrieval, embeddings, chunking, vector search, reranking, conversation storage, and knowledge indexing are not part of this endpoint. Those remain later phases. The existing knowledge and agentic routes are unchanged and are not the Phase 1 API.

## Purpose

`POST /api/v1/ai/chat` answers a message list through a replaceable language-model provider. ACOS stays the system of record for users and content. This service does not create accounts and does not write knowledge notes.

## Architecture

```text
POST /api/v1/ai/chat
        |
        v
AssistantService          (app/orchestration/assistant)
        |
        v
LlmProvider               (app/intelligence/assistant/provider.py)
        |
        +---------------+
        |               |
OpenAiChatProvider   OllamaChatProvider
```

Provider selection happens only in `app/infrastructure/llm/chat_factory.py`.

## Project structure

| Path | Role |
|---|---|
| `app/api/v1/assistant.py` | `/health`, `/ready`, `/api/v1/ai/chat` |
| `app/api/assistant_auth.py` | JWT and service-credential checks |
| `app/orchestration/assistant/service.py` | Validation, system instruction, provider call |
| `app/intelligence/assistant/` | Request, response, and error models |
| `app/infrastructure/llm/*_chat_provider.py` | OpenAI and Ollama HTTP adapters |

## Configuration

Settings live on `AppSettings` and come from the environment. See `.env.example`.

| Variable | Meaning |
|---|---|
| `AI_ENABLED` | When false, chat returns `AI_DISABLED` and does not call a model |
| `AI_PROVIDER` | `openai` or `ollama` |
| `AI_MODEL` | Optional model override |
| `AI_TIMEOUT` | Provider call timeout in seconds (default 30) |
| `AI_MAX_TOKENS` | Completion cap |
| `AI_TEMPERATURE` | Sampling temperature |
| `AI_MAX_MESSAGES` | Maximum messages in one request |
| `AI_MAX_MESSAGE_CHARACTERS` | Maximum characters per message |
| `AI_RETRY_MAX_ATTEMPTS` | Total attempts, 1 to 3 |
| `AI_SYSTEM_INSTRUCTION` | Instruction prepended to the provider request |
| `AUTH_JWT_SECRET` | Same HMAC secret as the Java `JwtTokenProvider` |
| `AUTH_JWT_ISSUER` | Set to `acos-platform` to match Java |
| `OPENAI_API_KEY` | OpenAI credential. Never commit a real value |
| `OLLAMA_BASE_URL` | Local Ollama, default `http://localhost:11434` |

## Providers

- **OpenAI** (`AI_PROVIDER=openai`) calls `{OPENAI_BASE_URL}/chat/completions`.
- **Ollama** (`AI_PROVIDER=ollama`) calls `{OLLAMA_BASE_URL}/api/chat`.

Both return the same `ChatResponse`: `answer`, `model`, `provider`, `correlation_id`.

`azure_openai` remains on the older platform factory. The Phase 1 assistant rejects it with `AI_PROVIDER_NOT_CONFIGURED`.

## API

Probes (no authentication, no model call):

- `GET /health` → `{"status": "UP"}`
- `GET /ready` → process and configuration. Missing OpenAI key marks configuration `DOWN`. It does not call the model, Redis, or Qdrant.

Chat:

- `POST /api/v1/ai/chat`
- Body: `{"messages": [{"role": "user", "content": "What is the Factory Pattern?"}]}`
- Roles: `system`, `user`, `assistant`
- Optional `user_id` must match the authenticated owner

OpenAPI: `/docs` and `/openapi.json`.

## Authentication

There is no login endpoint.

- User calls send `Authorization: Bearer` with the ACOS access token. The owner is the JWT `sub` (the user UUID). A body `user_id` that differs is rejected.
- Java indexing-style calls may send `X-Internal-Service` or, when `AUTH_API_KEY_ENABLED=true`, `X-API-Key`, and must also send `X-User-Id`.
- An invalid bearer token is rejected even if a service key is also present.

Local development uses the same check. Sign a token with `AUTH_JWT_SECRET` or log in through ACOS and reuse the access token. Do not disable authentication for the chat route.

## Errors

Responses use the existing problem-details shape (`code`, `title`, `detail`, `correlationId`). Provider secrets and prompt text are not returned.

| Condition | Code |
|---|---|
| Invalid body | `AI_VALIDATION_FAILED` |
| Missing or invalid credentials | `AI_AUTHENTICATION_FAILED` |
| `user_id` mismatch | `AI_FORBIDDEN` |
| `AI_ENABLED=false` | `AI_DISABLED` |
| Provider not selected or missing credentials | `AI_PROVIDER_NOT_CONFIGURED` |
| Provider unreachable or 5xx after retries | `AI_PROVIDER_UNAVAILABLE` |
| Provider rejected the credentials | `AI_PROVIDER_AUTHENTICATION_FAILED` |
| Provider rate limit | `AI_RATE_LIMITED` |
| Provider timeout | `AI_UPSTREAM_TIMEOUT` |
| Other provider failure | `AI_PROVIDER_ERROR` |

Retries run only for connection failures and HTTP 502, 503, and 504, and only up to `AI_RETRY_MAX_ATTEMPTS`. Timeouts, authentication failures, and request errors are not retried.

## Logging and correlation

Structlog records provider name, owner id, duration, outcome, and token counts. It does not record API keys, bearer tokens, message text, or the answer.

`X-Correlation-Id` is accepted from ACOS and returned on the response. When the header is absent, the existing request middleware generates one.

Metrics: `acos_ai_assistant_requests_total` and `acos_ai_assistant_request_duration_seconds`.

## Local development

```bash
cd architect-career-ai-platform
python -m venv .venv
.venv\Scripts\activate
pip install -e ".[dev]"
copy .env.example .env
```

Set `OPENAI_API_KEY` in `.env`, or set `AI_PROVIDER=ollama` and run Ollama locally (`ollama pull llama3.2`). Then:

```bash
uvicorn app.main:app --reload --port 8090
```

ACOS stays on port 8080. The browser continues to call Java. Java reaches this service only when `AI_PLATFORM_ENABLED=true` and `AI_PLATFORM_BASE_URL` points at port 8090. Wiring Java to `POST /api/v1/ai/chat` is not part of this phase.

## Testing

```bash
pytest tests/unit/test_assistant.py tests/unit/test_assistant_auth.py tests/unit/test_assistant_settings.py tests/integration/test_assistant_api.py
```

These tests mock HTTP. They do not call OpenAI or Ollama.

## Docker

```bash
docker build -t acos-ai-platform .
docker run --rm -p 8090:8090 --env-file .env acos-ai-platform
```

The image runs as user `acos` and has no secrets baked in. Pass credentials with the environment. The container health check calls `GET /health`.

## Later RAG work

Phase 3 and later can reuse `CallerContext.owner_id` as the retrieval filter. This phase does not read the ACOS database, does not write vectors, and does not store conversations.
