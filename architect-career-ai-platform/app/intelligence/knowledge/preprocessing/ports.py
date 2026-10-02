"""Preprocessing ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field


@dataclass(slots=True, frozen=True)
class PreprocessOptions:
    normalize_whitespace: bool = True
    normalize_unicode: bool = True
    remove_duplicates: bool = True
    remove_headers_footers: bool = True
    detect_language: bool = True


@dataclass(slots=True, frozen=True)
class PreprocessResult:
    text: str
    language: str | None = None
    diagnostics: dict[str, str] = field(default_factory=dict)


class PreprocessorPort(ABC):
    @abstractmethod
    async def process(
        self, text: str, options: PreprocessOptions | None = None
    ) -> PreprocessResult:
        raise NotImplementedError


class OcrPort(ABC):
    """OCR extension point for scanned documents."""

    @abstractmethod
    async def extract_text(self, payload: bytes, media_type: str | None = None) -> str:
        raise NotImplementedError
