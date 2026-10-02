# Pydantic

## Introduction

Pydantic validates data via Python type annotations—v2 powers FastAPI bodies and settings.

## Problem Statement

Dicts-everywhere APIs fail late inside RAG code with cryptic KeyErrors.

## Why ACOS Uses This

ACOS uses pydantic-settings for `Settings`, SecretStr for keys, and generated models from OpenAPI. CI installs pydantic for contracts validation.

## Implementation Overview

ACOS uses pydantic-settings for `Settings`, SecretStr for keys, and generated models from OpenAPI. CI installs pydantic for contracts validation.

## Best Practices

Parse at edges; use SecretStr; `validate_for_runtime()` for production invariants.

## Common Mistakes

- Printing SecretStr internals into logs.
- Optional fields that are effectively required in prod without validators.

## Alternative Approaches

dataclasses + manual checks; attrs; marshmallow.

## Trade-offs

Runtime safety vs validation CPU cost on large payloads.

## References to ACOS modules

- `app/shared/config/settings.py`
- vendored `third_party/acos_ai_contracts`

## Interview Questions

**Q:** Why SecretStr?
**A:** Reduces accidental string interpolation of secrets.

**Q:** Settings source?
**A:** Env + optional `.env` file.

## Further Reading

- docs.pydantic.dev

