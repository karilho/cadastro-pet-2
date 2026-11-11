# Extra: Formulário dinâmico

Lembra do desafio extra do V1, em que o dono do abrigo criava perguntas novas? Aqui a sugestão é levar isso pro banco. É um extra **em aberto**: sem requisito fechado e sem solução pronta. Você decide o escopo, integra na API do seu jeito e faz o front se quiser.

## Antes de começar

- Passos 1 a 5 concluídos (de preferência com testes passando)
- Guia, Parte 2, seção **SQL**: `JOIN`, chave estrangeira e relacionamentos 1:N e N:N
- Modelo: [03-modelo-de-dados.md, seção 6](../03-modelo-de-dados.md#6-extra-perguntas-e-respostas)

## A ideia

Guardar no banco as perguntas do formulário e as respostas de cada pet. As 7 perguntas do V1 entram como **originais**:

```
1 - Qual o nome e sobrenome do pet?
2 - Qual o tipo do pet (Cachorro/Gato)?
3 - Qual o sexo do animal?
4 - Qual endereço e bairro que ele foi encontrado?
5 - Qual a idade aproximada do pet?
6 - Qual o peso aproximado do pet?
7 - Qual a raça do pet?
```

## Ideias pra explorar

- Tabelas `pergunta` e `resposta` no `schema.sql`, com as 7 originais no seed.
- Um pet responde cada pergunta no máximo uma vez, e o banco garante isso.
- Perguntas originais não podem ser alteradas nem excluídas.
- Pergunta nova não é obrigatória: sem resposta, `NULL` no banco.
- Excluir uma pergunta exclui as respostas dela.
- Endpoints pra listar, criar, alterar e excluir perguntas, seguindo o padrão do [contrato](../04-contrato-da-api.md).
- Respostas extras no cadastro e na consulta do pet.

## Dicas

- Escreva primeiro a consulta SQL que responde "quais respostas tem este pet, com o texto da pergunta?". O `JOIN` aparece aqui de verdade.
- Se quiser numeração sem buracos ao excluir, faça a renumeração numa transação.

## Commit sugerido

```
feat: add dynamic form questions and answers
```
