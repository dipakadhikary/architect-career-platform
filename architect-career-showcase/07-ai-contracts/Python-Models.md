# Python Models

## What is generated

OpenAPI Generator (`generatorName: python`) emits package **`acos_ai_contracts`** under `target/generated/python`, packaged as `career-ai-python-sdk.zip`.

Config highlights (`pom.xml` / `generator/python/`):

| Option | Value |
| --- | --- |
| `packageName` | `acos_ai_contracts` |
| `projectName` | `career-ai-python-sdk` (Maven) / `acos-ai-contracts` (config YAML) |
| `packageVersion` | `${project.version}` (1.0.0) |

Models are **Pydantic v2**-oriented outputs from the upstream Python generator.

## FastAPI integration (AI Platform)

AI Platform declares:

```text
acos-ai-contracts @ file:./third_party/acos_ai_contracts
```

Route modules import request/response models from `acos_ai_contracts.models.*` (knowledge, learning, career, portfolio, chat, health).

Sync workflow (from `third_party/README.md`):

```bash
# contracts repo
mvn clean verify -Ppython

# AI Platform
python scripts/sync_contracts.py
pip install ./third_party/acos_ai_contracts
```

Do **not** hand-edit vendored models.

## Validation

| Layer | Mechanism |
| --- | --- |
| Contracts CI | `pip install pydantic` + `scripts/maven/validate-python.py` after generate |
| Runtime (AI) | Pydantic validation on FastAPI request bodies |
| Compatibility helper | AI Platform `scripts/check_contract_compatibility.py` imports key models |

## Interview Discussion

### Why Contract First?

Python request models track the same OpenAPI the Java Feign layer targets.

### Alternative approaches

Hand-written Pydantic models copied from Java — drift source.

### Trade-offs

Vendoring requires an explicit sync step until a private package index is used.

### Why not shared DTO libraries?

Java classes cannot be imported into FastAPI.

### Why OpenAPI?

Single schema → Pydantic + Feign + Axios.

### When would you choose gRPC?

`grpcio` + protobuf messages instead of Pydantic HTTP models.

### Scaling considerations

Publish `acos-ai-contracts` to an internal PyPI; pin versions in `pyproject.toml`.

### Principal API Architect interview questions

**Q1. Where do live models live for AI?**  
`architect-career-ai-platform/third_party/acos_ai_contracts`.

**Q2. Is the FastAPI server stub generated?**  
No — only models/client package; handlers are hand-written against those models.
