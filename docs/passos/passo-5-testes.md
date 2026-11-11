# Passo 5: Testes

Sistema sem teste é sistema que ninguém tem coragem de mexer. Nos Passos 1, 3 e 4 você já escreveu testes mínimos. Aqui você fecha a cobertura e garante que nada quebre.

## Antes de começar

- Passos 1 a 4 concluídos
- Guia, Parte 2, seção **Web em Java**, subseção **Testes automatizados**
- Regras: [01-regras-de-negocio.md](../01-regras-de-negocio.md)
- Contrato da API: [04-contrato-da-api.md](../04-contrato-da-api.md)
- Dados fixos para os testes: [seed](../03-modelo-de-dados.md#8-dados-de-exemplo-seed)

## O que entregar

- Testes de regra de negócio (JUnit 5)
- Testes do service (Mockito)
- Testes do controller (`@WebMvcTest`)
- Testes do repository contra PostgreSQL real (Testcontainers)
- `mvn test` passando em uma máquina limpa, só com Java e Docker (**sem PostgreSQL instalado**)

## Requisitos

### Tipos de teste

| Camada | Ferramenta | O que prova |
|---|---|---|
| Domínio e regras | JUnit 5 | Cada regra de negócio |
| Service | JUnit 5 + Mockito | Orquestração, com o repository isolado (mock) |
| Controller | `@WebMvcTest` + `MockMvc` | Status, corpo e formato de erro de cada endpoint |
| Repository | Testcontainers + PostgreSQL | Consultas e filtros no banco de verdade |

### Cobertura mínima obrigatória

Para **cada linha** das tabelas "válido/erro" da [seção 2 das regras](../01-regras-de-negocio.md#2-regras-por-campo), existe pelo menos um teste. Resumo do que precisa ter:

| Regra | Testes mínimos |
|---|---|
| Nome | válido, em branco, sem sobrenome, com número, com símbolo |
| Tipo e sexo | cada valor válido, valor inválido, caixa diferente |
| Número da casa | só dígitos, com letra |
| Idade | `3`, `3.5`, `0.5`, limite `20`, `20.1`, `0`, negativo, texto (`"3,5"`) |
| Peso | `5`, limite `0.5`, limite `60`, `0.4`, `60.1`, texto |
| Raça | válida, com hífen, com número, com símbolo |
| Campos opcionais | os cinco em branco voltam `null` |
| Alterar | tipo e sexo preservados |

Para a API, além disso:

- Cada endpoint, no caminho feliz, com o **status** certo.
- Cada tipo de erro (`400` de validação, `400` de JSON malformado, `404`) no **formato padrão**.
- Os exemplos da [tabela de filtros](../04-contrato-da-api.md#22-get-pets-listar-e-buscar) devolvendo os ids esperados.

### Estilo

1. Teste **comportamento**, não implementação. O nome do teste diz o que o sistema faz.
2. Use verbo na terceira pessoa. Exemplos:
   - `rejects pet weighing more than 60kg`
   - `accepts weight with comma as decimal separator`
   - `returns 404 when pet does not exist`
3. Organize por funcionalidade (`@Nested` ou classes separadas por camada).
4. Um teste, uma razão para falhar.
5. Teste independente: um não pode depender do resultado de outro.

### Banco nos testes

1. O teste do repository sobe um PostgreSQL descartável com **Testcontainers**. Nunca o seu banco de desenvolvimento.
2. Cada teste começa com os dados que ele mesmo precisa. Nada de depender do que "já estava lá".
3. Por que não H2? Porque a busca sem acento usa recurso do PostgreSQL que o H2 não tem. Teste no banco que vai para produção.

### Bug novo, teste novo

A partir de agora, todo bug que você corrigir **nasce com um teste que o reproduz**. Primeiro o teste falha, depois você corrige, depois ele passa. Faça isso pelo menos uma vez de propósito: quebre uma regra, escreva o teste, conserte.

## Pronto quando

- [ ] `mvn test` passa
- [ ] `mvn test` passa **sem PostgreSQL instalado** (só Docker)
- [ ] Todas as linhas das tabelas de regras têm teste
- [ ] Todo endpoint tem teste de sucesso e de erro
- [ ] Nenhum teste depende de ordem de execução
- [ ] Os nomes dos testes descrevem comportamento
- [ ] Você consegue quebrar uma regra de propósito e ver pelo menos um teste falhar

## Dicas

- Se você não consegue testar uma classe sem subir o banco, a classe tem dependência demais. Isso é sinal de projeto, não de teste.
- `@WebMvcTest` não sobe o banco. O service entra como `@MockBean`.
- Valores de borda (`0,5`, `60`, `20`) pegam mais bug do que valores "do meio".
- Teste a mensagem de erro exata. Ela faz parte do contrato.

## Erros comuns

- Teste que repete o código de produção (testa que `a + b` é `a + b`).
- `assertTrue(true)` ou teste sem `assert`.
- Teste que só passa na sua máquina porque depende do seu banco local.
- Cobrir só o caminho feliz.

## Ideia de solução

Branch [`ideia/passo-5`](https://github.com/karilho/cadastro-pet-2/tree/ideia/passo-5).

## Commit sugerido

```
test: cover pet rules, service, controller and repository
```
