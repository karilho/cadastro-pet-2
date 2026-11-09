-- Cadastro de Pets V2: modelagem (PostgreSQL 15+)
-- Pode rodar de novo sem quebrar: apaga e recria.

CREATE EXTENSION IF NOT EXISTS unaccent;

-- unaccent() não é IMMUTABLE no PostgreSQL. Este invólucro é, e por isso pode ser usado em índice.
CREATE OR REPLACE FUNCTION sem_acento(texto TEXT) RETURNS TEXT
    LANGUAGE sql IMMUTABLE PARALLEL SAFE
    AS $$ SELECT lower(public.unaccent('public.unaccent', texto)) $$;

DROP TABLE IF EXISTS pet;

CREATE TABLE pet (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome_completo  VARCHAR(120),
    tipo           VARCHAR(10)   NOT NULL CHECK (tipo IN ('CACHORRO', 'GATO')),
    sexo           VARCHAR(10)   NOT NULL CHECK (sexo IN ('MACHO', 'FEMEA')),
    numero_casa    VARCHAR(10)   CHECK (numero_casa ~ '^[0-9]+$'),
    cidade         VARCHAR(80)   NOT NULL CHECK (btrim(cidade) <> ''),
    rua            VARCHAR(120)  NOT NULL CHECK (btrim(rua) <> ''),
    idade_anos     NUMERIC(4, 2) CHECK (idade_anos > 0 AND idade_anos <= 20),
    peso_kg        NUMERIC(5, 2) CHECK (peso_kg >= 0.5 AND peso_kg <= 60),
    raca           VARCHAR(60),
    data_cadastro  TIMESTAMP     NOT NULL DEFAULT now()
);

-- A busca é por parte do texto, sem acento e sem caixa: o índice é sobre a expressão usada na busca.
CREATE INDEX idx_pet_nome_sem_acento ON pet (sem_acento(nome_completo));
CREATE INDEX idx_pet_raca_sem_acento ON pet (sem_acento(raca));
CREATE INDEX idx_pet_tipo ON pet (tipo);
