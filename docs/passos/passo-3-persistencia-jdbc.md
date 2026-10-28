# Passo 3: Persistência com JDBC

Agora o Java conversa com o banco, sem framework. Você vai escrever o SQL e ver cada conexão abrindo e fechando.

## Antes de começar

- Passos 1 e 2 concluídos (domínio e banco com seed)
- Guia, Parte 2, seção **Maven + JDBC**: as aulas de JDBC do DevDojo
- Variáveis de ambiente configuradas: [02, seção 3](../02-ambiente-e-banco.md#3-configuração-por-variáveis-de-ambiente)
- Contrato da tabela: [03-modelo-de-dados.md](../03-modelo-de-dados.md)

## O que entregar

- Dependência do driver MySQL no `pom.xml`
- Classe responsável pela conexão com o banco
- Repositório de pets com JDBC puro
- Um `main` de demonstração que exercita cada operação

## Requisitos

### Conexão

1. Adicione o driver (`mysql-connector-j`) pelo Maven.
2. URL, usuário e senha vêm de **variáveis de ambiente** (`DB_URL`, `DB_USER`, `DB_PASSWORD`). Nada disso escrito no código nem no Git.
3. Se faltar variável de ambiente, o programa avisa qual faltou, em português, e não segue.
4. Falha de conexão vira uma exceção **técnica** (diferente das exceções de regra de negócio).

### Operações do repositório

| Operação | Comportamento |
|---|---|
| Salvar | Insere o pet e devolve o `id` gerado pelo banco |
| Buscar por id | Devolve o pet ou indica que não existe (sem devolver `null` solto) |
| Listar todos | Todos os pets, ordenados por `id` |
| Buscar por critério | Nome, raça, sexo, idade, peso, tipo e cidade, combinando com E (regras da [seção 5](../01-regras-de-negocio.md#5-busca-de-pets)) |
| Alterar | Atualiza todos os campos editáveis. **Nunca** altera `tipo`, `sexo`, `id` ou `data_cadastro` |
| Excluir | Remove por `id` e informa se algo foi removido |

### Regras de qualidade (reprovam se faltarem)

1. **`PreparedStatement` SEMPRE.** Nenhum valor concatenado dentro de SQL. Isso é SQL Injection.
2. **`try-with-resources`** em toda `Connection`, `PreparedStatement` e `ResultSet`. Conexão vazada é bug.
3. Campo `NULL` do banco vira `null` no `Pet`, nunca `0` nem texto vazio.
4. A busca por critério monta o SQL dinamicamente (só os filtros usados), mas **os valores continuam em parâmetros `?`**.
5. Busca por parte do nome usa `LIKE` com `%` no valor do parâmetro, sem concatenar na query.

### Regra nível 2 (não obrigatória)

1. Faça a alteração dentro de uma **transação**: `setAutoCommit(false)`, `commit()` quando der certo e `rollback()` quando falhar.
2. Prove que o `rollback` funciona: force um erro no meio e confirme que nada foi gravado.

## Pronto quando

- [ ] Salvar, buscar, listar, alterar e excluir funcionam contra o MySQL
- [ ] A busca por `siames`, `FLOR` e `silva` devolve os ids esperados do [seed](../03-modelo-de-dados.md#resultados-esperados)
- [ ] Salvar um pet com campos opcionais vazios grava `NULL`, e ler devolve `null`
- [ ] Tentar alterar tipo ou sexo não muda nada no banco
- [ ] Buscar um id que não existe não lança erro técnico
- [ ] `grep` no projeto não acha nenhuma senha nem URL de banco escrita no código
- [ ] Nenhuma query usa `+` para juntar valor do usuário

## Dicas

- Teste um bug de propósito: busque o nome `' OR '1'='1`. Com `PreparedStatement` nada de estranho acontece. Com concatenação, você vê o problema.
- `ResultSet.getBigDecimal` devolve `null` para coluna `NULL`. `getInt` e `getDouble` devolvem `0`. Pegadinha clássica.
- Para o `id` gerado, procure `Statement.RETURN_GENERATED_KEYS`.
- Separe: um método transforma `ResultSet` em `Pet`. Você vai usar em todas as buscas.

## Erros comuns

- Abrir uma conexão nova a cada operação sem fechar.
- Deixar a senha "temporariamente" no código e subir pro Git. O histórico guarda para sempre.
- Capturar `SQLException` e engolir (`catch` vazio).
- Misturar regra de negócio (idade máxima) dentro do repositório. A regra mora no domínio.

## Commit sugerido

```
feat: add jdbc pet repository
```
