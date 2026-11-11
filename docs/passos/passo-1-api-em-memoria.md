# Passo 1: API completa, guardando em memória

Construa o backend inteiro, com os 5 endpoints do [README](../../README.md#endpoints-que-o-seu-backend-precisa-ter), **sem banco de dados**. Os pets ficam guardados em memória e somem quando a aplicação reinicia. É de propósito: neste passo você prova que a API cumpre o contrato e que o front funciona de ponta a ponta. O banco entra no Passo 2.

## Antes de começar

- Guia, Parte 2, seção **Web em Java: aprendendo a criar APIs REST com Spring Boot**, incluindo **Validação e tratamento de erros**
- Regras: [01-regras-de-negocio.md](../01-regras-de-negocio.md)
- Contrato: [04-contrato-da-api.md](../04-contrato-da-api.md)
- Front: [05-frontend.md](../05-frontend.md)

## O que entregar

- Projeto Maven com Spring Boot 3 (Java 21 ou superior) que sobe com `mvn spring-boot:run` na porta 8080
- Os 5 endpoints do README funcionando
- Camadas separadas: controller, service e repositório
- Validação de TODAS as regras de negócio, com as mensagens padrão
- Tratamento de erros centralizado, no formato do contrato
- CORS liberado para o front
- Testes mínimos (ver abaixo)

## Requisitos

### Projeto
1. Crie o projeto Maven você mesmo. Entenda cada linha do `pom.xml` (starters `web` e `validation`).
2. Organize os pacotes por responsabilidade (sugestão em [02, seção 4](../02-ambiente-e-banco.md#4-estrutura-de-pastas-sugerida)).

### Domínio
3. Crie `Pet`, `Endereco` e os ENUMs `TipoPet` (`CACHORRO`, `GATO`) e `SexoPet` (`MACHO`, `FEMEA`).
4. Campo opcional não informado é `null`. O texto `NÃO INFORMADO` não existe no backend.
5. Idade e peso são números exatos (`BigDecimal`).

### Camadas
6. **Controller:** só recebe a requisição, chama o service e devolve o status certo. Sem regra de negócio.
7. **Service:** regras que não cabem em anotação (tipo e sexo não mudam na alteração, texto vazio vira `null`).
8. **Repositório:** crie uma **interface** (`PetRepository`) e uma implementação **em memória** (um `Map` serve). O service só conhece a interface.
9. **DTOs** de entrada e de saída. A entidade não é exposta direto na API.

> O item 8 é o coração do desafio. No Passo 3 e no Passo 4 você vai criar **outras** implementações da mesma interface, e o controller e o service **não podem mudar**. Se mudarem, a separação ficou errada.

### Validação e erros
10. Regras com **Bean Validation** nos DTOs, mensagens da [tabela padrão](../01-regras-de-negocio.md#4-mensagens-de-erro-padrão).
11. Um único `@RestControllerAdvice` devolvendo SEMPRE o [formato de erro](../04-contrato-da-api.md#3-formato-de-erro).
12. `400` lista **todos** os campos inválidos de uma vez.
13. `500` nunca vaza stack trace.

### Busca
14. `GET /pets` com os filtros do contrato, combinados com E, ignorando maiúsculas, minúsculas e acentos no texto.
15. Pet com campo `null` nunca atende a um filtro daquele campo.

### Integração
16. CORS para `http://localhost:5500` ([contrato, seção 4](../04-contrato-da-api.md#4-cors-o-front-não-é-a-mesma-origem-da-api)).
17. Suba o front e use todas as telas.

### Testes mínimos deste passo
18. Um teste por regra de negócio no service ou na API (o restante fica para o Passo 5).
19. Pelo menos um teste de API (MockMvc) por endpoint, no caminho feliz e no de erro.

## Pronto quando

- [ ] `mvn spring-boot:run` sobe sem banco nenhum
- [ ] O front cadastra, lista, busca, altera e exclui sem erro
- [ ] Um cadastro com peso `61`, sem cidade e sem tipo mostra **três** erros no formulário
- [ ] `GET /pets/999` devolve `404` no formato padrão
- [ ] O controller não conhece a implementação em memória, só a interface
- [ ] `mvn test` verde

## Dicas

- Comece pelo `GET /pets` devolvendo lista vazia. Abra o front: a tela "Listar todos" já deve funcionar.
- Depois o `POST`. Depois a busca. Depois `PUT` e `DELETE`.
- Busca sem acento em Java: pesquise `java.text.Normalizer`.
- Select vazio do front chega como `""`. Descubra o que o Jackson faz com isso num enum.

## Erros comuns

- Colocar o `Map` direto no service ou no controller.
- `@ExceptionHandler(Exception.class)` engolindo tudo como `500`, inclusive o que é `400` ou `404`.
- Devolver a entidade direto e vazar campo interno.
- Liberar CORS para `*`.

## Ideia de solução

Depois de tentar: branch [`ideia/passo-1`](https://github.com/karilho/cadastro-pet-2/tree/ideia/passo-1). Se você travou de vez, pode **partir dela** para o Passo 2.

## Commit sugerido

```
feat: add pet api with in-memory storage
```
