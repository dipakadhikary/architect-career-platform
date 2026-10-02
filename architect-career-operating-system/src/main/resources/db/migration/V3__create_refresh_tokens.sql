-- Refresh token persistence for opaque token rotation/revocation.

CREATE TABLE acos.refresh_tokens (
    id           UUID                     NOT NULL,
    user_id      UUID                     NOT NULL,
    token_hash   CHAR(64)                 NOT NULL,
    expires_at   TIMESTAMPTZ              NOT NULL,
    revoked      BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ              NOT NULL,
    updated_at   TIMESTAMPTZ              NOT NULL,
    version      BIGINT                   NOT NULL,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_user_id FOREIGN KEY (user_id)
        REFERENCES acos.users (id)
);

CREATE INDEX ix_refresh_tokens_user_id ON acos.refresh_tokens (user_id);
CREATE INDEX ix_refresh_tokens_expires_at ON acos.refresh_tokens (expires_at);

COMMENT ON TABLE acos.refresh_tokens IS 'Opaque refresh tokens stored as SHA-256 hashes';
