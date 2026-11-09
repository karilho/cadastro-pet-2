# Ideia de solução: Passo 4 (repositório com Spring Data JPA)

> **Spoiler.** Uma forma de resolver. Tente antes.

## O que esta branch entrega

`JpaPetRepository`: a terceira implementação de `PetRepository`, agora com Spring Data JPA. É a implementação padrão a partir deste passo. O controller e o service continuam **sem nenhuma mudança** desde o Passo 1.

As três implementações convivem, escolhidas por profile:

| Profile | Implementação | Banco |
|---|---|---|
| (nenhum) | `JpaPetRepository` | PostgreSQL |
| `jdbc` | `JdbcPetRepository` | PostgreSQL |
| `memoria` | `InMemoryPetRepository` | nenhum |

```bash
mvn spring-boot:run                                   # JPA
mvn spring-boot:run -Dspring-boot.run.profiles=jdbc   # JDBC
mvn spring-boot:run -Dspring-boot.run.profiles=memoria
```

Banco e variáveis de ambiente: igual ao Passo 3.

## Decisões e o porquê

| Decisão | Por quê |
|---|---|
| `PetEntity` separada do `Pet` | O domínio não depende de JPA. Se um dia trocar de ferramenta, o `Pet` não muda |
| `ddl-auto=validate` | O `schema.sql` continua mandando. Se tabela e entidade divergirem, a aplicação **não sobe** (testado apagando a coluna `raca`) |
| `tipo`, `sexo` e `dataCadastro` com `updatable = false` | A regra "nunca muda" também fica no mapeamento |
| Busca com **Specifications** | Os filtros são todos opcionais. Specification monta a consulta só com o que veio, sem uma query por combinação |
| `sem_acento()` chamada pelo `cb.function` | Mesma regra de busca sem acento do JDBC, usando a função do banco |
| Alteração carrega a entidade e troca os campos | Dentro da transação, o JPA percebe a mudança e gera o `UPDATE` sozinho (dirty checking) |
| `open-in-view=false` | Evita consultas escondidas depois que o controller já respondeu |

## JDBC x JPA

| | JDBC | JPA |
|---|---|---|
| Linhas do repositório | ~190 em 1 arquivo | ~215 em 4 arquivos, quase sem SQL |
| SQL | Escrito à mão | Gerado (ligue `spring.jpa.show-sql=true` para ver) |
| Mapeamento linha → objeto | Método `paraPet(ResultSet)` | Anotações na `PetEntity` |
| Busca dinâmica | `StringBuilder` + lista de parâmetros | Specification |
| Transação | `setAutoCommit`, `commit`, `rollback` | `@Transactional` |
| Quando usar | Consulta muito específica ou performance crítica | CRUD do dia a dia |

O que o `save()` faz por baixo: se a entidade não tem id, gera um `INSERT` e lê o id criado pelo banco. Se tem id e está sendo gerenciada, o JPA compara o estado e gera o `UPDATE` só no fim da transação.

## Validação

97 testes passando, incluindo os **mesmos** 7 testes de repositório rodando contra JPA e contra JDBC. Front completo testado no navegador contra a versão JPA.
