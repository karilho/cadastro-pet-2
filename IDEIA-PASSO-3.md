# Ideia de solução: Passo 3 (repositório com JDBC puro)

> **Spoiler.** Uma forma de resolver. Tente antes.

## O que esta branch entrega

`JdbcPetRepository`: uma nova implementação de `PetRepository` que grava no PostgreSQL com JDBC puro. O controller e o service **não mudaram nenhuma linha** em relação ao Passo 1. Os pets agora sobrevivem ao reinício da aplicação.

## Como rodar

```bash
# 1. banco (uma vez): schema do Passo 2
psql -U <usuario> -d cadastro_pet -f database/schema.sql

# 2. variáveis de ambiente (ver .env.example)
export DB_URL=jdbc:postgresql://localhost:5432/cadastro_pet
export DB_USER=<usuario>
export DB_PASSWORD=<senha>

# 3. backend e front
mvn spring-boot:run
python3 -m http.server 5500 --directory frontend
```

Quer rodar sem banco, como no Passo 1? `mvn spring-boot:run -Dspring-boot.run.profiles=memoria`.

Testes: `mvn test` (precisa do **Docker** ligado: o Testcontainers sobe um PostgreSQL descartável).

> O `schema.sql` usa a extensão `unaccent`. Se o seu usuário da aplicação não puder criar extensão, rode `CREATE EXTENSION unaccent;` uma vez como administrador.

## Decisões e o porquê

| Decisão | Por quê |
|---|---|
| Recebe o `DataSource` do Spring | O Spring Boot já configura o pool de conexões a partir de `spring.datasource.*`. O resto é JDBC puro |
| `@Profile("!memoria")` e `@Profile("memoria")` | Duas implementações da mesma interface, e o Spring escolhe uma pelo profile |
| `PreparedStatement` com `?` sempre, inclusive na busca dinâmica | O SQL é montado só com os filtros usados, mas nenhum valor do usuário entra no texto do SQL |
| `try-with-resources` em tudo | Conexão, comando e resultado fecham sozinhos, mesmo com erro |
| Busca de texto com `sem_acento(...) LIKE '%' \|\| sem_acento(?) \|\| '%'` | Reaproveita a função do Passo 2, e o `%` fica no SQL, não concatenado no valor |
| Transação explícita na alteração | `commit` se deu certo, `rollback` se falhou |
| `ErroDeBancoException` | Erro técnico, separado de regra de negócio. O tratador transforma em `500` sem vazar detalhe |
| Testcontainers em vez de H2 | A busca sem acento depende do PostgreSQL. Teste no mesmo banco que roda de verdade |
| `testcontainers.version` 1.21.4 no `pom.xml` | Docker 29 ou superior recusa as versões antigas que vêm com o Spring Boot 3.3 |

## Validação

90 testes passando: os 83 do Passo 1, agora rodando contra o PostgreSQL, mais 7 do repositório (seed da documentação, filtros, `NULL`, SQL injection, alteração que não mexe em tipo e sexo, exclusão). O front completo foi testado no navegador contra esta API com banco, incluindo reiniciar a API e conferir que os pets continuaram lá.
