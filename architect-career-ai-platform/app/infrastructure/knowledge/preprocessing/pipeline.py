"""Deterministic preprocessing pipeline."""

from __future__ import annotations

import re
import unicodedata
from collections import OrderedDict

from app.intelligence.knowledge.preprocessing.ports import (
    PreprocessOptions,
    PreprocessorPort,
    PreprocessResult,
)


class DefaultPreprocessor(PreprocessorPort):
    async def process(
        self, text: str, options: PreprocessOptions | None = None
    ) -> PreprocessResult:
        opts = options or PreprocessOptions()
        cleaned = text.replace("\r\n", "\n").replace("\r", "\n")
        diagnostics: dict[str, str] = {}

        if opts.normalize_unicode:
            cleaned = unicodedata.normalize("NFKC", cleaned)

        if opts.remove_headers_footers:
            lines = cleaned.split("\n")
            filtered = [
                line
                for line in lines
                if not re.match(r"^(page\s+\d+|confidential|copyright)", line.strip(), re.I)
            ]
            cleaned = "\n".join(filtered)
            diagnostics["headers_footers"] = "removed"

        if opts.normalize_whitespace:
            cleaned = re.sub(r"[ \t]+", " ", cleaned)
            cleaned = re.sub(r"\n{3,}", "\n\n", cleaned).strip()

        if opts.remove_duplicates:
            lines = cleaned.split("\n")
            unique = list(OrderedDict.fromkeys(line.strip() for line in lines if line.strip()))
            cleaned = "\n".join(unique)

        language = None
        if opts.detect_language:
            language = self._detect_language(cleaned)
            diagnostics["language"] = language or "unknown"

        return PreprocessResult(text=cleaned, language=language, diagnostics=diagnostics)

    def _detect_language(self, text: str) -> str | None:
        sample = text[:1000]
        if not sample:
            return None
        try:
            from langdetect import detect

            return detect(sample)
        except Exception:
            ascii_ratio = sum(1 for ch in sample if ord(ch) < 128) / max(len(sample), 1)
            return "en" if ascii_ratio > 0.85 else None
