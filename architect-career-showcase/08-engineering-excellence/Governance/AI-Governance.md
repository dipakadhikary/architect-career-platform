# AI Governance

## Controls in platform

- Enterprise execution pipeline (policy, guardrails, routing, cost, audit hooks)  
- Prompt sanitization + data masking  
- Optional LangFuse for traces/evals  
- Model router / provider abstraction  
- Feature flag at Business boundary  

## Data & safety

- PII redaction toggle  
- Rate limiting  
- Production boot rules requiring auth configuration  

## Gaps

- Tenant isolation off by default  
- MCP/A2A stubs — no external tool governance yet  
- Eval suite not blocking CI  


## Interview Discussion

### Why this approach?

Treat AI as a governed platform capability, not a script.

### Alternative approaches

Call OpenAI from Business controllers. Rejected.

### Trade-offs

More moving parts than a single SDK call.

### Enterprise adoption

Model risk committee; approved model list; retention policy for prompts.

### Scaling considerations

Per-tenant budgets; human-in-the-loop queues.

### Principal Architect interview questions

**Q1. Where do guardrails run?**  
AI Platform enterprise pipeline / HeuristicGuardrails.

**Q2. Default LangFuse?**  
Disabled until keys + flag set.
