"""OCR extension point — disabled by default."""

from __future__ import annotations

from app.intelligence.knowledge.preprocessing.ports import OcrPort
from app.shared.exceptions import ValidationFailedError


class UnsupportedOcrAdapter(OcrPort):
    async def extract_text(self, payload: bytes, media_type: str | None = None) -> str:
        raise ValidationFailedError("OCR provider is not configured")
