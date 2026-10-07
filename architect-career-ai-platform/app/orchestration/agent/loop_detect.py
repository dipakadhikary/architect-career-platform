"""Stops a tool from being repeated with the same arguments."""

from __future__ import annotations

import json
from typing import Any


class LoopDetector:
    def __init__(self, repeat_limit: int) -> None:
        self._limit = repeat_limit
        self._seen: dict[str, int] = {}

    def repeated(self, tool: str, arguments: dict[str, Any]) -> bool:
        fingerprint = tool + ":" + json.dumps(arguments, sort_keys=True, default=str)
        count = self._seen.get(fingerprint, 0) + 1
        self._seen[fingerprint] = count
        return count >= self._limit
