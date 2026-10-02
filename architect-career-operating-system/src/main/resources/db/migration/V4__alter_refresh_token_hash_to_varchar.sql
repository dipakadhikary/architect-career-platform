-- Align refresh token hash column with JPA VARCHAR mapping.

ALTER TABLE acos.refresh_tokens
    ALTER COLUMN token_hash TYPE VARCHAR(64);
