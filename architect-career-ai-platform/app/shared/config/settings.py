"""Pydantic Settings for all environments."""

from __future__ import annotations

from enum import StrEnum
from functools import lru_cache
from pathlib import Path
from typing import Literal

from pydantic import Field, SecretStr, field_validator
from pydantic_settings import BaseSettings, SettingsConfigDict

_PROJECT_ROOT = Path(__file__).resolve().parents[3]
_ENV_FILE = _PROJECT_ROOT / ".env"


class AppEnvironment(StrEnum):
    DEVELOPMENT = "development"
    TEST = "test"
    STAGE = "stage"
    PRODUCTION = "production"


class AppSettings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=_ENV_FILE if _ENV_FILE.exists() else None,
        env_file_encoding="utf-8",
        extra="ignore",
        case_sensitive=False,
        env_ignore_empty=True,
    )

    app_name: str = "architect-career-ai-platform"
    app_env: AppEnvironment = AppEnvironment.DEVELOPMENT
    app_version: str = "0.1.0"
    app_debug: bool = False
    app_host: str = "0.0.0.0"
    app_port: int = 8090

    log_level: Literal["DEBUG", "INFO", "WARNING", "ERROR", "CRITICAL"] = "INFO"
    log_json: bool = True

    cors_allow_origins: str = "*"
    rate_limit_requests_per_minute: int = 120

    auth_jwt_enabled: bool = False
    auth_jwt_secret: SecretStr = SecretStr("change-me")
    auth_jwt_algorithm: str = "HS256"
    auth_jwt_audience: str = ""
    auth_jwt_issuer: str = ""
    auth_api_key_enabled: bool = False
    auth_api_keys: str = ""
    auth_internal_service_header: str = "X-Internal-Service"
    auth_internal_service_tokens: str = ""

    # # Format: redis://[:password]@host:port/db
    redis_url: str = "redis://localhost:6380/0"
    redis_enabled: bool = True

    qdrant_url: str = "http://localhost:6333"
    qdrant_api_key: SecretStr | None = None
    qdrant_enabled: bool = True

    openai_api_key: SecretStr | None = None
    openai_base_url: str = "https://api.openai.com/v1"
    openai_default_model: str = "gpt-4o-mini"
    openai_enabled: bool = True

    azure_openai_api_key: SecretStr | None = None
    azure_openai_endpoint: str = ""
    azure_openai_api_version: str = "2024-10-21"
    azure_openai_deployment: str = ""
    azure_openai_enabled: bool = False

    ollama_base_url: str = "http://localhost:11434"
    ollama_default_model: str = "llama3.2"
    ollama_enabled: bool = False

    llm_provider: Literal["openai", "azure_openai", "ollama"] = "openai"

    langfuse_public_key: str = ""
    langfuse_secret_key: SecretStr | None = None
    langfuse_host: str = "https://cloud.langfuse.com"
    langfuse_enabled: bool = False

    otel_enabled: bool = True
    otel_service_name: str = "architect-career-ai-platform"
    otel_exporter_otlp_endpoint: str = "http://localhost:4317"
    otel_traces_sampler_arg: float = Field(default=1.0, ge=0.0, le=1.0)

    metrics_enabled: bool = True

    # Knowledge / RAG
    embedding_provider: Literal[
        "openai", "azure_openai", "ollama", "bge_m3", "sentence_transformers", "hashing"
    ] = "hashing"
    embedding_model: str = "text-embedding-3-small"
    embedding_dimensions: int = 64
    azure_embedding_deployment: str = ""
    ollama_embedding_model: str = "nomic-embed-text"
    bge_m3_model: str = "BAAI/bge-m3"
    sentence_transformers_model: str = "sentence-transformers/all-MiniLM-L6-v2"

    vector_store_provider: Literal["qdrant", "memory"] = "memory"
    qdrant_collection: str = "acos_knowledge"

    chunking_strategy: Literal["recursive", "token", "sentence", "markdown", "semantic"] = (
        "recursive"
    )
    chunk_size: int = 800
    chunk_overlap: int = 120

    retrieval_mode: Literal["dense", "keyword", "hybrid"] = "dense"
    retrieval_score_threshold: float | None = None
    summarize_top_k: int = 5
    summarize_max_length: int = 800
    summarize_prompt_version: str = "v1"
    context_max_tokens: int = 3000
    prompts_root: str = "prompts/knowledge"

    reranker_provider: Literal["none", "identity", "cross_encoder", "bge", "cohere"] = "identity"
    cross_encoder_model: str = "cross-encoder/ms-marco-MiniLM-L-6-v2"
    cohere_api_key: SecretStr | None = None
    cohere_rerank_model: str = "rerank-english-v3.0"

    # Agentic AI
    agentic_prompts_root: str = "prompts/agentic"
    agentic_default_workflow: str = "question_answering"

    # Enterprise production controls
    guardrails_enabled: bool = True
    guardrails_pii_redaction_enabled: bool = True
    guardrails_max_input_chars: int = 20000
    prompt_require_approved: bool = False
    tenant_isolation_enabled: bool = False
    semantic_cache_enabled: bool = True
    semantic_cache_ttl_seconds: int = 3600
    semantic_similarity_threshold: float = 0.92

    routing_policy: str = "lowest_cost"
    routing_preferred_provider: str = ""
    routing_fallback_provider: str = "extractive"
    routing_capability_providers: str = ""

    policy_max_cost_usd: float = 5.0
    policy_max_tokens: int = 100000
    policy_max_latency_ms: float = 0.0
    policy_allowed_providers: str = ""
    policy_allowed_models: str = ""
    policy_allowed_capabilities: str = ""
    policy_restricted_tools: str = ""
    policy_restricted_prompts: str = ""

    cost_openai_per_1k_tokens: float = 0.15
    cost_azure_per_1k_tokens: float = 0.14
    cost_ollama_per_1k_tokens: float = 0.0

    resilience_retry_attempts: int = 2
    resilience_timeout_seconds: float = 30.0
    resilience_circuit_failure_threshold: int = 5
    resilience_circuit_recovery_seconds: float = 30.0
    resilience_bulkhead_limit: int = 64
    resilience_fallback_enabled: bool = True

    rate_limit_exempt_paths: str = (
        "/api/v1/ai/health,/api/v1/system/liveness,"
        "/api/v1/system/readiness,/api/v1/system/metrics,/docs,/redoc,/openapi.json"
    )

    @field_validator("app_env", mode="before")
    @classmethod
    def normalize_env(cls, value: object) -> object:
        if isinstance(value, str):
            return value.lower()
        return value

    @field_validator("retrieval_score_threshold", mode="before")
    @classmethod
    def empty_optional_float(cls, value: object) -> object:
        if value == "" or value is None:
            return None
        return value

    @property
    def cors_origins(self) -> list[str]:
        if self.cors_allow_origins.strip() == "*":
            return ["*"]
        return [item.strip() for item in self.cors_allow_origins.split(",") if item.strip()]

    @property
    def api_key_set(self) -> set[str]:
        return {item.strip() for item in self.auth_api_keys.split(",") if item.strip()}

    @property
    def internal_service_token_set(self) -> set[str]:
        return {
            item.strip() for item in self.auth_internal_service_tokens.split(",") if item.strip()
        }

    @property
    def is_production(self) -> bool:
        return self.app_env == AppEnvironment.PRODUCTION

    @property
    def policy_allowed_providers_set(self) -> set[str]:
        return {item.strip() for item in self.policy_allowed_providers.split(",") if item.strip()}

    @property
    def policy_allowed_models_set(self) -> set[str]:
        return {item.strip() for item in self.policy_allowed_models.split(",") if item.strip()}

    @property
    def policy_allowed_capabilities_set(self) -> set[str]:
        return {
            item.strip() for item in self.policy_allowed_capabilities.split(",") if item.strip()
        }

    @property
    def policy_restricted_tools_set(self) -> set[str]:
        return {item.strip() for item in self.policy_restricted_tools.split(",") if item.strip()}

    @property
    def policy_restricted_prompts_set(self) -> set[str]:
        return {item.strip() for item in self.policy_restricted_prompts.split(",") if item.strip()}

    @property
    def rate_limit_exempt_path_set(self) -> set[str]:
        return {item.strip() for item in self.rate_limit_exempt_paths.split(",") if item.strip()}

    @property
    def routing_capability_provider_map(self) -> dict[str, str]:
        mapping: dict[str, str] = {}
        for item in self.routing_capability_providers.split(","):
            if ":" not in item:
                continue
            capability, provider = item.split(":", 1)
            mapping[capability.strip()] = provider.strip()
        return mapping

    def validate_for_runtime(self) -> None:
        if not self.is_production:
            return
        if self.auth_jwt_secret.get_secret_value() == "change-me":
            raise ValueError("Production JWT secret must be configured")
        if not any(
            [
                self.auth_jwt_enabled,
                self.auth_api_key_enabled,
                bool(self.internal_service_token_set),
            ]
        ):
            raise ValueError("Production requires authentication to be enabled")
        if self.app_debug:
            raise ValueError("Production must disable APP_DEBUG")
        if self.cors_allow_origins.strip() == "*":
            raise ValueError("Production must not use CORS allow-origins=*")
        if self.langfuse_enabled and (not self.langfuse_public_key or not self.langfuse_secret_key):
            raise ValueError("LangFuse enabled without credentials")


@lru_cache(maxsize=1)
def get_settings() -> AppSettings:
    return AppSettings()
