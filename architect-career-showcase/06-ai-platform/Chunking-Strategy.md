# Chunking Strategy

## Algorithms (implemented)

| Strategy | Class | Behavior |
| --- | --- | --- |
| `recursive` (default) | `RecursiveChunker` | Split by separator hierarchy until size fit |
| `token` | `TokenChunker` | Whitespace token windows with overlap |
| `sentence` | `SentenceChunker` | Sentence groups with overlap |
| `markdown` | `MarkdownChunker` | Split on ATx headings; oversized sections recurse |
| `semantic` | `SemanticChunker` | **Extension point** — currently delegates to recursive |

Registry: `ChunkerRegistry` selects by `ChunkingStrategy`.

## Configuration

From `AppSettings`:

- `chunking_strategy` default `recursive`
- `chunk_size` default **800**
- `chunk_overlap` default **120**

Overlap is clamped to `size - 1` when size > 1.

## Metadata preservation

Each `TextChunk` carries `chunk_id`, `index`, `start_offset`, `end_offset`, and text. Document/owner metadata is attached at upsert time in the vector record (not lost at chunk boundaries).

## Trade-offs

| Approach | Pros | Cons |
| --- | --- | --- |
| Recursive | Good general default | May split mid-idea |
| Token | Predictable length | Ignores structure |
| Sentence | Linguistic boundaries | Uneven chunk sizes |
| Markdown | Keeps heading sections | Poor for non-MD |
| Semantic (future) | Topic coherence | Needs embeddings/model cost |

## Future semantic chunking

Replace `SemanticChunker` fallback with embedding-similarity breakpoints or dedicated libraries — port already reserved.

## Interview Discussion

### Why this architecture?

Strategy pattern lets operators tune chunking without code changes to retrieval.

### Alternative approaches

Fixed character windows only; LLM-based segmenters. Recursive is the pragmatic default.

### Trade-offs

Defaults (800/120) are heuristics — domain notes may need different sizes.

### Scaling considerations

Chunk once at index time; never re-chunk on every query. Cache embeddings of chunks.

### How would this evolve?

Parent-child chunk indexes; late chunking; structure-aware HTML DOM chunkers.

### Principal AI Architect interview questions

**Q1. Is semantic chunking live?**  
Registered, but implementation falls back to recursive today.

**Q2. Why overlap?**  
Preserves boundary context so retrieval does not miss split sentences.
