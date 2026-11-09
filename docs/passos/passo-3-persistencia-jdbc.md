# Passo 3: Repositório com JDBC puro

Troque o armazenamento em memória do Passo 1 por um que grava no PostgreSQL, **sem framework de persistência**. Você escreve cada SQL e vê cada conexão abrir e fechar.

## Antes de começar

- Passos 1 e 2 concluídos (API em memória e banco modelado com seed)
- Guia, Parte 2, seção **Maven + JDBC**: as aulas de JDBC do DevDojo
- Variáveis de ambiente configuradas: [02, seção 3](../02-ambiente-e-banco.md#3-configuração-por-variáveis-de-ambiente)
- Modelo e dados de exemplo: [03-modelo-de-dados.md](../03-modelo-de-dados.md)

## O que entregar

- Driver JDBC do PostgreSQL no `pom.xml`
- Uma nova implementação de `PetRepository` usando JDBC (`JdbcPetRepository`, por exemplo)
- A aplicação usando essa implementação no lugar da em memória
- Teste do repositório contra um PostgreSQL de verdade

## A regra de ouro deste passo

**O controller e o service não mudam.** Nenhuma linha. Você só cria uma classe nova que implementa a mesma interface do Passo 1. Se precisou mexer no service, a separação do Passo 1 vazou detalhe de armazenamento: corrija lá.

## Requisitos

### Conexão
1. URL, usuário e senha vêm de **variáveis de ambiente** (`DB_URL`, `DB_USER`, `DB_PASSWORD`). Nada disso no código nem no Git.
2. Use o `DataSource` que o Spring Boot já configura a partir de `spring.datasource.*` (starter `jdbc`). Você recebe o `DataSource` e abre a conexão com JDBC puro.
3. Falha de banco vira exceção **técnica**, que o tratador de erros transforma em `500` sem vazar detalhe.

### Operações

| Operação da interface | Comportamento no banco |
|---|---|
| Salvar (novo) | `INSERT` e devolve o pet com o `id` gerado pelo banco |
| Salvar (existente) | `UPDATE` dos campos editáveis. **Nunca** altera `tipo`, `sexo`, `id` ou data de cadastro |
| Buscar por id | `SELECT` por id, devolve vazio se não existir |
| Buscar com filtro | `SELECT` montado só com os filtros usados, ordenado por `id` |
| Excluir | `DELETE` por id, informa se algo foi removido |

### Regras de qualidade (reprovam se faltarem)
1. **`PreparedStatement` SEMPRE.** Nenhum valor concatenado dentro de SQL.
2. **`try-with-resources`** em toda `Connection`, `PreparedStatement` e `ResultSet`.
3. `NULL` do banco vira `null` no `Pet`, nunca `0` nem texto vazio.
4. A busca monta o SQL dinamicamente, mas **os valores continuam em parâmetros `?`**.
5. Busca de texto ignora caixa e acento **no SQL**, reaproveitando a solução do Passo 2.

### Teste do repositório
6. Teste a implementação JDBC contra um PostgreSQL **real**, subido pelo próprio teste com **Testcontainers** (precisa do Docker instalado).
7. O teste cria o schema com o `schema.sql` do Passo 2 e confere os resultados esperados do [seed](../03-modelo-de-dados.md#resultados-esperados).

### Regra nível 2 (não obrigatória)
8. Alteração dentro de uma **transação** explícita (`setAutoCommit(false)`, `commit`, `rollback`).

## Pronto quando

- [ ] O front funciona igual ao Passo 1, mas os pets **sobrevivem** a um reinício da aplicação
- [ ] O diff do passo não toca em controller nem service
- [ ] A busca por `siames`, `FLOR` e `silva` devolve os ids esperados
- [ ] Nenhuma query usa `+` para juntar valor do usuário
- [ ] `grep` não acha senha nem URL de banco no código
- [ ] `mvn test` verde (com Docker ligado)

## Dicas

- Busque o nome `' OR '1'='1`. Com `PreparedStatement` nada acontece. Com concatenação, você vê o problema.
- `ResultSet.getBigDecimal` devolve `null` para coluna `NULL`. `getInt` e `getDouble` devolvem `0`.
- Id gerado: `Statement.RETURN_GENERATED_KEYS`.
- Um método só transforma `ResultSet` em `Pet`. Você usa em toda busca.
- Duas implementações da mesma interface: o Spring precisa saber qual usar. Pesquise `@Primary` ou `@Profile`.

## Erros comuns

- Conexão aberta e nunca fechada.
- `catch (SQLException e) {}` vazio.
- Regra de negócio (idade máxima) dentro do repositório.

## Ideia de solução

Branch [`ideia/passo-3`](https://github.com/karilho/cadastro-pet-2/tree/ideia/passo-3).

## Commit sugerido

```
feat: add jdbc pet repository
```
