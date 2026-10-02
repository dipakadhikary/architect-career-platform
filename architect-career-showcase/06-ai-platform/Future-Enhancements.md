# Future Enhancements

Not implemented (or only stubbed/partial). Do not describe as production capabilities.

## RAG / data

| Item | Current | Target |
| --- | --- | --- |
| OCR | `UnsupportedOcrAdapter` raises | Real OCR provider |
| Semantic chunking | Falls back to recursive | Similarity-based segmentation |
| Cross-encoder/Cohere rerank | Optional paths | Default in prod profiles with deps |
| Multi-collection tenants | Single collection + flag | Enforced tenant isolation |
| Reindex tooling | Manual | Job to migrate embedding models |

## Agentic / protocols

| Item | Current | Target |
| --- | --- | --- |
| MCP network | In-memory transport/tools | Real MCP servers/clients |
| A2A network | In-memory registry/delegation | Inter-service agent protocol |
| Durable HITL checkpoints | In-process dict | Redis/Postgres checkpointer |
| Retrieval/prompt/LLM RAG cache slots | Methods on Redis RAG cache | Wire into retriever/prompt/LLM stages or remove dead API |
| HTTP `run_graph` | Service method only | Public graph invoke/resume/cancel routes |
| Adapter streaming → HTTP SSE | Adapter streams exist (OpenAI/Azure) | Chat streaming endpoint |
| `sentence-transformers` | Optional runtime import | Declare optional extra in `pyproject` for BGE/cross-encoder |
| Unused `orchestration/*.py` ABCs | Legacy extension stubs | Delete or implement deliberately |
| Richer tools | Small builtin set | OpenAPI/MCP-imported tools |

## Evaluation / observability

| Item | Current | Target |
| --- | --- | --- |
| LLM-as-judge / Ragas | Heuristic lexical | Offline+online judges |
| OTel span coverage | Provider bootstrap | Full FastAPI/httpx/RAG spans |
| OTel collector in Compose | Referenced, not defined | Add collector service |
| LangFuse default on | Disabled | Env-specific enablement |
| Prompt A/B | Version folders only | Traffic splitting experiments |

## Security / delivery

| Item | Current | Target |
| --- | --- | --- |
| Auth defaults | JWT/API key off | Mandatory outside development |
| mTLS to Business | API key/header patterns | Service mesh identity |
| Kubernetes manifests | Absent | Deploy/HPA/Secrets |
| Hallucination hard-block | Eval only | Guardrail tied to citations |

## Promotion rule

When code + tests land, move the item into the relevant Chapter 06 doc and remove it here. Add/adjust ADRs in Chapter 03 when the decision is architectural.
