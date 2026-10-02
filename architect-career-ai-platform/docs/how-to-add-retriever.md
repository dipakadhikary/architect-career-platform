# How To Add a New Retriever

1. Implement `app.intelligence.retrievers.ports.RetrieverPort`.
2. Place the adapter under `app/infrastructure/vector/` (or a dedicated package).
3. Wire through DI.
4. Do not expose vendor client types outside infrastructure.
