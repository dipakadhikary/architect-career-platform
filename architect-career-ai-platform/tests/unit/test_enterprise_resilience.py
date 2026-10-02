"""Unit tests for resilience patterns."""

from __future__ import annotations

import pytest
from app.shared.resilience.patterns import (
    Bulkhead,
    CircuitBreaker,
    CircuitOpenError,
    with_circuit_breaker,
    with_fallback,
    with_retry,
    with_timeout,
)


@pytest.mark.asyncio
async def test_retry_recovers() -> None:
    calls = {"n": 0}

    async def flaky() -> str:
        calls["n"] += 1
        if calls["n"] < 2:
            raise RuntimeError("fail")
        return "ok"

    result, retries = await with_retry(flaky, attempts=3)
    assert result == "ok"
    assert retries >= 1


@pytest.mark.asyncio
async def test_timeout() -> None:
    async def slow() -> str:
        import asyncio

        await asyncio.sleep(0.2)
        return "late"

    with pytest.raises(TimeoutError):
        await with_timeout(slow, 0.01)


@pytest.mark.asyncio
async def test_circuit_opens() -> None:
    breaker = CircuitBreaker(failure_threshold=2, recovery_timeout_seconds=60)

    async def boom() -> str:
        raise RuntimeError("down")

    with pytest.raises(RuntimeError):
        await with_circuit_breaker(breaker, boom)
    with pytest.raises(RuntimeError):
        await with_circuit_breaker(breaker, boom)
    with pytest.raises(CircuitOpenError):
        await with_circuit_breaker(breaker, boom)


@pytest.mark.asyncio
async def test_fallback() -> None:
    async def boom() -> str:
        raise RuntimeError("x")

    async def soft(_exc: BaseException) -> str:
        return "degraded"

    result = await with_fallback(boom, soft)
    assert result == "degraded"


@pytest.mark.asyncio
async def test_bulkhead() -> None:
    bh = Bulkhead(limit=1)
    entered = False

    async def hold() -> str:
        nonlocal entered
        entered = True
        return "ok"

    assert await bh.run(hold) == "ok"
    assert entered
