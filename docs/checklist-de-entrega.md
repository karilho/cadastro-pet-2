# Checklist de entrega

Antes de dizer "terminei", passe por esta lista. Ela vale para o desafio inteiro, não só para um passo.

## Repositório

- [ ] O projeto compila e passa nos testes em uma **máquina limpa** (`git clone`, configurar `.env`, `mvn test`)
- [ ] `git status` limpo: nada esquecido, nada sobrando
- [ ] `.env` **não** está no repositório. `.env.example` está
- [ ] Nenhuma senha, chave ou URL de banco no código, nem em commits antigos
- [ ] `.gitignore` cobre `target/`, `.idea/` e arquivos de segredo
- [ ] Nenhum arquivo `.txt` de pet, nenhum `System.out.println` de depuração esquecido

## Commits

- [ ] Um commit por funcionalidade concluída
- [ ] Mensagens em [Conventional Commits](https://www.youtube.com/watch?v=sbK9h45Jc5o) (`feat:`, `fix:`, `refactor:`, `test:`, `docs:`)
- [ ] Mensagens curtas e descritivas, sem `"ajustes"` nem `"final"`
- [ ] Histórico que conta a evolução do projeto, passo a passo

## O seu README

O README do **seu** projeto precisa ter:

- [ ] O que o projeto faz, em duas ou três frases
- [ ] Requisitos (Java, Maven, MySQL)
- [ ] Como criar o banco e rodar `schema.sql` e `seed.sql`
- [ ] Quais variáveis de ambiente configurar
- [ ] Como rodar a aplicação e como rodar os testes
- [ ] Exemplos de chamada da API (`curl` ou coleção do Postman)
- [ ] Decisões que você tomou e **por quê** (ex.: Specifications ou `@Query`, onde mora cada regra)

## Regras de negócio

- [ ] Todas as linhas das tabelas de [01-regras-de-negocio.md](01-regras-de-negocio.md) funcionam
- [ ] As mensagens de erro são as da tabela padrão
- [ ] `NÃO INFORMADO` existe em um único lugar
- [ ] Tipo e sexo nunca são alterados

## Banco

- [ ] `schema.sql`, `seed.sql` e `queries.sql` rodam do zero
- [ ] As consultas devolvem os resultados esperados do [seed](03-modelo-de-dados.md#resultados-esperados)
- [ ] Nenhuma query concatena valor do usuário

## API

- [ ] Todos os endpoints seguem o [contrato](05-contrato-da-api.md)
- [ ] Todo erro usa o formato padrão
- [ ] `500` nunca vaza detalhe interno

## Testes

- [ ] `mvn test` passa sem MySQL
- [ ] Cada regra de negócio tem teste
- [ ] Cada endpoint tem teste de sucesso e de erro

## Para a entrevista

Você precisa conseguir explicar, sem olhar o código:

- [ ] Por que `PreparedStatement` e não concatenar texto
- [ ] O que o Spring Data JPA faz por baixo do `save()` (você implementou os dois)
- [ ] Por que o controller não fala com o banco
- [ ] Por que `NULL` no banco e `NÃO INFORMADO` só na tela
- [ ] A diferença entre `LIKE 'x%'` e `LIKE '%x%'` para o índice
- [ ] Por que `DECIMAL` e não `FLOAT`
