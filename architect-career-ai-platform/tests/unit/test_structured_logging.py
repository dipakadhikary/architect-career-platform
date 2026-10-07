"""Structured logs include the request correlation id."""

from __future__ import annotations

import json
import logging
from io import StringIO

from app.shared.context.request_context import RequestContext, request_context_var
from app.shared.logging.setup import configure_logging


def test_stdlib_log_includes_the_bound_correlation_id() -> None:
    root = logging.getLogger()
    previous_handlers = list(root.handlers)
    previous_level = root.level
    stream = StringIO()
    token = request_context_var.set(
        RequestContext.create(correlation_id="java-correlation-1", request_id="req-1")
    )
    try:
        configure_logging(level="INFO", json_logs=True)
        root.handlers[0].setStream(stream)
        logging.getLogger("app.api.exceptions").warning(
            "request_validation_failed",
            extra={"errors": [{"field": "body.goal"}]},
        )
        payload = json.loads(stream.getvalue())
    finally:
        request_context_var.reset(token)
        root.handlers.clear()
        for handler in previous_handlers:
            root.addHandler(handler)
        root.setLevel(previous_level)

    assert payload["event"] == "request_validation_failed"
    assert payload["correlation_id"] == "java-correlation-1"
    assert payload["request_id"] == "req-1"
