# Ideia de solução: Passo 5 (testes)

> **Spoiler.** Uma forma de resolver. Tente antes.

## O que esta branch entrega

119 testes, todos rodando com `mvn test`, sem PostgreSQL instalado (só Docker ligado, por causa do Testcontainers).

## Mapa dos testes

| Classe | Camada | Ferramenta | O que prova |
|---|---|---|---|
| `NomeCompletoValidatorTest` | Regra do nome | Bean Validation pura, sem Spring | Nome opcional, sobrenome obrigatório, só letras |
| `PetServiceTest` | Service + repositório em memória | JUnit | Regras do service com dados reais |
| `PetServiceMockitoTest` | Service isolado | Mockito | O que o service manda gravar e quando **não** grava |
| `PetApiTest` | API inteira | `@SpringBootTest` + MockMvc + PostgreSQL | Cada endpoint, cada regra, cada erro, cada filtro do contrato, CORS |
| `PetControllerErroInternoTest` | Só a camada web | `@WebMvcTest` + `@MockBean` | `500` genérico, sem vazar SQL nem stack trace |
| `PetRepositoryTest` | Repositório JPA | Testcontainers | Seed da documentação, filtros, `NULL`, SQL injection |
| `JdbcPetRepositoryTest` | Repositório JDBC | Testcontainers | **Os mesmos** testes, contra a outra implementação |

## Bug novo, teste novo

Exemplo real deste projeto: o front mandava `tipo: ""` quando o usuário não escolhia nada, e a API devolvia só o erro de `tipo`, escondendo os outros. O teste `treatsEmptyTipoAndSexoAsMissingAndStillListsTheOtherErrors` foi escrito **antes** da correção, falhou, e só passou depois do ajuste no `JacksonConfig`.

## Decisões e o porquê

| Decisão | Por quê |
|---|---|
| Testcontainers em vez de H2 | A busca sem acento usa `unaccent`, que o H2 não tem |
| Um PostgreSQL só para todos os testes (`ComBancoDeTeste`) | Subir um banco por teste seria lento. O `schema.sql` recria a tabela a cada contexto |
| `@DirtiesContext` no `PetApiTest` | Cada teste começa com banco vazio, nenhum depende de outro |
| Nomes de teste em inglês, verbo na terceira pessoa | `rejectsWeightOutsideTheLimits` lê como uma frase sobre o comportamento |
| Testes parametrizados para limites | `0.4`, `0.5`, `60`, `60.1`: os bugs moram nas bordas |
