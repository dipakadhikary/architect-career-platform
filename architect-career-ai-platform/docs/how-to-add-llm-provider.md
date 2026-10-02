# How To Add a New LLM Provider

1. Keep `app.intelligence.llm.ports.LlmPort` unchanged unless the port itself must evolve.
2. Create `app/infrastructure/llm/<provider>_adapter.py` implementing `LlmPort`.
3. Ensure vendor SDK imports stay inside the adapter file.
4. Register the adapter in `app.shared.di.container.ApplicationContainer`.
5. Add settings fields in `AppSettings` and `.env.example`.
6. Add a mock/unit test under `tests/`.
7. Document provider-specific env vars in the development guide.
