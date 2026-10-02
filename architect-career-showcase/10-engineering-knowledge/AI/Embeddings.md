# Embeddings

## Introduction

Embeddings map text to dense vectors so semantic similarity becomes geometric distance.

## Problem Statement

Keyword-only search misses paraphrased knowledge notes.

## Why ACOS Uses This

ACOS embedding architecture supports provider adapters; tests often use hashing embeddings for determinism; Redis may cache embedding results.

## Implementation Overview

ACOS embedding architecture supports provider adapters; tests often use hashing embeddings for determinism; Redis may cache embedding results.

## Best Practices

Version embedding models; re-index on model change; don't mix dimensions in one collection casually.

## Common Mistakes

- Changing model without re-embedding.
- Logging raw text + vectors with PII.

## Alternative Approaches

Sparse BM25 only; hybrid search; ColBERT late interaction.

## Trade-offs

Semantic recall vs cost/latency of embedding calls. Cache aggressively.

## References to ACOS modules

- [06-ai-platform/Embedding-Architecture.md](../../06-ai-platform/Embedding-Architecture.md)

## Interview Questions

**Q:** Why cache embeddings?
**A:** Identical chunks shouldn't pay provider cost repeatedly.

**Q:** Test strategy?
**A:** Deterministic fake embeddings in unit/integration tests.

## Further Reading

- Provider embedding docs (OpenAI/Azure/Ollama)

