"""Reusable resilience patterns (no vendor coupling)."""

from __future__ import annotations

import asyncio
import time
from collections.abc import Awaitable, Callable
from dataclasses import dataclass
from typing import TypeVar

from app.shared.exceptions import PlatformError

T = TypeVar("T")


class CircuitOpenError(PlatformError):
    def __init__(self, detail: str = "Circuit breaker is open") -> None:
        super().__init__(
            title="Service Unavailable",
            detail=detail,
            status=503,
            code="AI_CIRCUIT_OPEN",
            type_uri="https://acos.local/problems/ai-circuit-open",
        )


@dataclass
class CircuitBreaker:
    failure_threshold: int = 5
    recovery_timeout_seconds: float = 30.0
    failures: int = 0
    opened_at: float | None = None

    def allow(self) -> bool:
        if self.opened_at is None:
            return True
        return time.monotonic() - self.opened_at >= self.recovery_timeout_seconds

    def record_success(self) -> None:
        self.failures = 0
        self.opened_at = None

    def record_failure(self) -> None:
        self.failures += 1
        if self.failures >= self.failure_threshold:
            self.opened_at = time.monotonic()


@dataclass
class Bulkhead:
    limit: int = 32
    _in_flight: int = 0

    async def run(self, fn: Callable[[], Awaitable[T]]) -> T:
        if self._in_flight >= self.limit:
            raise PlatformError(
                title="Too Many Requests",
                detail="Bulkhead capacity exceeded",
                status=429,
                code="AI_BULKHEAD_FULL",
            )
        self._in_flight += 1
        try:
            return await fn()
        finally:
            self._in_flight -= 1


async def with_timeout(fn: Callable[[], Awaitable[T]], timeout_seconds: float) -> T:
    return await asyncio.wait_for(fn(), timeout=timeout_seconds)


async def with_retry(
    fn: Callable[[], Awaitable[T]],
    *,
    attempts: int = 3,
    base_delay_seconds: float = 0.05,
    retry_on: tuple[type[BaseException], ...] = (Exception,),
) -> tuple[T, int]:
    last_error: BaseException | None = None
    for attempt in range(1, attempts + 1):
        try:
            return await fn(), attempt - 1
        except retry_on as exc:
            last_error = exc
            if attempt >= attempts:
                break
            await asyncio.sleep(base_delay_seconds * attempt)
    assert last_error is not None
    raise last_error


async def with_circuit_breaker(
    breaker: CircuitBreaker,
    fn: Callable[[], Awaitable[T]],
) -> T:
    if not breaker.allow():
        raise CircuitOpenError()
    try:
        result = await fn()
    except Exception:
        breaker.record_failure()
        raise
    breaker.record_success()
    return result


async def with_fallback(
    fn: Callable[[], Awaitable[T]],
    fallback: Callable[[BaseException], Awaitable[T]],
) -> T:
    try:
        return await fn()
    except Exception as exc:
        return await fallback(exc)
