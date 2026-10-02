# Layer Responsibilities

| Layer | Responsibility | Must not |
| --- | --- | --- |
| API | HTTP, auth extraction, validation, contract responses | Call vendor SDKs |
| Orchestration | Compose intelligence ports into workflows | Contain domain rules |
| Intelligence | Define ports/DTOs for AI capabilities | Import OpenAI/Qdrant/Redis SDKs |
| Infrastructure | Adapt vendor SDKs to ports | Leak SDK types upward |
| Shared | Cross-cutting platform utilities | Contain business logic |
