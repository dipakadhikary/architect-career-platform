"""Safe assistant errors. Details never include credentials or prompt text."""

from __future__ import annotations

from app.shared.exceptions import PlatformError


class AiDisabledError(PlatformError):
    def __init__(self) -> None:
        super().__init__(
            title="AI Disabled",
            detail="The ACOS assistant is disabled",
            status=503,
            code="AI_DISABLED",
            type_uri="https://acos.local/problems/ai-disabled",
        )


class ProviderNotConfiguredError(PlatformError):
    def __init__(self, detail: str = "The language model provider is not configured") -> None:
        super().__init__(
            title="Provider Not Configured",
            detail=detail,
            status=503,
            code="AI_PROVIDER_NOT_CONFIGURED",
            type_uri="https://acos.local/problems/ai-provider-not-configured",
        )


class ProviderUnavailableError(PlatformError):
    def __init__(self, detail: str = "The language model provider is unavailable") -> None:
        super().__init__(
            title="Provider Unavailable",
            detail=detail,
            status=503,
            code="AI_PROVIDER_UNAVAILABLE",
            type_uri="https://acos.local/problems/ai-provider-unavailable",
        )


class ProviderAuthenticationError(PlatformError):
    def __init__(self) -> None:
        super().__init__(
            title="Provider Authentication Failed",
            detail="The language model provider rejected the service credentials",
            status=502,
            code="AI_PROVIDER_AUTHENTICATION_FAILED",
            type_uri="https://acos.local/problems/ai-provider-authentication-failed",
        )


class ProviderUnexpectedError(PlatformError):
    def __init__(
        self,
        detail: str = "The language model provider returned an unexpected response",
    ) -> None:
        super().__init__(
            title="Provider Error",
            detail=detail,
            status=502,
            code="AI_PROVIDER_ERROR",
            type_uri="https://acos.local/problems/ai-provider-error",
        )
