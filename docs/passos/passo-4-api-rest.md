# Passo 4: API REST com Spring Boot

Este é o coração do desafio. Você constrói o **backend** e o front pronto (`frontend/`) passa a funcionar com ele. Qualquer cliente HTTP (Postman, o front, outro serviço) usa o seu sistema.

## Antes de começar

- Passos 1 a 3 concluídos
- Guia, Parte 2, seção **Web em Java: criando API's REST com Spring Boot**: Spring, HTTP, MVC e frameworks web
- Contrato dos endpoints, formatos e erros: [04-contrato-da-api.md](../04-contrato-da-api.md)
- O front que vai consumir a API: [05-frontend.md](../05-frontend.md)
- Regras: [01-regras-de-negocio.md](../01-regras-de-negocio.md)

## O que entregar

- Aplicação Spring Boot 3 que sobe com `mvn spring-boot:run`
- Os 5 endpoints do contrato
- Camadas separadas: controller, service e repository
- Validação com Bean Validation
- Tratamento de erros centralizado
- CORS configurado para o front

## Requisitos

### Projeto e persistência

1. Transforme o projeto em Spring Boot 3 (starters `web`, `data-jpa`, `validation` e o driver do PostgreSQL).
2. Configure o banco pelas **variáveis de ambiente** (`spring.datasource.url=${DB_URL}` e as outras). Nada de senha no `application.properties`.
3. Troque o repositório JDBC por **Spring Data JPA**. Mapeie `Pet` (e `Endereco`) para as tabelas que **você** modelou no Passo 2.
4. Deixe o JPA **não** recriar as tabelas sozinho (`ddl-auto=validate` ou `none`). O `schema.sql` do Passo 2 continua sendo a fonte da verdade.
5. **Guarde o repositório JDBC do Passo 3** no projeto ou no histórico do Git. Compare os dois: o que o Spring gera por baixo do `save()`?

### Camadas

6. **Controller:** só recebe a requisição, chama o service e devolve o status. Nenhuma regra de negócio, nenhum acesso a banco.
7. **Service:** regras de negócio e orquestração.
8. **Repository:** só acesso a dados.
9. **DTOs** de entrada (`POST`, `PUT`) e de saída. A entidade nunca é devolvida direto na API.

### Endpoints

10. Implemente os cinco endpoints exatamente como em [04-contrato-da-api.md](../04-contrato-da-api.md#2-endpoints): rotas, status de sucesso e headers (`Location` no `POST`).
11. A busca com filtros (`GET /pets?...`) devolve os ids esperados da [tabela de exemplos](../04-contrato-da-api.md#22-get-pets-listar-e-buscar).
12. `PUT` nunca altera `tipo`, `sexo`, `id` nem `dataCadastro`.

### Validação

13. As regras viram **Bean Validation** nos DTOs (`@NotBlank`, `@Pattern`, `@DecimalMin`, `@DecimalMax`, `@Valid` e afins). As mensagens são as da [tabela padrão](../01-regras-de-negocio.md#4-mensagens-de-erro-padrão).
14. Regras que a anotação não cobre ficam no service (por exemplo, nome com pelo menos duas palavras, ou a diferença entre alterar e criar).
15. Lembre: `NÃO INFORMADO` não existe na API. Campo opcional vazio é `null`.

### Tratamento de erros

16. Crie **um** `@RestControllerAdvice` que trata todos os erros e devolve o [formato padrão](../04-contrato-da-api.md#3-formato-de-erro).
17. Use o status certo para cada caso (tabela de status do contrato).
18. Erro de validação lista **todos** os campos inválidos de uma vez.
19. `500` devolve mensagem genérica, nunca stack trace, e registra o detalhe no log.

### Integração com o front

20. Configure o **CORS** para a origem `http://localhost:5500` ([contrato, seção 4](../04-contrato-da-api.md#4-cors-o-front-não-é-a-mesma-origem-da-api)).
21. Suba o front (instruções em [05-frontend.md](../05-frontend.md)) e use-o para testar tudo o que você construiu.
22. Se o front mostrar um erro que o Postman não mostra, o problema está na integração (provavelmente CORS ou formato de resposta). Descubra pelo console do navegador.

## Pronto quando

- [ ] A aplicação sobe só com as variáveis de ambiente configuradas
- [ ] O front pronto cadastra, lista, busca, altera e exclui pets sem erro
- [ ] Os erros de validação da API aparecem embaixo de cada campo do formulário do front
- [ ] Todos os checks de [04-contrato-da-api.md, seção 4](../04-contrato-da-api.md#5-pronto-quando)
- [ ] Dá para fazer `POST`, `GET`, `PUT` e `DELETE` pelo Postman ou `curl`
- [ ] Um `POST` com peso `61`, sem cidade e com tipo `Elefante` devolve **três** erros de campo
- [ ] `GET /pets/999` devolve `404` no formato padrão
- [ ] `grep` no projeto não acha senha em nenhum arquivo versionado
- [ ] O controller não importa nenhuma classe de repository

## Dicas

- Deixe o Postman salvo com uma coleção de uma requisição por endpoint e por erro. Ela vira sua documentação viva.
- Busca sem acento e sem caixa: você já resolveu isso no SQL do Passo 2. Descubra como levar a mesma solução para o JPA (consulta própria com `@Query`, por exemplo) e compare com o que o Spring gera sozinho.
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
