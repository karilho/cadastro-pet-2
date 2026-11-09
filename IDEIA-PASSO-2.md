# Ideia de solução: Passo 2 (modelagem SQL)

> **Spoiler.** Uma forma de resolver. Tente antes.

## O que esta branch entrega

A pasta `database/` com `schema.sql`, `seed.sql` e `queries.sql` para PostgreSQL 15+. O código Java é o mesmo do Passo 1: a API continua em memória. O banco só passa a ser usado no Passo 3.

## Como rodar

```bash
psql -U <usuario> -d cadastro_pet -f database/schema.sql
psql -U <usuario> -d cadastro_pet -f database/seed.sql
psql -U <usuario> -d cadastro_pet -f database/queries.sql
```

Cada consulta do `queries.sql` tem no comentário o resultado esperado, igual à tabela de `docs/03-modelo-de-dados.md`.

## Decisões e o porquê

| Decisão | Por quê |
|---|---|
| `BIGINT GENERATED ALWAYS AS IDENTITY` | Forma padrão do PostgreSQL para chave auto incremento. `ALWAYS` impede que alguém force um id na mão |
| `tipo` e `sexo` como `VARCHAR` + `CHECK` | Mais simples de evoluir do que um `ENUM` do PostgreSQL, e o JPA (Passo 4) mapeia direto como texto |
| `NUMERIC(4,2)` e `NUMERIC(5,2)` | Número exato. Ponto flutuante erra casas decimais |
| `CHECK` em idade, peso, número da casa, cidade e rua | O banco também se defende de um `INSERT` direto, fora do Java |
| Campos opcionais sem `NOT NULL` | Não informado é `NULL`. E `NULL` passa em qualquer `CHECK`, que é o que queremos |
| Extensão `unaccent` + função `sem_acento` | O PostgreSQL não ignora acento sozinho. A função é `IMMUTABLE` para poder entrar em índice |
| Índice sobre `sem_acento(nome_completo)` e `sem_acento(raca)` | O índice precisa ser sobre a **mesma expressão** usada na busca |
| `data_cadastro DEFAULT now()` | O banco preenche, o usuário nunca informa |

## Sobre índice e `LIKE '%texto%'`

Índice B-tree ajuda em igualdade e em `LIKE 'texto%'` (começa com). Em `LIKE '%texto%'` (contém) ele não ajuda: o banco precisa olhar todas as linhas. Para "contém" em tabela grande, o caminho é o índice trigram (`pg_trgm`), assunto para depois. Rode os dois `EXPLAIN` do final do `queries.sql` e compare.

## Validação

Rodado num PostgreSQL 16: o `schema.sql` roda duas vezes seguidas sem erro, o seed carrega 10 pets, cada consulta devolve exatamente os ids esperados, e o banco recusa peso `61` e idade `0`, mas aceita um pet sem os campos opcionais.
