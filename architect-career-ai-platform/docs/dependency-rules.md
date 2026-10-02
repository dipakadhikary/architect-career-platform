# Dependency Rules

1. Dependencies point inward only.
2. Business domains never import `app.infrastructure`.
3. Intelligence ports never import vendor SDKs.
4. API depends on shared + orchestration/intelligence abstractions.
5. Infrastructure implements intelligence ports and is wired by DI.
6. Request/response models for AI contracts come from `acos_ai_contracts`.
