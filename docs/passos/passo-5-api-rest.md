# Passo 5: API REST com Spring Boot

O terminal sai de cena. Agora qualquer cliente HTTP (Postman, um front-end, outro serviço) usa o seu sistema.

## Antes de começar

- Passos 1 a 4 concluídos
- Guia, Parte 2, seção **Web em Java: criando API's REST com Spring Boot**: Spring, HTTP, MVC e frameworks web
- Contrato dos endpoints, formatos e erros: [05-contrato-da-api.md](../05-contrato-da-api.md)
- Regras: [01-regras-de-negocio.md](../01-regras-de-negocio.md)

## O que entregar

- Aplicação Spring Boot 3 que sobe com `mvn spring-boot:run`
- Os 5 endpoints do contrato
- Camadas separadas: controller, service e repository
- Validação com Bean Validation
- Tratamento de erros centralizado

## Requisitos

### Projeto e persistência

1. Transforme o projeto em Spring Boot 3 (starters `web`, `data-jpa`, `validation` e o driver do MySQL).
2. Configure o banco pelas **variáveis de ambiente** (`spring.datasource.url=${DB_URL}` e as outras). Nada de senha no `application.properties`.
3. Troque o repositório JDBC por **Spring Data JPA**. Mapeie `Pet` (e `Endereco`) para a tabela `pet` do [contrato de colunas](../03-modelo-de-dados.md#contrato-das-colunas).
4. Deixe o JPA **não** recriar as tabelas sozinho (`ddl-auto=validate` ou `none`). O `schema.sql` do Passo 2 continua sendo a fonte da verdade.
5. **Guarde o repositório JDBC do Passo 3** no projeto ou no histórico do Git. Compare os dois: o que o Spring gera por baixo do `save()`?

### Camadas

6. **Controller:** só recebe a requisição, chama o service e devolve o status. Nenhuma regra de negócio, nenhum acesso a banco.
7. **Service:** regras de negócio e orquestração.
8. **Repository:** só acesso a dados.
9. **DTOs** de entrada (`POST`, `PUT`) e de saída. A entidade nunca é devolvida direto na API.

### Endpoints

10. Implemente os cinco endpoints exatamente como em [05-contrato-da-api.md](../05-contrato-da-api.md#2-endpoints): rotas, status de sucesso e headers (`Location` no `POST`).
11. A busca com filtros (`GET /pets?...`) devolve os ids esperados da [tabela de exemplos](../05-contrato-da-api.md#22-get-pets-listar-e-buscar).
12. `PUT` nunca altera `tipo`, `sexo`, `id` nem `dataCadastro`.

### Validação

13. As regras viram **Bean Validation** nos DTOs (`@NotBlank`, `@Pattern`, `@DecimalMin`, `@DecimalMax`, `@Valid` e afins). As mensagens são as da [tabela padrão](../01-regras-de-negocio.md#4-mensagens-de-erro-padrão).
14. Regras que a anotação não cobre ficam no service (por exemplo, nome com pelo menos duas palavras, ou a diferença entre alterar e criar).
15. Lembre: `NÃO INFORMADO` não existe na API. Campo opcional vazio é `null`.

### Tratamento de erros

16. Crie **um** `@RestControllerAdvice` que trata todos os erros e devolve o [formato padrão](../05-contrato-da-api.md#3-formato-de-erro).
17. Use o status certo para cada caso (tabela de status do contrato).
18. Erro de validação lista **todos** os campos inválidos de uma vez.
19. `500` devolve mensagem genérica, nunca stack trace, e registra o detalhe no log.

## Pronto quando

- [ ] A aplicação sobe só com as variáveis de ambiente configuradas
- [ ] Todos os checks de [05-contrato-da-api.md, seção 4](../05-contrato-da-api.md#4-pronto-quando)
- [ ] Dá para fazer `POST`, `GET`, `PUT` e `DELETE` pelo Postman ou `curl`
- [ ] Um `POST` com peso `61`, sem cidade e com tipo `Elefante` devolve **três** erros de campo
- [ ] `GET /pets/999` devolve `404` no formato padrão
- [ ] `grep` no projeto não acha senha em nenhum arquivo versionado
- [ ] O controller não importa nenhuma classe de repository

## Dicas

- Deixe o Postman salvo com uma coleção de uma requisição por endpoint e por erro. Ela vira sua documentação viva.
- Busca sem acento e sem caixa: o MySQL com a collation do banco já ajuda. Descubra como o JPA monta a query, ou escreva a sua com `@Query`. Compare.
- Para buscas com vários filtros opcionais, procure por **Specifications** ou por consultas derivadas. Escolha uma e justifique no seu README.
- Um erro de JSON malformado e um erro de validação são erros **diferentes** no Spring. Os dois precisam cair no seu `@RestControllerAdvice`.

## Erros comuns

- Expor a entidade JPA direto no controller (e vazar campo que não devia).
- Regra de negócio dentro do controller.
- `@ExceptionHandler(Exception.class)` engolindo tudo como `500`, inclusive o que deveria ser `404`.
- Deixar `ddl-auto=create` e perder os dados do seed a cada reinício.
- Devolver `200` com mensagem de erro no corpo.

## Commit sugerido

```
feat: add pet rest api with spring boot
```
