"""Filesystem adapter for local artifact storage."""

from __future__ import annotations

from pathlib import Path


class FilesystemAdapter:
    def __init__(self, root: str | Path = ".data") -> None:
        self._root = Path(root)
        self._root.mkdir(parents=True, exist_ok=True)

    def resolve(self, *parts: str) -> Path:
        path = self._root.joinpath(*parts)
        path.parent.mkdir(parents=True, exist_ok=True)
        return path

    def write_bytes(self, relative_path: str, payload: bytes) -> Path:
        path = self.resolve(relative_path)
        path.write_bytes(payload)
        return path

    def read_bytes(self, relative_path: str) -> bytes:
        return self.resolve(relative_path).read_bytes()
