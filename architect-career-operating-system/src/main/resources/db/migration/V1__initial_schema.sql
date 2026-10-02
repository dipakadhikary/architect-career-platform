-- ACOS infrastructure foundation.
-- Business domain tables are intentionally deferred to later migrations.

CREATE SCHEMA IF NOT EXISTS acos;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

COMMENT ON SCHEMA acos IS 'Architect Career Operating System';
