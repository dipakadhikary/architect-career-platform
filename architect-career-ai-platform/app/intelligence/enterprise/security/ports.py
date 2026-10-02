"""Enterprise security helper ports."""

from __future__ import annotations

from abc import ABC, abstractmethod


class PromptSanitizerPort(ABC):
    @abstractmethod
    def sanitize(self, text: str) -> str:
        raise NotImplementedError


class DataMaskerPort(ABC):
    @abstractmethod
    def mask(self, text: str) -> str:
        raise NotImplementedError
