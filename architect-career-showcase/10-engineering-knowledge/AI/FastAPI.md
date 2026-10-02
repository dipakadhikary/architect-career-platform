# FastAPI

## Introduction

FastAPI is a modern Python web framework for building APIs with type hints, automatic OpenAPI, and async support.

## Problem Statement

Flask/Django-only stacks are less ergonomic for typed request models and high-concurrency I/O to LLMs.

## Why ACOS Uses This

ACOS AI Platform exposes `/api/v1/ai/**` and system health/metrics on port 8090 via uvicorn; routes import `acos_ai_contracts` Pydantic models.

## Implementation Overview

ACOS AI Platform exposes `/api/v1/ai/**` and system health/metrics on port 8090 via uvicorn; routes import `acos_ai_contracts` Pydantic models.

## Best Practices

Keep routers thin; push logic to services/pipeline; rely on contracts package for shapes.

## Common Mistakes

- Re-defining request models by hand drifting from contracts.
- Running with auth disabled in production images.

## Alternative Approaches

Flask; Django Ninja; Litestar; NestJS.

## Trade-offs

Speed of development vs Python packaging/ops. Fits AI ecosystem libraries.

## References to ACOS modules

- [06-ai-platform](../../06-ai-platform/)
- [07-ai-contracts/Python-Models.md](../../07-ai-contracts/Python-Models.md)

## Interview Questions

**Q:** How are OpenAPI models kept true?
**A:** Generated/vendored `acos_ai_contracts`, not ad-hoc duplicates.

**Q:** ASGI server?
**A:** uvicorn.

## Further Reading

- fastapi.tiangolo.com

