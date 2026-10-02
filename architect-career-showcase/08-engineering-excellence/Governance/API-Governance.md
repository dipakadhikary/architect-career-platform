# API Governance

## Business APIs

- Versioned under `/api/v1`  
- Envelope/`ApiResponse` patterns in platform  
- OpenAPI via springdoc (`/v3/api-docs`, Swagger UI)  
- JWT bearer scheme  

## AI APIs

Governed by `architect-career-ai-contracts`:

- OpenAPI 3.1 modular files + aggregator  
- Problem Details (`application/problem+json`)  
- Stable operationIds  
- Security schemes `bearerJwt`, `serviceApiKey`  
- SemVer + CHANGELOG  

## AsyncAPI

Spec-first; no broker requirement to merge contract changes — but event address stability still matters.


## Interview Discussion

### Why this approach?

Separate product APIs from AI contracts SoT.

### Alternative approaches

One giant OpenAPI for everything. Couples releases.

### Trade-offs

Business AI BFF still incomplete vs Web clients.

### Enterprise adoption

Portal + lint (Spectral) on all public APIs.

### Scaling considerations

Per-domain API owners.

### Principal Architect interview questions

**Q1. AI path prefix?**  
`/api/v1/ai/...`.

**Q2. Error media type for AI contracts?**  
`application/problem+json` (RFC 9457 style).
