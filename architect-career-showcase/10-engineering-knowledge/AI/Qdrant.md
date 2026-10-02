# Qdrant

## Introduction

Qdrant is a vector database for storing embeddings and performing similarity search.

## Problem Statement

Postgres alone is a weak ANN engine for large semantic corpora.

## Why ACOS Uses This

ACOS optionally enables Qdrant (`QDRANT_URL`, compose image `v1.12.5`). Readiness can degrade if enabled but down. Not required for all tests.

## Implementation Overview

ACOS optionally enables Qdrant (`QDRANT_URL`, compose image `v1.12.5`). Readiness can degrade if enabled but down. Not required for all tests.

## Best Practices

Keep collections per domain/version; gate behind feature flags; plan re-index jobs.

## Common Mistakes

- Assuming Qdrant is source of truth for user notes.
- No backup/reindex story.

## Alternative Approaches

pgvector; Weaviate; Milvus; OpenSearch k-NN.

## Trade-offs

Specialized ANN vs operational extra moving part. Optional in ACOS.

## References to ACOS modules

- AI docker-compose + vector docs in chapter 06
- [08-engineering-excellence/Infrastructure/Docker.md](../../08-engineering-excellence/Infrastructure/Docker.md)

## Interview Questions

**Q:** Default local URL?
**A:** `http://localhost:6333`.

**Q:** Required to boot?
**A:** Can run with Qdrant disabled depending on settings.

## Further Reading

- qdrant.tech documentation

