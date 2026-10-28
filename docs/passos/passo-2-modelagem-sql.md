# Passo 2: Modelagem SQL

Antes de escrever Java para o banco, desenhe o banco. Aqui você só escreve SQL e roda no PostgreSQL. Quase nada vem pronto, de propósito.

## Antes de começar

- Banco `cadastro_pet` e usuário da aplicação funcionando: [02-ambiente-e-banco.md](../02-ambiente-e-banco.md)
- Guia, Parte 2, seção **SQL**: fundamentos, `WHERE`, agregações, `GROUP BY` e `INNER JOIN`
- O que modelar: [03-modelo-de-dados.md](../03-modelo-de-dados.md), seções 1 a 3

## O que entregar

Três arquivos na pasta `database/`:

| Arquivo | Conteúdo |
|---|---|
| `schema.sql` | A modelagem: tabelas, restrições e índices |
| `seed.sql` | Os 10 pets de exemplo |
| `queries.sql` | As consultas da lista abaixo |

## Requisitos

### `schema.sql`

1. Modele a entidade Pet da [seção 1 do modelo](../03-modelo-de-dados.md#1-entidade-pet).
2. Escolha chave, tipos e restrições respondendo as perguntas da [seção 3 do modelo](../03-modelo-de-dados.md#3-decisões-que-são-suas).
3. O banco deve **recusar** dado que viole as regras que ele consegue garantir.
4. Crie índices onde fizerem sentido e saiba justificar cada um.
5. O script precisa poder rodar de novo, sem quebrar.

### `seed.sql`

1. Insira os 10 pets da [tabela de dados de exemplo](../03-modelo-de-dados.md#8-dados-de-exemplo-seed), com os `NULL` nos lugares certos.

### `queries.sql`

Uma consulta para cada linha da [tabela de resultados esperados](../03-modelo-de-dados.md#resultados-esperados), cada uma com um comentário dizendo o que faz. Mínimo obrigatório:

1. Listar todos os pets
2. Buscar por **parte** do nome, ignorando caixa e acento
3. Buscar por raça
4. Buscar por faixa de idade
5. Quantidade de pets por tipo
6. Peso médio por tipo e sexo

### Regra nível 2 (não obrigatória)

1. Modele o [endereço como entidade própria](../03-modelo-de-dados.md#5-regra-nível-2-endereço-separado) ligada ao pet.
2. Reescreva a listagem usando `INNER JOIN` e depois `LEFT JOIN`.
3. Insira um pet sem endereço e descreva num comentário a diferença que você viu entre os dois.

## Pronto quando

- [ ] `schema.sql`, `seed.sql` e `queries.sql` rodam do zero no PostgreSQL sem erro
- [ ] Cada consulta devolve exatamente o resultado esperado da tabela
- [ ] A busca por `siames` acha `Siamês`
- [ ] O banco recusa um pet com peso `61`
- [ ] O banco recusa um pet com idade `0`
- [ ] O banco aceita um pet sem raça, sem peso e sem idade
- [ ] Você explica a diferença entre `LIKE 'Flor%'` e `LIKE '%Flor%'` olhando o plano de execução (`EXPLAIN`)

## Dicas

- Faça a busca sem acento e sem caixa. Se o seu banco não resolve isso sozinho, pesquise como o PostgreSQL trata esse caso.
- Confira a média na mão com 2 ou 3 linhas antes de confiar na consulta.
- `NULL` não é igual a nada, nem a outro `NULL`. Teste `= NULL` e depois `IS NULL`.
- Apagou o pet de teste? Confira que o seed continua com 10.

## Erros comuns

- Usar ponto flutuante para peso e idade.
- Marcar tudo como obrigatório e depois não conseguir cadastrar pet sem raça.
- Esquecer de rodar o `seed.sql` depois de recriar as tabelas.
- Criar índice em tudo "por garantia".

## Commit sugerido

```
feat: add database schema, seed and queries
```
