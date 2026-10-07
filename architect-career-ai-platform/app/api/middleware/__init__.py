"""Decompress gzip request bodies before routing.

Java Feign gzips JSON once the body exceeds the configured minimum size.
FastAPI does not decode Content-Encoding, so a knowledge note arrives as gzip
bytes and fails UTF-8 JSON parsing.
"""

from __future__ import annotations

import gzip
import zlib

from starlette.responses import JSONResponse
from starlette.types import ASGIApp, Message, Receive, Scope, Send

_GZIP = {b"gzip", b"x-gzip"}


class GzipRequestMiddleware:
    def __init__(self, app: ASGIApp) -> None:
        self.app = app

    async def __call__(self, scope: Scope, receive: Receive, send: Send) -> None:
        if scope["type"] != "http" or not _is_gzip(scope):
            await self.app(scope, receive, send)
            return

        compressed = await _read_body(receive)
        try:
            decoded = gzip.decompress(compressed)
        except (gzip.BadGzipFile, EOFError, zlib.error):
            response = JSONResponse(
                status_code=400,
                content={"detail": "Request body is not valid gzip"},
            )
            await response(scope, _empty_receive, send)
            return

        request_scope = dict(scope)
        request_scope["headers"] = _without_encoding(scope["headers"], len(decoded))
        await self.app(request_scope, _once(decoded), send)


def _is_gzip(scope: Scope) -> bool:
    for name, value in scope.get("headers", []):
        if name.lower() == b"content-encoding" and value.strip().lower() in _GZIP:
            return True
    return False


def _without_encoding(
    headers: list[tuple[bytes, bytes]], length: int
) -> list[tuple[bytes, bytes]]:
    kept = [
        (name, value)
        for name, value in headers
        if name.lower() not in {b"content-encoding", b"content-length"}
    ]
    kept.append((b"content-length", str(length).encode("ascii")))
    return kept


async def _read_body(receive: Receive) -> bytes:
    chunks: list[bytes] = []
    while True:
        message = await receive()
        if message["type"] == "http.disconnect":
            break
        chunks.append(message.get("body", b""))
        if not message.get("more_body", False):
            break
    return b"".join(chunks)


def _once(body: bytes) -> Receive:
    sent = False

    async def receive() -> Message:
        nonlocal sent
        if sent:
            return {"type": "http.disconnect"}
        sent = True
        return {"type": "http.request", "body": body, "more_body": False}

    return receive


async def _empty_receive() -> Message:
    return {"type": "http.disconnect"}
