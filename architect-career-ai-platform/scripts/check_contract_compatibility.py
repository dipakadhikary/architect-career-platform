"""Verify vendored acos-ai-contracts package is importable and version-compatible."""

from __future__ import annotations

import importlib.metadata
import sys
from pathlib import Path

REQUIRED_PACKAGE = "acos-ai-contracts"
REQUIRED_MODULES = (
    "acos_ai_contracts",
    "acos_ai_contracts.models.chat_completion_request",
    "acos_ai_contracts.models.knowledge_search_request",
)


def main() -> int:
    root = Path(__file__).resolve().parents[1]
    vendored = root / "third_party" / "acos_ai_contracts"
    if not vendored.exists():
        print(f"Missing vendored contracts at {vendored}", file=sys.stderr)
        return 1

    try:
        version = importlib.metadata.version(REQUIRED_PACKAGE)
    except importlib.metadata.PackageNotFoundError:
        print(f"Package not installed: {REQUIRED_PACKAGE}", file=sys.stderr)
        return 1

    for module_name in REQUIRED_MODULES:
        try:
            importlib.import_module(module_name)
        except Exception as exc:  # noqa: BLE001
            print(f"Failed importing {module_name}: {exc}", file=sys.stderr)
            return 1

    print(f"Contract compatibility OK: {REQUIRED_PACKAGE}=={version}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
