# How to add new events

1. Add reusable envelope/header schemas only in `asyncapi/common/common-events.yaml`.
2. Define channel + message + payload in the domain AsyncAPI file.
3. Document producer/consumer intent and retry/DLQ expectations.
4. Reference the channel from `asyncapi/ai-platform-events-v1.yaml` when it is part of the platform aggregate.
5. Add examples.
6. Update `CHANGELOG.md`.
7. Run `npm run lint:asyncapi && npm run generate:events`.
