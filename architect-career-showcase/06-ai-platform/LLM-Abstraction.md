# LLM Abstraction

## Port and factory

`LlmPort` implemented by adapters; `build_llm_port` selects from `llm_provider`:

| Provider | Adapter | Enable flags |
| --- | --- | --- |
| `openai` (default) | `OpenAIAdapter` | `openai_enabled`, API key |
| `azure_openai` | `AzureOpenAIAdapter` | `azure_openai_enabled`, endpoint/deployment/key |
| `ollama` | `OllamaAdapter` | `ollama_enabled`, `ollama_base_url` |

Defaults: `openai_default_model=gpt-4o-mini`, `ollama_default_model=llama3.2`.

## Usage paths

- Agentic `LlmReasoner` for reasoning/chat-style generation.
- Knowledge summarize/response paths call `LlmPort` when a generative provider is selected and enabled.
- Factory fallback: `ExtractiveSummarizer` when the configured provider is not enabled / does not match the enablement chain — sentence extraction rather than a live LLM.
- Model router may also target **`extractive`** via `routing_fallback_provider` (default `extractive`).

## Streaming

| Layer | Behavior |
| --- | --- |
| HTTP APIs | Chat and other routes return **full JSON** bodies — no SSE/WebSocket streaming endpoints |
| `OpenAIAdapter` / `AzureOpenAIAdapter` | Adapter `stream` methods perform real token streams from the SDK |
| `OllamaAdapter` | Pseudo-stream: completes then yields once |
| `ExtractiveSummarizer` | Stream mirrors complete |

Do not claim end-user streaming chat UX; adapter streaming is infrastructure-ready only.

## Fallback

Enterprise pipeline resilience: retry, timeout, circuit breaker, bulkhead, optional soft fallback (except knowledge index/search/summarize which disallow soft fake success). Token/cost fields on some responses use placeholder usage structs for metrics labeling — not a billing integration.

## Future providers

Anthropic, Bedrock, Azure AI Foundry catalogs, on-prem vLLM OpenAI-compatible endpoints.

## Interview Discussion

### Why this architecture?

One `LlmPort` keeps workflows provider-agnostic and allows Ollama for air-gapped demos.

### Alternative approaches

Direct OpenAI calls in every workflow — rejected for testability and lock-in.

### Trade-offs

Lowest-common-denominator port features; advanced provider tools need careful extension.

### Scaling considerations

Per-provider rate limits; router cost/latency policies; connection pooling via httpx factory.

### How would this evolve?

Streaming chat; tool-calling parity matrix; structured output JSON schema enforcement.

### Principal AI Architect interview questions

**Q1. Default chat model?**  
`gpt-4o-mini` when OpenAI provider selected.

**Q2. Can Azure be primary?**  
Yes — set `llm_provider=azure_openai` and enable Azure settings.
