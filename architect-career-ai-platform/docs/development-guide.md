# Development Guide

## Setup

```bash
pip install -e ".[dev]"
pre-commit install
cp .env.example .env
python scripts/sync_contracts.py
```

## Run

```bash
uvicorn app.main:app --reload --port 8090
```

## Test / quality

```bash
pytest
ruff check app tests
black --check app tests
mypy app
```

## Environments

`APP_ENV` supports `development`, `test`, `stage`, `production`. Configuration loads from `.env`, process environment variables, and secret-backed env vars. No secrets are hardcoded.
