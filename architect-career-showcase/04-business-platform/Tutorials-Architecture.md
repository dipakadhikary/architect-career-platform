# Tutorials Architecture

Tutorials extend ACOS with hierarchical learning material. The feature reuses existing Knowledge-style Markdown rendering, authentication/authorization, `ApiResponse` envelopes, Flyway migrations, and React feature-module patterns. It does **not** introduce a parallel content platform.

## Domain model

| Entity | Table | Notes |
| --- | --- | --- |
| `TutorialTopic` | `acos.tutorial_topics` | Self-referencing `parent_id`, owner-scoped, stable `slug` + materialized `path` |
| `TutorialConcept` | `acos.tutorial_concepts` | One optional Markdown concept per topic |
| `TutorialQuestion` | `acos.tutorial_questions` | Many Markdown Q&A pairs per topic |

Hierarchy is an adjacency list with arbitrary depth. Paths are unique per owner (`owner_id`, `path`). Circular parent assignment is rejected in the service layer.

No seed/tutorial sample data is loaded by migrations.

## Content types

Every topic may expose:

1. **Concept** — Markdown body stored in PostgreSQL (`TEXT`)
2. **Questions & Answers** — Markdown question + answer pairs

Images are supported only through existing Markdown image URLs rendered by `MarkdownViewer` (rehype-sanitize). There is no separate tutorial image-upload subsystem because Knowledge also stores Markdown in DB without a dedicated binary image store.

## URLs

Stable frontend routes:

| Route | Purpose |
| --- | --- |
| `/tutorials` | Home + button-triggered global search + create topic |
| `/tutorials/{path}` | Topic hub (edit/move/add child) |
| `/tutorials/{path}/concept` | Concept view/edit |
| `/tutorials/{path}/questions` | Q&A list with per-question Show/Hide Answer |

Example: `/tutorials/design-patterns/java-design-patterns/creational/concept`

## REST APIs (`/api/v1/tutorials`)

All endpoints require bearer JWT and are owner-scoped (same model as Knowledge).

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/tree` | Sidebar hierarchy (titles/slugs/flags only) |
| POST | `/topics` | Create topic |
| PUT | `/topics/{id}` | Update title/slug/parent/sort |
| DELETE | `/topics/{id}` | Cascade delete |
| GET | `/topics/by-path` | Topic metadata |
| GET | `/topics/by-path/concept` | Concept Markdown |
| PUT | `/topics/{id}/concept` | Upsert concept |
| GET | `/topics/by-path/questions` | Q&A list |
| POST | `/topics/{id}/questions` | Create Q&A |
| PUT | `/questions/{id}` | Update Q&A |
| DELETE | `/questions/{id}` | Delete Q&A |
| GET | `/search?q=` | PostgreSQL FTS across concept + questions + answers |

Responses use the canonical `ApiResponse<T>` envelope.

## PostgreSQL Full-Text Search

Migration `V13__create_tutorial_tables.sql` adds expression GIN indexes for FTS:

- `tutorial_concepts` — `GIN (to_tsvector('english', coalesce(content, '')))`
- `tutorial_questions` — `GIN (to_tsvector('english', question || ' ' || answer))`

Search uses:

- `websearch_to_tsquery('english', :q)`
- `@@` match against the same `to_tsvector(...)` expressions (index-backed)
- `ts_rank` for relevance ordering
- `ts_headline` for snippets (`<mark>` highlights)

Snippet HTML is sanitized server-side (only `<mark>` retained) and rendered safely on the frontend without `dangerouslySetInnerHTML`.

Search executes only after the user clicks **Search** (no live search-on-keystroke).

## Frontend reuse

- Existing `Sidebar` + collapsible Tutorials tree (`TutorialSidebarTree`)
- `MarkdownViewer`, `PageHeader`, `FormDialog`, `EmptyState`, `ErrorPanel`, `LoadingOverlay`
- React Query feature hooks and axios `apiClient` unwrap conventions
- Auth via existing protected routes / JWT session

## How users create content

1. Open **Tutorials** in the left nav (or `/tutorials`)
2. **New topic** — optionally choose a parent
3. From a topic hub, **Add child**, **Create Concept**, or **Add Questions & Answers**
4. Edit Markdown the same way as Knowledge notes
5. Use global Search on the Tutorials home page to find matches across concepts and Q&A

## Testing strategy

| Layer | Coverage |
| --- | --- |
| Unit | `TutorialSluggerTest`, `TutorialServiceImplTest` (hierarchy, circular prevention, concept/Q&A, search mapping) |
| Repository / FTS | `TutorialSearchRepositoryTest` on PostgreSQL (Testcontainers) |
| API integration | `TutorialIntegrationTest` (CRUD, search, auth, owner isolation, validation) |
| Frontend | Component tests for tree, breadcrumb, independent Show Answer, search button behavior, results grid, empty/error states |

## Migration

`V13__create_tutorial_tables.sql` — topics/concepts/questions + FTS generated columns + GIN indexes. Zero tutorial rows after clean install.
