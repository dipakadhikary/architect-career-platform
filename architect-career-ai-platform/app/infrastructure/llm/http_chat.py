"""Shared HTTP call used by chat providers. Retries only transient failures."""

from __future__ import annotations

import httpx

from app.intelligence.assistant.errors import (
    ProviderAuthenticationError,
    ProviderUnavailableError,
    ProviderUnexpectedError,
)
from app.shared.exceptions import RateLimitError, UpstreamTimeoutError

_RETRYABLE_STATUS = frozenset({502, 503, 504})


async def post_json(
    *,
    url: str,
    headers: dict[str, str],
    payload: dict[str, object],
    timeout_seconds: float,
    max_attempts: int,
    transport: httpx.AsyncBaseTransport | None = None,
) -> httpx.Response:
    """POST JSON with a hard timeout. Connect failures and 502/503/504 retry within the cap."""
    last_transport_error: Exception | None = None
    response: httpx.Response | None = None
    attempts = max(1, max_attempts)
    for attempt in range(1, attempts + 1):
        try:
            async with httpx.AsyncClient(timeout=timeout_seconds, transport=transport) as client:
                response = await client.post(url, headers=headers, json=payload)
        except httpx.TimeoutException as exc:
            raise UpstreamTimeoutError("The language model provider timed out") from exc
        except httpx.TransportError as exc:
            last_transport_error = exc
            if attempt == attempts:
                raise ProviderUnavailableError(
                    "The language model provider is unavailable"
                ) from exc
            continue
        if response.status_code in _RETRYABLE_STATUS and attempt < attempts:
            continue
        return response
    raise ProviderUnavailableError(
        "The language model provider is unavailable"
    ) from last_transport_error


def map_status(response: httpx.Response) -> None:
    """Translate provider HTTP status into a safe platform error."""
    status = response.status_code
    if status in {401, 403}:
        raise ProviderAuthenticationError()
    if status == 429:
        raise RateLimitError(detail="The language model provider rate limit was reached")
    if status in _RETRYABLE_STATUS:
        raise ProviderUnavailableError("The language model provider is unavailable")
    if status >= 400:
        raise ProviderUnexpectedError("The language model provider rejected the request")
