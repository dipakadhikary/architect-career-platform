#!/usr/bin/env python3
"""Synchronize generated Python contracts into third_party/acos_ai_contracts."""

from __future__ import annotations

import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_SOURCE = (
    ROOT.parent / "architect-career-ai-contracts" / "target" / "generated" / "python"
)
DEST = ROOT / "third_party" / "acos_ai_contracts"

CONTRACTS_PYPROJECT = """[build-system]
requires = ["setuptools>=68", "wheel"]
build-backend = "setuptools.build_meta"

[project]
name = "acos-ai-contracts"
version = "1.0.0"
description = "Generated ACOS AI Platform contract models"
requires-python = ">=3.10"
dependencies = [
  "urllib3>=1.25.3,<3.0.0",
  "python-dateutil>=2.8.2",
  "pydantic>=2",
  "typing-extensions>=4.7.1",
]

[tool.setuptools.packages.find]
where = ["."]
include = ["acos_ai_contracts*"]
"""


def main() -> int:
    source = Path(sys.argv[1]) if len(sys.argv) > 1 else DEFAULT_SOURCE
    package_src = source / "acos_ai_contracts"
    if not package_src.exists():
        print(f"Contracts package not found at {package_src}", file=sys.stderr)
        print(
            "Run `mvn clean verify -Ppython` in architect-career-ai-contracts first.",
            file=sys.stderr,
        )
        return 1

    if DEST.exists():
        shutil.rmtree(DEST)
    DEST.mkdir(parents=True)

    shutil.copytree(package_src, DEST / "acos_ai_contracts")
    (DEST / "pyproject.toml").write_text(CONTRACTS_PYPROJECT, encoding="utf-8")
    readme_src = source / "README.md"
    if readme_src.exists():
        shutil.copy2(readme_src, DEST / "README.md")

    (DEST / "SYNCED_FROM.txt").write_text(f"source={source}\n", encoding="utf-8")
    print(f"Synced contracts from {source} -> {DEST}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
