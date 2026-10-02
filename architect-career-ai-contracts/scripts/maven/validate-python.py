#!/usr/bin/env python3
"""Validate generated Python package structure for the ACOS AI contracts build."""

from __future__ import annotations

import ast
import sys
from pathlib import Path


def main() -> int:
    if len(sys.argv) != 2:
        print("Usage: validate-python.py <generated-python-dir>", file=sys.stderr)
        return 2

    root = Path(sys.argv[1])
    if not root.exists():
        print(f"[python] Generated directory not found: {root}", file=sys.stderr)
        return 1

    py_files = list(root.rglob("*.py"))
    if not py_files:
        print(f"[python] No Python sources found under {root}", file=sys.stderr)
        return 1

    failed = False
    for file_path in py_files:
        try:
            source = file_path.read_text(encoding="utf-8")
            ast.parse(source, filename=str(file_path))
        except SyntaxError as exc:
            failed = True
            print(f"[python] Syntax error in {file_path}: {exc}", file=sys.stderr)

    package_dirs = [p for p in root.rglob("*") if p.is_dir() and (p / "__init__.py").exists()]
    if not package_dirs:
        print("[python] No Python packages (__init__.py) detected", file=sys.stderr)
        failed = True

    if failed:
        print("[python] Validation failed", file=sys.stderr)
        return 1

    print(f"[python] Validated {len(py_files)} Python source files")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
