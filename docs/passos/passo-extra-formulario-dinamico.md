# Passo EXTRA: Formulário dinâmico

Lembra do desafio extra do V1, em que o dono do abrigo criava perguntas novas? Agora ele vive no banco e na API. Este passo é **opcional** e é o mais difícil.

## Antes de começar

- Passos 1 a 6 concluídos (de preferência com testes passando)
- Guia, Parte 2, seção **SQL**: `JOIN`, chave estrangeira e relacionamentos 1:N e N:N
- Modelo das tabelas: [03-modelo-de-dados.md, seção 3](../03-modelo-de-dados.md#3-tabelas-do-passo-extra)

## O que entregar

- Tabelas `pergunta` e `resposta` no `schema.sql`
- Perguntas de 1 a 7 carregadas como **originais**
- Endpoints de perguntas
- Respostas extras dentro do cadastro e da consulta do pet

## Requisitos

### Banco

1. Crie as tabelas `pergunta` e `resposta` seguindo o [modelo](../03-modelo-de-dados.md#3-tabelas-do-passo-extra).
2. O par (`pet_id`, `pergunta_id`) é único.
3. Carregue no seed as 7 perguntas originais do [`formulario.txt`](../../formulario.txt), marcadas como `original`.

### Regras

1. As perguntas de 1 a 7 são **originais**: não podem ser alteradas nem excluídas.
2. O usuário só altera ou exclui as perguntas que ele mesmo adicionou.
3. Ao **excluir** uma pergunta, as seguintes sobem a numeração, sem buracos. Com 9 perguntas, ao excluir a 8, a 9 vira a 8.
4. Ao **criar**, a nova pergunta recebe a numeração seguinte (a anterior + 1).
5. Pergunta nova **não é obrigatória**. Sem resposta, o valor é `NÃO INFORMADO` na exibição e `NULL` no banco.
6. Excluir uma pergunta exclui as respostas dela.
7. Ao alterar uma pergunta, as respostas continuam valendo (muda só o texto da pergunta).

### API

Defina os endpoints, mantendo o padrão do [contrato](../05-contrato-da-api.md):

| Método | Rota | O que faz |
|---|---|---|
| `GET` | `/perguntas` | Lista todas, ordenadas por `numero` |
| `POST` | `/perguntas` | Cria uma pergunta nova (`201`) |
| `PUT` | `/perguntas/{id}` | Altera o texto de uma pergunta que **não** seja original |
| `DELETE` | `/perguntas/{id}` | Exclui uma pergunta que **não** seja original (`204`) |

- Mexer em pergunta original devolve `400`, com uma mensagem clara, no formato padrão de erro.
- O `POST /pets` aceita uma lista opcional de respostas extras, e o `GET /pets/{id}` devolve essas respostas.

Formato sugerido para as respostas extras no pet:

```json
{
  "respostasExtras": [
    { "perguntaId": 8, "resposta": "Gosta de crianças" },
    { "perguntaId": 9, "resposta": null }
  ]
}
```

## Pronto quando

- [ ] As 7 originais existem e não podem ser alteradas nem excluídas
- [ ] Criar, alterar e excluir pergunta funcionam
- [ ] Excluir a pergunta 8 de 9 faz a 9 virar 8
- [ ] Resposta em branco vira `NÃO INFORMADO` (exibição) e `NULL` (banco)
- [ ] Excluir uma pergunta não deixa respostas órfãs
- [ ] Tudo isso tem teste

## Dicas

- Renumerar não é só `UPDATE`: pense em fazer isso numa transação para não deixar a numeração pela metade.
- Escreva primeiro as consultas SQL que respondem "quais respostas tem este pet, com o texto da pergunta?". O `JOIN` aparece aqui de verdade.
- Decida se a numeração é guardada ou calculada. Cada opção tem custo.

## Commit sugerido

```
feat: add dynamic form questions and answers
```
