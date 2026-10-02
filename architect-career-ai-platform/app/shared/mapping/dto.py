"""Helpers for mapping generated Pydantic contract models."""

from __future__ import annotations

from typing import Any, TypeVar

from pydantic import BaseModel

T = TypeVar("T", bound=BaseModel)


def parse_model(model_type: type[T], data: dict[str, Any]) -> T:
    return model_type.model_validate(data)


def dump_model(
    model: BaseModel, *, by_alias: bool = True, exclude_none: bool = True
) -> dict[str, Any]:
    return model.model_dump(by_alias=by_alias, exclude_none=exclude_none)
