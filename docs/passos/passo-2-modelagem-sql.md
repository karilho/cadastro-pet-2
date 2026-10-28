# Passo 2: Modelagem SQL

Antes de escrever Java para o banco, desenhe o banco. Aqui você só escreve SQL e roda no MySQL.

## Antes de começar

- Banco `cadastro_pet` criado e usuário `pet_app` funcionando: [02-ambiente-e-banco.md](../02-ambiente-e-banco.md)
- Guia, Parte 2, seção **SQL**: fundamentos, `WHERE`, agregações, `GROUP BY` e `INNER JOIN`
- Contrato da tabela: [03-modelo-de-dados.md](../03-modelo-de-dados.md), seções 1 e 5

## O que entregar

Três arquivos na pasta `database/`:

| Arquivo | Conteúdo |
|---|---|
| `schema.sql` | Criação da tabela `pet`, restrições e índices |
| `seed.sql` | Os 10 pets de exemplo |
| `queries.sql` | As consultas da lista abaixo |

## Requisitos

### `schema.sql`

1. Crie a tabela `pet` seguindo o [contrato de colunas](../03-modelo-de-dados.md#contrato-das-colunas).
2. Chave primária com auto incremento.
3. `NOT NULL` apenas onde o contrato diz que não aceita nulo.
4. `CHECK` para idade (maior que 0 e até 20) e peso (de 0.5 a 60).
5. `data_cadastro` com padrão `CURRENT_TIMESTAMP`.
6. Pelo menos dois índices: `nome_completo` e `raca`.
7. O script precisa rodar de novo sem quebrar (pense em `DROP TABLE IF EXISTS` ou `CREATE TABLE IF NOT EXISTS`).

### `seed.sql`

1. Insira EXATAMENTE os 10 pets da [tabela de dados de exemplo](../03-modelo-de-dados.md#5-dados-de-exemplo-seed).
2. Mantenha os `NULL` onde a tabela mostra `NULL`.

### `queries.sql`

Uma consulta para cada linha da [tabela de resultados esperados](../03-modelo-de-dados.md#resultados-esperados), cada uma com um comentário dizendo o que faz. Mínimo obrigatório:

1. Listar todos os pets
2. Buscar por **parte** do nome, ignorando caixa e acento
3. Buscar por raça
4. Buscar por faixa de idade
5. Quantidade de pets por tipo (`GROUP BY`)
6. Peso médio por tipo e sexo (`GROUP BY` com dois campos)

### Regra nível 2 (não obrigatória)

1. Crie a tabela `endereco` ([modelo, seção 2](../03-modelo-de-dados.md#2-tabela-endereco-regra-nível-2)) e ligue ao pet por chave estrangeira.
2. Reescreva a listagem com `INNER JOIN`.
3. Insira um pet sem endereço e reescreva com `LEFT JOIN`. Descreva, num comentário, a diferença que você viu no resultado.

## Pronto quando

- [ ] `schema.sql`, `seed.sql` e `queries.sql` rodam do zero no MySQL sem erro
- [ ] Cada consulta devolve exatamente o resultado esperado da tabela
- [ ] A busca por `siames` acha `Siamês` sem nenhum tratamento extra no SQL
- [ ] Inserir um pet com peso `61` é recusado pelo banco
- [ ] Inserir um pet com idade `0` é recusado pelo banco
- [ ] Você explica a diferença entre `LIKE 'Flor%'` e `LIKE '%Flor%'` usando `EXPLAIN`

## Como testar na mão

```sql
-- deve falhar (peso fora do limite)
INSERT INTO pet (tipo, sexo, cidade, rua, peso_kg) VALUES ('GATO', 'MACHO', 'Curitiba', 'Rua X', 61);

-- deve funcionar (idade e peso são opcionais)
INSERT INTO pet (tipo, sexo, cidade, rua) VALUES ('GATO', 'MACHO', 'Curitiba', 'Rua X');
```

Apague o pet de teste depois (`DELETE`), para o seed continuar com 10.

## Dicas

- Rode o `schema.sql` pelo terminal: `mysql -u pet_app -p cadastro_pet < database/schema.sql`.
- A collation do banco já resolve caixa e acento. Faça a consulta sem `LOWER()` e sem `REPLACE()` primeiro. Se funcionar, você entendeu a collation.
- `GROUP BY` com média: confira o resultado na mão com 2 ou 3 linhas antes de confiar.
- `NULL` não é igual a nada, nem a outro `NULL`. Teste `= NULL` e depois `IS NULL`.

## Erros comuns

- Usar `FLOAT` para peso.
- Colocar `NOT NULL` em tudo e depois não conseguir cadastrar pet sem raça.
- `LIKE '%flor%'` com `LOWER()` em coluna indexada sem entender por que o índice não ajuda.
- Esquecer de rodar o `seed.sql` depois de recriar a tabela.

## Commit sugerido

```
feat: add database schema, seed and queries
```
