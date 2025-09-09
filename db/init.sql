CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE IF NOT EXISTS divida(
    id BIGSERIAL PRIMARY KEY,
    identificador UUID NOT NULL UNIQUE,
    valor NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_divida_created_at ON divida (created_at);