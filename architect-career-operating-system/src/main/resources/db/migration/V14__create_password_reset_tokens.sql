-- Single-use password reset tokens. Only the SHA-256 hash is stored.

CREATE TABLE acos.password_reset_tokens (
    id           UUID                     NOT NULL,
    user_id      UUID                     NOT NULL,
    token_hash   VARCHAR(64)              NOT NULL,
    purpose      VARCHAR(32)              NOT NULL,
    expires_at   TIMESTAMPTZ              NOT NULL,
    used_at      TIMESTAMPTZ,
    created_at   TIMESTAMPTZ              NOT NULL,
    updated_at   TIMESTAMPTZ              NOT NULL,
    version      BIGINT                   NOT NULL,
    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id),
    CONSTRAINT uk_password_reset_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_password_reset_tokens_user_id FOREIGN KEY (user_id)
        REFERENCES acos.users (id),
    CONSTRAINT ck_password_reset_tokens_purpose CHECK (purpose = 'PASSWORD_RESET')
);

CREATE INDEX ix_password_reset_tokens_user_id ON acos.password_reset_tokens (user_id);
CREATE INDEX ix_password_reset_tokens_expires_at ON acos.password_reset_tokens (expires_at);

COMMENT ON TABLE acos.password_reset_tokens IS
    'Password reset tokens stored as SHA-256 hashes. Raw tokens are emailed once.';
