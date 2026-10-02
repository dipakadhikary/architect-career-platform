# Vendored AI Contracts

This directory contains the generated Python package `acos_ai_contracts` synchronized from
`architect-career-ai-contracts`.

Regenerate and sync:

```bash
# in architect-career-ai-contracts
mvn clean verify -Ppython

# in architect-career-ai-platform
python scripts/sync_contracts.py
pip install ./third_party/acos_ai_contracts
```

Do not hand-edit models under `acos_ai_contracts/`.
